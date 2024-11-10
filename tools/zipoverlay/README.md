# atosgi/tools/zipoverlay

A tool for taking a list of ZIP files and merging them together.

Usage:

    zipoverlay dest.zip s1.zip s2.zip s3.zip ...

## NOTES

general reason for this tool is to join zip / jar files together for "release mega-jars"
that can contain arbitrary trees of files rather than the stricter layout that
`java_binary` allows.

NOTE: Duplicate zip entries always fail. However, MANIFEST.MF files wil be quietly ignored except the
one listed first.

eventual usage in bazel:

    java_library(
      name = "z",
      srcs = [ ... ],
    )
    
    bnd(
      name = "z-bundle",
      deps = [":z"],
      bnd_lines = [
        "Private-Package: ...",
      ],
    )
    
    pkg_zip(
      name = "z2", 
      srcs = [ ... ],
    )
    
    zip_overlay(
      name = "my-mega-jar",
      out = "my-meta-jar.jar",
      deps = [
        ":z-bundle",
        ":z2",
      ],
    )
