#!/bin/sh

#TODO: only apply on pending changes

if [ ! -e MODULE.bazel ]; then
    echo "not in project root"
    exit 1
fi

set -ex

bash tools/format/markdown-format.sh
buildifier -r -v ./
bash tools/pomgen/pomgen.sh | xmllint --format --pretty 1 --noblanks - >pom.xml
bash tools/format/xml-format.sh
bash tools/format/sh-format.sh
bash tools/lint/sh-lint.sh
bazel run format
