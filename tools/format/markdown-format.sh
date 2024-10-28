#!/bin/sh

set -ex
find . -iname \*md -print0 | xargs --null -t -I {} comrak {} --to commonmark -o {}
