load("@rules_jvm_external//:defs.bzl", "artifact")

def atosgi_artifact(a):
    return artifact(a, repository_name = "@rules_jvm_external++maven+boinsoft_atosgi_maven")
