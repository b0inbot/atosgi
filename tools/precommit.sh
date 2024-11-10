#!/bin/sh

#TODO: only apply on pending changes

if [ ! -e MODULE.bazel ]; then
    echo "not in project root"
    exit 1
fi

set -ex

bash tools/format/markdown-format.sh
buildifier -r -v ./
bazel run tools/pomgen:pomgen
xmllint --format --pretty 1 --noblanks -o pom.xml pom.xml
bash tools/format/xml-format.sh
bash tools/format/sh-format.sh
bash tools/lint/sh-lint.sh
bazel run format
