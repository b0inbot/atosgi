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
        "\n".join(["bundle:" + x.basename for x in bundles]) + "\n" + extra,
    )

    return [
        DefaultInfo(files = depset(bundles + [sl])),
        BundlesInfo(depends = ctx.attr.depends, provides = ctx.attr.provides),
    ]

def _bundles_test_impl(ctx):
    expected = ctx.actions.declare_file(ctx.label.name + ".test.in")
    ctx.actions.write(expected, "%s" % ("\n".join(ctx.attr.expected)))

    links = []
    bundle_basenames = []
    for b in ctx.attr.src[DefaultInfo].files.to_list():
        if b.dirname != expected.dirname:
            sl = ctx.actions.declare_file("link-" + b.basename)  #, sibling = expected)
            ctx.actions.symlink(output = sl, target_file = b)
            links.append(sl)

    # Do not add .jar so we can skip it in the bundle loader
    launcher = ctx.actions.declare_file("launcher")
    ctx.actions.symlink(output = launcher, target_file = ctx.attr._test_tool[DefaultInfo].files.to_list()[0])
    links.append(launcher)

    script = """
        find .
        set -x
        F=$(find . -iname \\*.test.in)
	cd $(dirname $F)
	java -jar -DloadCwdBundles -DnonInteractive -DtestScript=$(basename $F) -DtestAndQuit launcher
    """

    ctx.actions.write(
        output = ctx.outputs.executable,
        content = script,
    )

    return [
        DefaultInfo(
            runfiles = ctx.runfiles(files = [
                ctx.attr._test_tool[DefaultInfo].files.to_list()[0],
                expected,
            ] + ctx.attr.src[DefaultInfo].files.to_list() + links),
            files = ctx.attr._test_tool[DefaultInfo].files,
            executable = ctx.outputs.executable,
        ),
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

bundles_test = rule(
    attrs = {
        "src": attr.label(
            providers = [BundlesInfo],
            allow_files = False,
        ),
        "expected": attr.string_list(),
        "_test_tool": attr.label(
            default = "//launcher:launcher_release",
            cfg = "target",
        ),
    },
    provides = [],
    implementation = _bundles_test_impl,
    toolchains = [],
    test = True,
)
