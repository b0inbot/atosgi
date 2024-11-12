load("@atosgi//:defs.bzl", "launcher")

alias(
    name = "bnd-lib",
    actual = "@biz.aQute.bnd//jar",
)

java_binary(
    name = "bnd",
    main_class = "aQute.bnd.main.bnd",
    visibility = ["//visibility:public"],
    runtime_deps = [
        ":bnd-lib",
    ],
)

alias(
    name = "format",
    actual = "//tools/format",
)

launcher(
    name = "gogo",
    bundles = [
        "@atosgi//bundles:felix-default",
        "@atosgi//bundles:extras",
        "@atosgi//bundles:netty",
    ],
    visibility = ["//visibility:public"],
)
