def _launcher_impl(name, targets = [], bundles = [], repos = [], *args, **kwargs):
    default_bundles = [
        "@atosgi//bundles:gogo",
        "@atosgi//bundles:declarative-services",
    ]

    all_bundles = (bundles if len(bundles) != 0 else default_bundles) + targets

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
        "targets": attr.label_list(mandatory = False, doc = "", configurable = False),
        "bundles": attr.label_list(mandatory = False, doc = "", configurable = False),
    },
    implementation = _launcher_impl,
)
