load("@rules_java//java:defs.bzl", "JavaInfo")

def _zip_overlay_impl(ctx):
    if ctx.attr.output_file:
        out = ctx.actions.declare_file(ctx.attr.output_file)
    else:
        out = ctx.actions.declare_file(ctx.label.name + ".zip")

    args = ctx.actions.args()
    args.add(out)
    for z in ctx.files.deps:
        args.add(z)

    inputs = depset(ctx.files.deps)

    ctx.actions.run(
        executable = ctx.executable._zip_overlay,
        arguments = [args],
        inputs = inputs,
        outputs = [out],
        mnemonic = "ZipOverlay",
        progress_message = "Joining ZIP files for %s" % ctx.label,
        toolchain = "@bazel_tools//tools/jdk:current_java_toolchain",
    )

    return [
        DefaultInfo(files = depset([out])),
    ]

zip_overlay = rule(
    attrs = {
        "deps": attr.label_list(
            providers = [],
            allow_files = True,
        ),
        "output_file": attr.string(),
        "_zip_overlay": attr.label(
            executable = True,
            cfg = "exec",
            default = Label("//tools/zipoverlay:zipoverlay"),
            allow_files = True,
        ),
        "_java_toolchain": attr.label(
            default = "@bazel_tools//tools/jdk:current_java_toolchain",
        ),
    },
    provides = [DefaultInfo],
    implementation = _zip_overlay_impl,
    toolchains = [],
)
