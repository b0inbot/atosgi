load("//private:artifact.bzl", artifact = "atosgi_artifact")
load("//private:bnd.bzl", "bnd")

java_library(
    name = "zipoverlay-lib",
    srcs = [
        "ManyZipEntryStream.java",
        "ZipOverlay.java",
    ],
    visibility = ["//visibility:public"],
    deps = [
        artifact("io.vavr:vavr"),
    ],
)

java_binary(
    name = "zipoverlay",
    main_class = "boinsoft.atosgi.tools.zipoverlay.ZipOverlay",
    runtime_deps = [
        ":zipoverlay-lib",
    ],
)
