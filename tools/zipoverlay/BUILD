load("//private:artifact.bzl", artifact = "atosgi_artifact")
load("//private:bnd.bzl", "bnd")

package(
    default_package_metadata = [
        "//:license",
        "//:package_info",
    ],
    default_visibility = ["//visibility:public"],
)

java_library(
    name = "zipoverlay-lib",
    srcs = [
        "ManyZipEntryStream.java",
        "ZipOverlay.java",
    ],
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
