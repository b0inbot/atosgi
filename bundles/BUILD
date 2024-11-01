load("//private:artifact.bzl", artifact = "atosgi_artifact")
load("//private:bundles.bzl", "bundles")

package(default_visibility = ["//visibility:public"])

bundles(
    name = "extras",
    bundles = [
        artifact("org.osgi:org.osgi.util.tracker"),
        artifact("io.vavr:vavr"),
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
    name = "felix-default",
    bundles = [
        artifact("org.apache.felix:org.apache.felix.bundlerepository"),
        artifact("org.apache.felix:org.apache.felix.gogo.command"),
        artifact("org.apache.felix:org.apache.felix.gogo.jline"),
        artifact("org.apache.felix:org.apache.felix.gogo.runtime"),
        artifact("org.fusesource.jansi:jansi"),
        artifact("org.jline:jline"),
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
)

bundles(
    name = "pax-logging-log4j2",
    bundles = [
        artifact("org.ops4j.pax.logging:pax-logging-api"),
        artifact("org.ops4j.pax.logging:pax-logging-log4j2"),
    ],
)
