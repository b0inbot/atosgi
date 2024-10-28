load("@rules_jvm_external//:defs.bzl", "artifact")

def map_mvn_artifact(p, name):
    native.genrule(
        name = name,
        srcs = [artifact(p, repository_name = "boinsoft_atosgi_maven")],
        outs = [name + ".jar"],
        cmd = "cp $< $@",
        visibility = ["//visibility:public"],
    )

def map_many_mvn_artifacts(group, names, prefix = ""):
    srcs = []
    for name in names:
        map_mvn_artifact(group + ":" + name, prefix + group + "." + name)
        srcs.append(":" + prefix + group + "." + name)

    native.filegroup(
        name = prefix + group,
        srcs = srcs,
        visibility = ["//visibility:public"],
    )
