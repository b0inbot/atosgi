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
    name = "pomgen-lib",
    srcs = [
        "PomgenMain.java",
    ],
    deps = [
        artifact("io.vavr:vavr"),
    ],
)

java_binary(
    name = "pomgen",
    main_class = "boinsoft.atosgi.tools.pomgen.PomgenMain",
    runtime_deps = [
        ":pomgen-lib",
    ],
)
