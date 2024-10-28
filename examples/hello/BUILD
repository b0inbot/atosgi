load("@atosgi//:defs.bzl", "atosgi_artifact", "bnd", "launcher")

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
    },
    deps = [
        ":hello",
    ],
)

# We define a launcher which can be used as a local testing environment.
# we include "felix-default" and "declarative-services" by default
# but they are included here as examples.
#
launcher(
    name = "interactive",
    # you can see which bundle groups are available:
    #   bazel query 'kind(bundles, @atosgi//...)'
    # and you can create your own via the `bundles` rule.
    bundles = [
        "@atosgi//bundles:felix-default",
        "@atosgi//bundles:declarative-services",
    ],
    # targets are *any* bnd label
    targets = [
        ":hello_bundle",
    ],
    visibility = ["//visibility:public"],
)

#
# examples session
#
# $ bazel run @atosgi//examples/hello:interactive
# ...
# [INFO] atosgi-launcher
#
# Hello SCR: activate
# ____________________________
# Welcome to Apache Felix Gogo
#
# g! lb
# START LEVEL 20
#    ID|State      |Level|Name
#     0|Active     |    0|System Bundle (7.0.5)|7.0.5
#     1|Active     |   20|hello.hello_bundle (0.0.0)|0.0.0
#     2|Active     |   10|Apache Felix Bundle Repository (2.0.10)|2.0.10
#     3|Active     |   10|Apache Felix Gogo Command (1.1.2)|1.1.2
#     4|Active     |   10|Apache Felix Gogo JLine Shell (1.1.8)|1.1.8
#     5|Active     |   10|Apache Felix Gogo Runtime (1.1.4)|1.1.4
#     6|Active     |   10|jansi (1.18.0)|1.18.0
#     7|Active     |   10|JLine Bundle (3.13.2)|3.13.2
#     8|Active     |   10|org.osgi:org.osgi.service.component (1.5.1.202212101352)|1.5.1.202212101352
#     9|Active     |   10|org.osgi:org.osgi.util.function (1.2.0.202109301733)|1.2.0.202109301733
#    10|Active     |   10|org.osgi:org.osgi.util.promise (1.3.0.202212101352)|1.3.0.202212101352
#    11|Active     |   10|Apache Felix Declarative Services (2.2.12)|2.2.12
#
