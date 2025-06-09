load("@atosgi//:defs.bzl", "atosgi_artifact", "bnd", "bundles", "bundles_test")

package(
    default_package_metadata = [
        "//:license",
        "//:package_info",
    ],
    default_visibility = ["//examples:__subpackages__"],
)

# We define our java_library like any other library, except that if we
# need OSGI APIs, we include them as deps.
#
# if you want to see available atosgi artifacts:
#    bazel query '@@rules_jvm_external++maven+boinsoft_atosgi_maven//:all'
# atosgi_artifact converts its short name to long name:
#    org.osgi:osgi.cmpn -> org_osgi_osgi_cmpn
# thus some domain knowledge is required to know what you do and do not want.
#
java_library(
    name = "hello",
    srcs = [
        "Hello.java",
    ],
    deps = [
        atosgi_artifact("org.osgi:osgi.cmpn"),
        atosgi_artifact("org.ops4j.pax.logging:pax-logging-api"),
    ],
)

# We "OSGI-ify" our java_library by connecting it to a bundle,
# setting our Private-Package to the one defined in Hello.java,
# and depend on our library
bnd(
    name = "hello_bundle",
    # the full range of bnd inputs are anything that can be in a MANIFEST.MF Jar
    # PLUS any bnd instructions that the bnd cli supports in "standalone mode":
    # https://bnd.bndtools.org/chapters/820-instructions.html
    bnd_inputs = {
        "Private-Package": "boinsoft.atosgi.example",
        "Bundle-SymbolicName": "boisoft.atosgi.example.hello_bundle",
    },
    deps = [
        ":hello",
    ],
)

bnd(
    name = "hello_bundle2",
    bnd_inputs = {
        "Private-Package": "boinsoft.atosgi.example",
        "Bundle-SymbolicName": "boisoft.atosgi.example.hello_bundle2",
    },
    deps = [
        ":hello",
    ],
)

# We create a "bundle group" which specifies the list of bundles and other
# bundle group dependencies. (This is currently required for running in a launcher target
# but may become unnecessary.)
bundles(
    name = "hello_bundles",
    bundles = [
        ":hello_bundle",
    ],
)

bundles_test(
    name = "hello_bundles_test",
    src = ":hello_bundles",
    expected = [
        "boisoft.atosgi.example.hello_bundle",
    ],
)
