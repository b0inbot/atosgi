# boinsoft.atosgi

Bazel module supporting OSGI workflows in Bazel.

## Usage

NOTE: currently requires bazel8 due to use of new "symbolic macros"

### Installation

    bazel_dep(name = "atosgi", version = "0.1.0")

### Usage

The main rules are "bnd", "bundles".

"bnd" is used for applying bnd rules to a java\_library
target. The output of the bnd rule is an OSGI bundle that can be deployed.

"bundles" is used for grouping related OSGI bundles together in a single group. In
documentation these are called Bundle Groups (see below).

See [examples/hello/BUILD](examples/hello/BUILD) for annotated usage.

### Bundle Groups

We separate out sets of useful features as "Bundle Groups". You can see these bundle groups
via:

    bazel query 'kind(bundles, @atosgi//bundles:all)'

NOTE: These bundle groups are a more limited form of OBR or Apache Karaf Features. The index
format and behavior is currently in flux.

## Useful targets

- `@atosgi//:bnd` - Executable JAR of the bnd CLI. Usable out of the box\!\!
- `@atosgi//bundles:declarative-services` - Apache Felix SCR implementation of OSGI Declarative Services, as a "Bundle Group"
- `@atosgi//bundles:felix-default` - A Bundle Group that includes the standard OSGI bundles that come with an Apache Felix release.
- `@atosgi//bundles:config-admin` - A ConfigAdmin bundle group. Currently Apache Felix ConfigAdmin.
- `@atosgi//bundles:pax-logging-log4j2` - PAX Logging API + Log4j2 implementation.
- `@@rules_jvm_external++maven+boinsoft_atosgi_maven//:all` - The maven artifacts that atosgi exposes and uses.
- `@atosgi//launcher:launcher_release` - The Launcher release.

## Running the launcher

You can launch the launcher from the @atosgi root:

    bazel build --config release
    java -jar bazel-bin/launcher/launcher-release.jar

## Important version details

- bazel        - 8.0 and above. Currently developed on 8.0.1
- bnd          - 7.0.0
- apache-felix - 7.0.5
- OSGI spec    - 8.0.0

## TODO

Release and deployment is a big missing question.

Some bigger "wrapping" rules that will do the majority of the work.

See [TODO.md](TODO.md) for more detailed and smaller TODOs.
