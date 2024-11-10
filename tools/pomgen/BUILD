load("//private:artifact.bzl", artifact = "atosgi_artifact")
load("//private:bnd.bzl", "bnd")

java_library(
    name = "pomgen-lib",
    srcs = [
        "PomgenMain.java",
    ],
    visibility = ["//visibility:public"],
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
