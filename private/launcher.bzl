def _launcher_impl(name, targets, bundles = [], *args, **kwargs):
    native.genrule(
        name = name + "_targets_manifest",
        outs = [name + "_ATOSGI_MANIFEST.MF"],
        cmd = "touch $@\n" + "\n".join(["echo " + x.name + ".jar > $@ \n" for x in targets]),
    )
    default_bundles = [
        "@atosgi//bundles:felix-default",
        "@atosgi//bundles:declarative-services",
    ]

    all_bundles = (bundles if len(bundles) != 0 else default_bundles) + targets + [":" + name + "_targets_manifest"]

    native.java_binary(
        name = name,
        deploy_manifest_lines = [
            "Atosgi-StartLevel: 50",
        ],
        main_class = "boinsoft.atosgi.launcher.Main",
        data = all_bundles,
        runtime_deps = ["@atosgi//launcher:launcher-lib"],
        *args,
        **kwargs
    )

launcher = macro(
    attrs = {
        "targets": attr.label_list(mandatory = True, doc = "", configurable = False),
        "bundles": attr.label_list(mandatory = False, doc = "", configurable = False),
    },
    implementation = _launcher_impl,
)
