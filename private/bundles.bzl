load("@rules_java//java:defs.bzl", "JavaInfo")
load(":providers.bzl", "BundlesInfo")

def _bundles_impl(ctx):
    name = ctx.attr.name

    bundles = ctx.files.bundles
    sl = ctx.actions.declare_file(
        ctx.label.name + ".index",
    )
    extra = ""
    if ctx.attr.start_levels:
        extra = "\n".join(["startlevel:" + x for x in ctx.attr.start_levels]) + "\n"

    ctx.actions.write(
        sl,
        "\n".join(["bundle:" + x.short_path for x in bundles]) + "\n" + extra,
    )

    return [
        DefaultInfo(files = depset(bundles + [sl])),
        BundlesInfo(depends = ctx.attr.depends, provides = ctx.attr.provides),
    ]

bundles = rule(
    attrs = {
        "start_levels": attr.string_list(),
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
