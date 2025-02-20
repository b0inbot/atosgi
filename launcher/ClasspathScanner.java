package boinsoft.atosgi.launcher;

import io.vavr.control.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.*;

/** Utility class for taking in a jar:file:file.jar!path/to/dir/ type URI and scanning into it */
public class ClasspathScanner {
  private final String input_;

  public ClasspathScanner(String input) {
    this.input_ = input;
  }

  // code adapted from a few useful stackoverflow answers
  //  https://stackoverflow.com/a/62412693
  //  https://stackoverflow.com/a/22605905
  //  TODO: this just spits out index names. we need a more generic one, given the name
  public List<String> scan() throws Exception {
    final Map<String, String> env = new HashMap<>();
    final var uri = getClass().getResource(input_);
    final var array = uri.toString().split("!");
    List<String> ret = new ArrayList<>();

    try (FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env)) {
      final Path path = fs.getPath(array[1]);

      try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
        for (Path p : dirStream) {
          String name = p.getFileName().toString();
          if (name.endsWith(".index")) {
            ret.add(name.substring(0, name.length() - 6));
          }
        }
      }
    }
    return ret;
  }
}
