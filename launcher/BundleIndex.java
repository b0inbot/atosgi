package boinsoft.atosgi.launcher;

import io.vavr.control.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.*;
import java.util.stream.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.startlevel.BundleStartLevel;

/**
 * A BundleIndex contains the list of bundles, start levels, and dependent bundle indexes that
 * constitute a deployable group of OSGI bundles.
 *
 * <p>Working spec: line starts with bundle:, startlevel:, or index:
 *
 * <p>bundle: - defines a jar to load startlevel: - defines a start level symbol=int index: -
 * defines another index to load
 *
 * <p>start levels are compared against the bundles. for each bundle: for each startlevel: if
 * bundle.symbolicName begins with startlevelname set start level of bundle
 */
public class BundleIndex {
  public static record PendingBundle(String filename) {}

  public List<PendingBundle> pendingBundles;
  public Map<String, Integer> startLevels;

  public BundleIndex() throws URISyntaxException {
    pendingBundles = new ArrayList<>();
    startLevels = new HashMap<>();
  }

  protected Bundle installBundle(BundleContext ctx, int defaultStartLevel, PendingBundle pending)
      throws BundleException, IOException {
    // TODO: the location of the bundle should be relative to the
    // source of the index. Only in some cases will the location
    // be within the classpath. This requires some smarter resolution
    // engine.
    var is = getClass().getClassLoader().getResourceAsStream("autoinstall.d/" + pending.filename);
    Bundle b = ctx.installBundle("classpath://autoinstall.d/" + pending.filename, is);
    b.adapt(BundleStartLevel.class).setStartLevel(defaultStartLevel);
    for (var e : startLevels.keySet()) {
      if (b.getSymbolicName().startsWith(e)) {
        b.adapt(BundleStartLevel.class).setStartLevel(startLevels.get(e));
      }
    }
    return b;
  }

  public List<Try<Bundle>> install(BundleContext ctx, int defaultStartLevel) {
    return (List<Try<Bundle>>)
        pendingBundles.stream()
            .map((pending) -> Try.of(() -> installBundle(ctx, defaultStartLevel, pending)))
            .collect(Collectors.toList());
  }

  void processLine(String line) {
    if (line.startsWith("bundle:")) {
      pendingBundles.add(new PendingBundle(line.substring("bundle:".length(), line.length())));
    } else if (line.startsWith("startlevel:")) {
      String[] tokens = line.substring("startlevel:".length(), line.length()).split("=");
      startLevels.put(tokens[0], Integer.parseInt(tokens[1]));
    }
    // TODO: handle index: for referencing other indices
    // TODO: refine exceptions
  }

  public static Try<BundleIndex> parseTry(URL url) {
    return Try.of(
        () -> {
          BundleIndex idx = new BundleIndex();
          var r = new BufferedReader(new InputStreamReader(url.openStream()));
          while (r.ready()) idx.processLine(r.readLine());
          return idx;
        });
  }
}
