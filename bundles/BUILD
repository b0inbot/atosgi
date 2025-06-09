load("//private:artifact.bzl", artifact = "atosgi_artifact")
load("//private:bundles.bzl", "bundles", "bundles_test")

package(
    default_package_metadata = [
        "//:license",
        "//:package_info",
    ],
    default_visibility = ["//visibility:public"],
)

bundles(
    name = "extras",
    bundles = [
        artifact("org.osgi:org.osgi.util.tracker"),
        artifact("io.vavr:vavr"),
    ],
    start_levels = [
        "org.osgi=40",
        "io.vavr=40",
    ],
)

bundles_test(
    name = "extras_test",
    src = ":extras",
    expected = [
        "io.vavr",
        "org.osgi.util.tracker",
    ],
)

# untested
bundles(
    name = "felix-log",
    bundles = [
        artifact("org.apache.felix:org.apache.felix.log"),
    ],
    visibility = ["//visibility:private"],
)

bundles(
    name = "bundlerepository",
    bundles = [
        artifact("org.apache.felix:org.apache.felix.bundlerepository"),
    ],
    start_levels = [
        "org.apache.felix=20",
    ],
)

bundles(
    name = "gogo",
    bundles = [
        artifact("org.apache.felix:org.apache.felix.gogo.command"),
        artifact("org.apache.felix:org.apache.felix.gogo.jline"),
        artifact("org.apache.felix:org.apache.felix.gogo.runtime"),
        artifact("org.fusesource.jansi:jansi"),
        artifact("org.jline:jline"),
    ],
    start_levels = [
        "org.fusesource=10",
        "org.jline=10",
        "org.apache.felix=20",
    ],
)

bundles(
    name = "declarative-services",
    bundles = [
        artifact("org.osgi:org.osgi.service.component"),
        artifact("org.osgi:org.osgi.util.promise"),
        artifact("org.osgi:org.osgi.util.function"),
        artifact("org.apache.felix:org.apache.felix.scr"),
    ],
    depends = [],
    provides = [
        "declarative-services",
    ],
    start_levels = [
        "org.osgi=20",
        "org.apache.felix=30",
    ],
)

bundles(
    name = "pax-logging-log4j2",
    bundles = [
        artifact("org.ops4j.pax.logging:pax-logging-api"),
        artifact("org.ops4j.pax.logging:pax-logging-log4j2"),
    ],
    start_levels = [
        "org.ops4j.pax.logging=40",
    ],
)

bundles(
    name = "configadmin",
    bundles = [
        artifact("org.apache.felix:org.apache.felix.configadmin"),
    ],
    start_levels = [
        "org.apache.felix.configadmin=35",
    ],
)

bundles(
    name = "fileinstall",
    bundles = [
        artifact("org.apache.felix:org.apache.felix.fileinstall"),
    ],
    start_levels = [
        "org.apache.felix.fileinstall=30",
    ],
)

bundles(
    name = "netty",
    bundles = [
        artifact("io.netty:netty-common"),
        artifact("io.netty:netty-buffer"),
        artifact("io.netty:netty-transport"),
        artifact("io.netty:netty-handler"),
        artifact("io.netty:netty-codec"),
        artifact("io.netty:netty-codec-http"),
        artifact("io.netty:netty-codec-http2"),
        artifact("io.netty:netty-codec-socks"),
        artifact("io.netty:netty-resolver"),
        artifact("io.netty:netty-transport-native-unix-common"),
        artifact("io.netty:netty-transport-native-epoll"),
        artifact("io.netty:netty-transport-classes-epoll"),
        artifact("io.netty:netty-handler-proxy"),
    ],
    start_levels = [
        "io.netty=45",
    ],
)

bundles_test(
    name = "netty_test",
    src = ":netty",
    expected = [
        "io.netty.common",
        "io.netty.codec",
    ],
)
