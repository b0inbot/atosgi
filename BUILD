load("@atosgi//:defs.bzl", "launcher")

genrule(
    name = "bnd-file",
    srcs = [
        "@biz.aQute.bnd//file",
    ],
    outs = ["bnd-file.jar"],
    cmd = "cp $< $@",
    executable = True,
    visibility = ["//visibility:public"],
)

java_import(
    name = "bnd-lib",
    jars = [
        ":bnd-file",
    ],
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
    ],
    visibility = ["//visibility:public"],
)
