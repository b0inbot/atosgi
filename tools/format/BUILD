load("@aspect_rules_lint//format:defs.bzl", "format_multirun")
load("@rules_java//java:defs.bzl", "java_binary")

package(
    default_package_metadata = [
        "//:license",
        "//:package_info",
    ],
    default_visibility = ["//:__subpackages__"],
)

alias(
    name = "java-format-lib",
    actual = "@google-java-format//jar",
)

java_binary(
    name = "java-format",
    jvm_flags = [
        "--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
    ],
    main_class = "com.google.googlejavaformat.java.Main",
    runtime_deps = [":java-format-lib"],
)

format_multirun(
    name = "format",
    java = ":java-format",
)
