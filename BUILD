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
