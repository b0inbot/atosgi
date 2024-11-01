#!/bin/sh

. tools/shared.sh

set -ex
ffp0 \*\.sh | xargsp0 shfmt -i 4 -w {}
