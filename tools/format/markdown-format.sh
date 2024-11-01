#!/bin/sh

. tools/shared.sh

set -ex
ffp0 \*\.md | xargsp0 comrak {} --to commonmark -o {}
