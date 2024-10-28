package boinsoft.atosgi.launcher;

import io.vavr.control.*;
import java.io.*;
import java.net.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
 * then tries to find bundles embedded within the launcher.
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

  public static void main(String[] args) {
    info("atosgi-launcher");
    info("== CWD: " + Path.of("").toAbsolutePath().toString());
    try {

      // TODO: load tmp from env
      Path tmp = FileSystems.getDefault().getPath("/tmp");
      Path cache = Files.createTempDirectory(tmp, "atosgi-cache");

      Map<String, String> configProps =
          Map.of(
              "felix.log.level",
              "2",
              "org.osgi.framework.storage.clean",
              "onFirstInit",
              "org.osgi.framework.storage",
              cache.toString());

      framework = getFrameworkFactory().newFramework(configProps);
      framework.init();

      // build a Classpath Filesystem we can re-use when we have multiple bundle
      // folders to load from
      ClassLoader cl = ClassLoader.getSystemClassLoader();
      URI uri = cl.getResource("META-INF/MANIFEST.MF").toURI();
      FileSystem cpFileSystem =
          FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
      Path p = cpFileSystem.getPath("META-INF", "MANIFEST.MF");

      // TODO: delete all XManifest usage with something else. File is empty on bazel run :...
      XManifest manifest = new XManifest(Files.newInputStream(p));
      var defaultStartLevel = manifest.getInt("Atosgi-StartLevel", 20).get();
      var sleepIntervalMs = manifest.getInt("Atosgi-SleepIntervalMs", 100).get();

      // find and install all the bundles embedded in our super-JAR
      BundleContext ctx = framework.getBundleContext();
      List<Bundle> bundles = new ArrayList<Bundle>();

      Map<String, Prefix> bundlePrefixes =
          manifest
              .getString("Atosgi-BundlePrefix")
              .orElse(Option.of("bundles/=10"))
              .map(Main::resolveBundlePrefixes)
              .get();

      for (Prefix prefix : bundlePrefixes.values()) {
        List<String> bundlePaths = getClasspathBundles(prefix, cpFileSystem);
        for (String s : bundlePaths) {
          if (!s.endsWith(".jar")) {
            continue;
          }
          InputStream is = Main.class.getClassLoader().getResourceAsStream(s);
          Bundle b = ctx.installBundle(s.toString(), is);
          b.adapt(BundleStartLevel.class).setStartLevel(prefix.startLevel());
          bundles.add(b);
        }
      }

      // find and install all the bundles in our JAVA_RUNFILES manifest. This is for
      // running via bazel run :target but we should eventually use it instead of
      // prefix scanning in our classpath.
      List<Path> manifests = resolveAtosgiManifests();
      for (Path atm : manifests) {
        var fileReader = Files.newBufferedReader(atm, StandardCharsets.UTF_8);
        while (fileReader.ready()) {
          String line = fileReader.readLine();
          String folder = atm.getParent().getFileName().toString();
          Bundle b = ctx.installBundle("file://" + atm.getParent().toString() + "/" + line);
          var prefix = bundlePrefixes.get(folder);
          if (prefix == null) {
            prefix = bundlePrefixes.get(folder + "/");
          }
          if (prefix != null) {
            b.adapt(BundleStartLevel.class).setStartLevel(prefix.startLevel());
          } else {
            b.adapt(BundleStartLevel.class).setStartLevel(defaultStartLevel);
          }

          bundles.add(b);
        }
        fileReader.close();
      }

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

      info("cleaning up tmp " + cache.toString());
      try (var dirStream = Files.walk(cache)) {
        dirStream.map(Path::toFile).sorted(Comparator.reverseOrder()).forEach(File::delete);
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

  private static List<Path> resolveAtosgiManifests() throws IOException {
    Map<String, String> env = System.getenv();
    if (env.get("JAVA_RUNFILES") == null) {
      return new ArrayList<Path>();
    }
    String file = env.get("JAVA_RUNFILES") + "/MANIFEST";
    Path manifestFile = FileSystems.getDefault().getPath(file);

    List<Path> manifests = new ArrayList<Path>();
    BufferedReader fileReader = Files.newBufferedReader(manifestFile, StandardCharsets.UTF_8);
    while (fileReader.ready()) {
      String line = fileReader.readLine();
      String[] parts = line.split(" ");
      String name = parts[0];
      if (name.endsWith("_ATOSGI_MANIFEST.MF")) {
        manifests.add(FileSystems.getDefault().getPath(parts[1]));
      }
    }
    fileReader.close();

    // just a quirk of how the build and manifest file works, order is in reverse from:
    // 1. most generic/base items.
    // 2. less generic ones passed into launcher.
    // 3. your own targets.
    Collections.reverse(manifests);
    return manifests;
  }

  private static Map<String, Prefix> resolveBundlePrefixes(String prefixes) {
    return Arrays.stream(prefixes.split(","))
        .map(Prefix::parse)
        .collect(Collectors.toMap(p -> p.folder, p -> p));
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