#!/usr/bin/env bash

ffp0() {
    PATTERN="$1"
    shift
    find . -type f -iname "$PATTERN" -print0
}

xargsp0() {
    cmd=$1
    shift
    xargs --null -t -I {} "$cmd" "$@"
}
