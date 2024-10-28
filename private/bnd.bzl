load("@rules_java//java:defs.bzl", "JavaInfo")
load(":providers.bzl", "BndBundleInfo")

def _bnd_impl(ctx):
    bnd = ctx.actions.declare_file(ctx.label.name + ".bnd")
    outjar = ctx.actions.declare_file(ctx.label.name + ".jar")

    links = []
    bundle_basenames = []
    for b in ctx.files.deps:
        sl = ctx.actions.declare_file("link-" + b.basename, sibling = bnd)
        ctx.actions.symlink(output = sl, target_file = b)
        links.append(sl)
        bundle_basenames.append("link-" + b.basename)

    bndlines = []
    bndlines.append("-standalone:")
    bndlines.append("-classpath: " + ", ".join(bundle_basenames))
    bndlines.append("-output: " + outjar.basename)
    bndlines.append("Target-Label: " + str(ctx.label))

    for k in ctx.attr.bnd_inputs:
        bndlines.append(k + ": " + ctx.attr.bnd_inputs[k])

    ctx.actions.write(
        bnd,
        "\n".join(bndlines),
    )

    inputs = depset(links + ctx.files.deps + [bnd])

    args = ctx.actions.args()
    args.add(bnd)

    ctx.actions.run(
        executable = ctx.executable._bnd,
        arguments = [args],
        inputs = inputs,
        outputs = [outjar],
        mnemonic = "BndJar",
        progress_message = "Creating Bundle from JAR %s" % ctx.label,
        toolchain = "@bazel_tools//tools/jdk:current_java_toolchain",
    )

    return [
        DefaultInfo(files = depset([outjar])),
        BndBundleInfo(output = outjar),
        JavaInfo(output_jar = outjar, compile_jar = None),
    ]

bnd = rule(
    attrs = {
        "deps": attr.label_list(
            providers = [],
            allow_files = True,
        ),
        "resources": attr.label_keyed_string_dict(),
        "bnd_inputs": attr.string_dict(),
        "_bnd": attr.label(
            executable = True,
            cfg = "exec",
            default = Label("//:bnd"),
            allow_files = True,
        ),
        "_java_toolchain": attr.label(
            default = "@bazel_tools//tools/jdk:current_java_toolchain",
        ),
    },
    provides = [DefaultInfo, BndBundleInfo, JavaInfo],
    implementation = _bnd_impl,
    toolchains = [],
)
