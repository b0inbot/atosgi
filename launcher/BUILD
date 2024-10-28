load("//private:artifact.bzl", artifact = "atosgi_artifact")

java_library(
    name = "launcher-lib",
    srcs = [
        "Main.java",
        "XManifest.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        artifact("org.osgi:osgi.core"),
        artifact("org.apache.felix:org.apache.felix.framework"),
        artifact("org.apache.felix:org.apache.felix.main"),
        artifact("io.vavr:vavr"),
    ],
)
