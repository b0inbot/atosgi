load("//private:bundles.bzl", "bundles")
load("//private:map_maven_artifacts.bzl", "map_mvn_artifact")

map_mvn_artifact(
    "org.fusesource.jansi:jansi",
    "jansi",
)

map_mvn_artifact(
    "org.apache.felix:org.apache.felix.bundlerepository",
    "bundlerepository",
)

map_mvn_artifact(
    "org.apache.felix:org.apache.felix.gogo.command",
    "gogo.command",
)

map_mvn_artifact(
    "org.apache.felix:org.apache.felix.gogo.jline",
    "gogo.jline",
)

map_mvn_artifact(
    "org.apache.felix:org.apache.felix.gogo.runtime",
    "gogo.runtime",
)

map_mvn_artifact(
    "org.jline:jline",
    "jline",
)

map_mvn_artifact(
    "org.apache.felix:org.apache.felix.scr",
    "scr",
)

map_mvn_artifact(
    "org.osgi:org.osgi.service.component",
    "org.osgi.service.component",
)

map_mvn_artifact(
    "org.osgi:org.osgi.util.promise",
    "org.osgi.util.promise",
)

map_mvn_artifact(
    "org.osgi:org.osgi.util.function",
    "org.osgi.util.function",
)

map_mvn_artifact(
    "org.osgi:org.osgi.util.tracker",
    "org.osgi.util.tracker",
)

map_mvn_artifact(
    "io.vavr:vavr",
    "io.vavr",
)

bundles(
    name = "felix-default",
    bundles = [
        ":bundlerepository",
        ":gogo.command",
        ":gogo.jline",
        ":gogo.runtime",
        ":jansi",
        ":jline",
    ],
    visibility = ["//visibility:public"],
)

bundles(
    name = "declarative-services",
    bundles = [
        ":org.osgi.service.component",
        ":org.osgi.util.function",
        ":org.osgi.util.promise",
        ":scr",
    ],
    depends = [],
    provides = [
        "declarative-services",
    ],
    visibility = ["//visibility:public"],
)
