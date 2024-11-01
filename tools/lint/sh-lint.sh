#!/bin/sh

. tools/shared.sh

set -ex
ffp0 \*\.sh | xargsp0 shellcheck -x -a -P ./ {}
