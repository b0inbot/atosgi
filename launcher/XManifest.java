package boinsoft.atosgi.launcher;

import io.vavr.control.Option;
import io.vavr.control.Try;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.*;

/** Simpler interface to java.util.jar.Manifest class. */
public class XManifest {
  public XManifest(InputStream is) throws IOException {
    this.manifest_ = new Manifest(is);
    this.mainAttributes_ = this.manifest_.getMainAttributes();
  }

  private final Manifest manifest_;
  private final Attributes mainAttributes_;

  public Attributes getMainAttributes() {
    return this.mainAttributes_;
  }

  public Option<String> getString(String key) {
    return Option.of((String) mainAttributes_.getValue(key));
  }

  public String getString(String key, String defaultValue) {
    return Option.of((String) mainAttributes_.getValue(key)).getOrElse(defaultValue);
  }

  public Try<Option<Integer>> getInt(String key) {
    return Try.of(
        () -> {
          return getString(key).map(Integer::parseInt);
        });
  }

  public Try<Integer> getInt(String key, Integer defaultValue) {
    return Try.of(
        () -> {
          return getString(key).map(Integer::parseInt).getOrElse(defaultValue);
        });
  }
}
