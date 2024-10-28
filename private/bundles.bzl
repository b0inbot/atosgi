load("@rules_java//java:defs.bzl", "JavaInfo")
load(":providers.bzl", "BundlesInfo")

def _bundles_impl(ctx):
    bundles = ctx.files.bundles
    sl = ctx.actions.declare_file(
        ctx.label.name + "_ATOSGI_MANIFEST.MF",
    )
    ctx.actions.write(
        sl,
        "\n".join([x.basename for x in bundles]) + "\n",
    )

    return [
        DefaultInfo(files = depset(bundles + [sl])),
        BundlesInfo(depends = ctx.attr.depends, provides = ctx.attr.provides),
    ]

bundles = rule(
    attrs = {
        "provides": attr.string_list(),
        "depends": attr.string_list(),
        "bundles": attr.label_list(
            providers = [],
            allow_files = True,
        ),
    },
    provides = [BundlesInfo],
    implementation = _bundles_impl,
    toolchains = [],
)
