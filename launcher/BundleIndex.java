package boinsoft.atosgi.launcher;

import io.vavr.control.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.*;
import java.util.stream.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.*;
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
  public static record PendingBundles(String source, String URI) {}

  public List<PendingBundles> pendingBundles;
  public Map<String, Integer> startLevels;
  public Path path;
  public String folder;
  public String indexName;

  public BundleIndex(Path p) {
    path = p;
    pendingBundles = new ArrayList<>();
    startLevels = new HashMap<>();
    folder = path.getParent().getFileName().toString();
    indexName = path.getFileName().toString().replace(".index", "");
  }

  public List<Try<Bundle>> install(BundleContext ctx, int defaultStartLevel) {
    return (List<Try<Bundle>>)
        pendingBundles.stream()
            .map(
                (pending) -> {
                  return Try.of(
                      () -> {
                        Bundle b = ctx.installBundle(pending.URI);
                        b.adapt(BundleStartLevel.class).setStartLevel(defaultStartLevel);
                        for (var e : startLevels.keySet()) {
                          if (b.getSymbolicName().startsWith(e)) {
                            b.adapt(BundleStartLevel.class).setStartLevel(startLevels.get(e));
                          }
                        }
                        return b;
                      });
                })
            .collect(Collectors.toList());
  }

  void processLine(String line) {
    if (line.startsWith("bundle:")) {
      pendingBundles.add(
          new PendingBundles(
              indexName, "file:" + line.substring("bundle:".length(), line.length())));
    } else if (line.startsWith("startlevel:")) {
      String[] tokens = line.substring("startlevel:".length(), line.length()).split("=");
      startLevels.put(tokens[0], Integer.parseInt(tokens[1]));
    }
    // TODO: handle index: for referencing other indices
    // TODO: refine exceptions
  }

  public static Try<BundleIndex> parse(Path p) {
    return Try.of(
        () -> {
          BundleIndex idx = new BundleIndex(p);
          Files.lines(p)
              .forEach(
                  line -> {
                    idx.processLine(line);
                  });
          return idx;
        });
  }
}
