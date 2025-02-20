load("@boinsoft_tools//:rules/ziptool.bzl", "ziptool")
load("@rules_pkg//pkg:zip.bzl", "pkg_zip")
load("//private:artifact.bzl", artifact = "atosgi_artifact")
load("//private:bnd.bzl", "bnd")

package(
    default_package_metadata = [
        "//:license",
        "//:package_info",
    ],
    default_visibility = ["//visibility:public"],
)

java_library(
    name = "launcher-lib",
    srcs = [
        "BundleIndex.java",
        "Main.java",
    ],
    deps = [
        artifact("org.osgi:osgi.core"),
        artifact("org.apache.felix:org.apache.felix.framework"),
        artifact("org.apache.felix:org.apache.felix.main"),
        artifact("io.vavr:vavr"),
    ],
)

java_binary(
    name = "launcher",
    main_class = "boinsoft.atosgi.launcher.Main",
    runtime_deps = [
        ":launcher-lib",
    ],
)

###
### Below is an example of what we might move into a "release" macro,
### we take the bundles and wrap them into a zip
### and merge that zip into our "super-jar"
###

# creates classpath:/autoinstall.d/*.jar that
# can be classpath scanned to install
pkg_zip(
    name = "launcher_autoinstall",
    srcs = [
        "@atosgi//bundles:configadmin",
        "@atosgi//bundles:declarative-services",
        "@atosgi//bundles:felix-default",
        "@atosgi//bundles:fileinstall",
        "@atosgi//bundles:pax-logging-log4j2",
    ],
    package_dir = "autoinstall.d",
)

# We can provide arbitrary files into a zip
# and merge it with our release, providing
# our "overrides" show up first
# in the deps list in ziptool.overlay.
pkg_zip(
    name = "launcher_config",
    srcs = [
        # this file is already in launcher_deploy.jar but we
        # override it here
        "config.properties",
    ],
)

#
# creates the final "release JAR"
#
ziptool.overlay(
    name = "launcher_release",
    output_file = "launcher-release.jar",
    # deps are applied in-order TODO: figure out a better way
    deps = [
        # 1
        ":launcher_config",
        # 2
        ":launcher_deploy.jar",
        # 3
        ":launcher_autoinstall",
    ],
)
