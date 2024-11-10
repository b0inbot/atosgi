package boinsoft.atosgi.tools.zipoverlay;

import io.vavr.control.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.*;
import java.util.zip.*;

record ReadableZipEntry(ZipFile zf, ZipEntry ze) {
  public InputStream getInputStream() throws IOException {
    return zf.getInputStream(ze);
  }

  public String name() {
    return ze.getName();
  }

  public boolean isDirectory() {
    return ze.isDirectory();
  }
}

/**
 * Utility class which takes in a series of Path objects which point to ZIP files and generates a
 * stream of ReadableZipEntries, for reading each item.
 */
class ManyZipEntryStream implements AutoCloseable {
  List<Try<ZipFile>> inputs;

  public ManyZipEntryStream(List<Path> paths) {
    this.inputs =
        paths.stream().map(Try::success).map(x -> x.mapTry(p -> new ZipFile(p.toFile()))).toList();
  }

  public Stream<ReadableZipEntry> stream() throws IOException {
    return this.inputs.stream()
        .map(Try::get)
        .flatMap(zf -> zf.stream().map(z -> new ReadableZipEntry(zf, z)));
  }

  public void close() throws IOException {
    this.inputs.stream().forEach(x -> x.andThenTry(f -> f.close()));
  }
}
