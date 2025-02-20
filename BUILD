load("@atosgi//:defs.bzl", "launcher")
load("@rules_license//:version.bzl", "version")
load("@rules_license//rules:license.bzl", "license")
load("@rules_license//rules:package_info.bzl", "package_info")

package(
    default_applicable_licenses = [
        ":license",
        ":package_info",
    ],
    default_visibility = ["//visibility:public"],
)

license(
    name = "license",
    license_kinds = [
        "@rules_license//licenses/spdx:Apache-2.0",
    ],
    license_text = "LICENSE",
)

package_info(
    name = "package_info",
    package_name = "atosgi",
    package_version = version,
)

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
