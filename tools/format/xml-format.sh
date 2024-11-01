#!/bin/sh

. tools/shared.sh

set -ex
ffp0 \*\.xml | xargsp0 xmllint --format --pretty 1 --noblanks {} -o {}
