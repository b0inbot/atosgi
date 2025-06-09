load("//private:artifact.bzl", _atosgi_artifact = "atosgi_artifact")
load("//private:bnd.bzl", _bnd = "bnd")
load("//private:bundles.bzl", _bundles = "bundles", _bundles_test = "bundles_test")
load("//private:launcher.bzl", _launcher = "launcher")

bundles = _bundles
bundles_test = _bundles_test
atosgi_artifact = _atosgi_artifact
bnd = _bnd
launcher = _launcher
