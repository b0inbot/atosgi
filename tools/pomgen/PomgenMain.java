package boinsoft.atosgi.tools.pomgen;

import io.vavr.control.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.jar.*;
import java.util.stream.*;

/**
 * Pomgen is a simple program for generating a Maven POM out of our bazel repo.
 *
 * <p>Importantly, it is currently only for generating the POMs for listing the dependencies in a
 * format security scanners can see.
 */
public class PomgenMain {

  static int FIND_PROJECT_ROOT_MAXDEPTH = 3;

  static String TEMPLATE =
      """
<dependency>
  <groupId>{0}</groupId>
  <artifactId>{1}</artifactId>
  <version>{2}</version>
</dependency>
  """;

  public static void info(String msg) {
    System.err.printf("[INFO] %s\n", msg);
  }

  public static Option<Path> resolveProjectRoot() {
    var env = System.getenv();
    var buildWorkDir = env.get("BUILD_WORKING_DIRECTORY");
    Path dir;

    if (buildWorkDir != null && !buildWorkDir.equals("")) {
      dir = Paths.get(buildWorkDir);
    } else {
      dir = Path.of("").toAbsolutePath();
    }
    for (int x = 0; x != FIND_PROJECT_ROOT_MAXDEPTH; x++) {
      info("Looking for project root (MODULE.bazel) in " + dir.toString());
      if (Files.exists(dir.resolve("MODULE.bazel"))) {
        return Option.of(dir.toAbsolutePath().normalize());
      }
      dir = dir.resolve("../");
    }

    return Option.none();
  }

  static class LineProcessor {
    boolean recording;
    List<String> items;

    LineProcessor() {
      this.recording = false;
      this.items = new LinkedList<String>();
    }

    void onLine(String s) {
      if (this.recording) {
        if (s.strip().equals("# END-pom.xml")) {
          this.recording = false;
          return;
        }
        items.add(s.strip().replaceAll("^\"|\",$", ""));
      } else {
        if (s.strip().equals("# START-pom.xml")) {
          this.recording = true;
          return;
        }
      }
    }
  }

  public static void main(String[] args) throws IOException {
    info("pomgen");
    Path cwd = Path.of("").toAbsolutePath();
    var rootOpt = resolveProjectRoot();

    if (rootOpt.isEmpty()) {
      info("Can't find project root");
      System.exit(1);
    }

    var root = rootOpt.get();

    // existence of MODULE.bazel already checked in resolveProjectRoot
    var moduleBazel = root.resolve("MODULE.bazel");

    var pomOutput = root.resolve("pom.xml");

    var pomStart = root.resolve("tools/pomgen/pom-start.xmlfrag");
    var pomEnd = root.resolve("tools/pomgen/pom-end.xmlfrag");

    try (var out = Files.newOutputStream(pomOutput);
        var pomStartIn = Files.newInputStream(pomStart);
        var pomEndIn = Files.newInputStream(pomEnd)) {

      pomStartIn.transferTo(out);

      var lp = new LineProcessor();
      Files.lines(moduleBazel).sequential().forEach(v -> lp.onLine(v));
      // order doesnt really matter here other than keeping it in the same order
      // as the MODULE.bazel is "nicer"
      out.write(
          lp.items.stream()
              .sequential()
              .map(x -> x.split(":"))
              .map(items -> MessageFormat.format(TEMPLATE, items[0], items[1], items[2]))
              .reduce("", (v, buf) -> v + buf)
              .getBytes());

      pomEndIn.transferTo(out);
    }
  }
}
