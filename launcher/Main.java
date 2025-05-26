package boinsoft.atosgi.launcher;

import io.vavr.control.*;
import java.io.*;
import java.net.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.*;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;

/**
 * Main is the entrypoint for launching the atosgi launcher.
 *
 * <p>It launches OSGI, sets up cache in a temporary directory, performs some initial configuration,
 * then tries to find bundles embedded within the launcher or within the bazel runtime files
 */
public class Main {

  protected record Prefix(String folder, int startLevel) {
    static Prefix parse(String s) {
      String[] items = s.trim().split("=");
      if (items.length == 1) {
        // TODO: load default start level from config
        return new Prefix(items[0], 10);
      }
      return new Prefix(items[0], Integer.parseInt(items[1]));
    }
  }

  public static void info(String msg) {
    System.out.printf("[INFO] %s\n", msg);
  }

  private static Framework framework = null;

  public static Map<String, String> resolveConfigProps(Path cache) throws IOException {
    Map<String, String> configProps =
        new HashMap<>(
            Map.of(
                "felix.log.level",
                "2",
                "org.osgi.framework.storage.clean",
                "onFirstInit",
                "org.osgi.framework.storage",
                cache.toString(),
                "org.osgi.framework.system.packages.extra",
                "javax.*,org.xml.sax,org.xml.sax.helpers",
                "org.osgi.framework.bootdelegation",
                "javax.*,org.xml.sax,org.xml.sax.helpers"));

    // use config.properties in classpath to override defaults
    var is = Main.class.getClassLoader().getResourceAsStream("config.properties");
    if (is == null) {
      return configProps;
    }
    var prop = new Properties();
    prop.load(is);
    for (String key : prop.stringPropertyNames()) {
      configProps.put(key, prop.getProperty(key).toString());
    }

    return configProps;
  }

  public static void main(String[] args) throws IOException {
    Path cwd = Path.of("").toAbsolutePath();
    Path cache;
    // TODO: cache should be more configerable
    boolean cleanupCache = true;

    if (args.length == 0) {
      // TODO: load tmp from env
      Path tmp = FileSystems.getDefault().getPath("/tmp");
      cache = Files.createTempDirectory(tmp, "atosgi-cache");
    } else {
      cache = Path.of(args[0]);
      cleanupCache = false;
    }

    // TODO: even when this is off, if the gogo bundles have been previously loaded
    // into the cache, then this has no effect. We essentually need a way of
    // marking a series of bundles as provides feature "interactive shell" and then
    // turn that feature off before starting up.
    boolean nonInteractive = System.getProperty("nonInteractive") != null;
    boolean loadCwdBundles = System.getProperty("loadCwdBundles") != null;

    info("atosgi-launcher");
    info("== CWD: " + cwd.toString());
    info("== Cache: " + cache.toString());
    info("== Interactive: " + Boolean.toString(!nonInteractive));
    info("== Load Bundles from CWD: " + Boolean.toString(loadCwdBundles));

    try {

      Map<String, String> configProps = resolveConfigProps(cache);

      // the config specifies which bundle groups to try to load.
      String bundleGroupsData = configProps.get("atosgi.autoinstall.bundle-groups");
      List<String> bundleGroups;
      if (bundleGroupsData == null) {
        ClasspathScanner scanner = new ClasspathScanner("/autoinstall.d");
        bundleGroups = scanner.scan();
        if (nonInteractive) {
          bundleGroups = bundleGroups.stream().filter(x -> !x.equals("gogo")).toList();
        }
      } else {
        bundleGroups =
            Arrays.asList(bundleGroupsData.split(",")).stream().map(String::trim).toList();
      }

      var sleepIntervalMs = 100; // TODO: load from atosgi.* property
      var defaultStartLevel = 50; // TODO: load from atosgi.* property

      // each index in this list is a list of bundles that make up a single bundle group
      var indices = resolveIndexes(bundleGroups);

      // run!
      framework = getFrameworkFactory().newFramework(configProps);
      framework.init();

      // find and install all the bundles embedded in our super-JAR
      BundleContext ctx = framework.getBundleContext();
      List<Bundle> bundles = new ArrayList<Bundle>();

      for (var tryIndex : indices) {
        var index = tryIndex.get();
        var l = index.install(ctx, defaultStartLevel);
        bundles.addAll(l.stream().map(Try::get).toList());
      }

      // find and install bundles in the current working directory
      if (loadCwdBundles) {
        File dir = new File(".");
        File[] files = dir.listFiles();
        for (File f : files) {
          if (f.getName().endsWith(".jar")) {
            var bundle = ctx.installBundle("file:" + f.getPath());
            bundles.add(bundle);
            bundle.adapt(BundleStartLevel.class).setStartLevel(defaultStartLevel);
          }
        }
      }

      // start the OSGI framework
      framework.start();

      // start each bundle
      for (Bundle b : bundles) {
        b.start();
      }
      List<Integer> startLevels = resolveStartLevels(defaultStartLevel);

      // start slowly increasing the start level
      for (int x : startLevels) {
        ctx.getBundle().adapt(FrameworkStartLevel.class).setStartLevel(x);
        Thread.sleep(sleepIntervalMs);
      }

      framework.waitForStop(0);

      if (cleanupCache) {
        info("cleaning up tmp " + cache.toString());
        try (var dirStream = Files.walk(cache)) {
          dirStream.map(Path::toFile).sorted(Comparator.reverseOrder()).forEach(File::delete);
        }
      }
    } catch (Exception ex) {
      System.err.println("Could not create framework: " + ex);
      ex.printStackTrace();
      System.exit(-1);
    }
  }

  private static FrameworkFactory getFrameworkFactory() throws Exception {
    java.net.URL url =
        Main.class
            .getClassLoader()
            .getResource("META-INF/services/org.osgi.framework.launch.FrameworkFactory");
    if (url != null) {
      BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
      try {
        for (String s = br.readLine(); s != null; s = br.readLine()) {
          s = s.trim();
          // Try to load first non-empty, non-commented line.
          if ((s.length() > 0) && (s.charAt(0) != '#')) {
            return (FrameworkFactory) Class.forName(s).newInstance();
          }
        }
      } finally {
        if (br != null) br.close();
      }
    }

    throw new Exception("Could not find framework factory.");
  }

  //
  // OSGI starts at start-level 1 in our workflow and instead of
  // just jumping straight to start-level N we jump in incremenets
  // of some number. For... reasons? I can't remember why I did this
  // but I like it.
  private static List<Integer> resolveStartLevels(int finalStartLevel) {
    if (finalStartLevel < 20) {
      throw new RuntimeException("Atosgi-StartLevel must be above 20");
    }

    List<Integer> al = new ArrayList<Integer>();
    al.add(1);
    for (int x = 1; x < finalStartLevel; x += 10) {
      al.add(x);
    }
    al.add(finalStartLevel);
    return al;
  }

  private static <T> T peekPrint(T t) {
    System.out.println("==== " + t);
    return t;
  }

  private static List<Try<BundleIndex>> resolveIndexes(List<String> groups) throws IOException {
    ClassLoader cl = Main.class.getClassLoader();
    // TODO: we should be able to resolve indexes from arbitrary locations: http://, classpath://,
    // etc.
    return groups.stream()
        .map(
            group -> Optional.ofNullable((URL) cl.getResource("autoinstall.d/" + group + ".index")))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(BundleIndex::parseTry)
        .collect(Collectors.toList());
  }

  private static List<String> getClasspathBundles(Prefix prefix, FileSystem fileSystem)
      throws IOException, URISyntaxException {
    List<String> items = new ArrayList<String>();
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    List<URL> urls = Collections.list(cl.getResources(prefix.folder()));
    for (URL url : urls) {
      URI u = url.toURI();
      Path p = fileSystem.getPath("/" + prefix.folder());
      Stream<Path> walk = Files.walk(p, 1);
      for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
        // TODO: normalize path
        items.add(u.toString() + it.next().getFileName().toString());
      }
    }
    return items;
  }
}
