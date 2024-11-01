#!/bin/sh

#TODO: make this something runnable in bazel via 'bazel run :genpom'

if [ ! -e MODULE.bazel ]; then
  echo "not in project root"
  exit 1
fi

cat tools/pomgen/pom-start.xml
cat MODULE.bazel \
  | awk -F ':' '/START-pom.xml/ { printing=1;next }      \
                /END-pom.xml/   { printing=0 }            \
                  { if (printing==1) {                     \
                        gsub(/ /, "", $0);                  \
                        group = substr($1, 2);               \
                        art = $2;                             \
                        version = substr($3, 0, length($3)-2); \
                        printf "<dependency><groupId>";         \
                        printf group; printf "</groupId>";       \
                        printf "<artifactId>"; printf art;        \
                        printf "</artifactId>";                    \
                        printf "<version>"; printf version;       \
                        printf "</version></dependency>";        \
                    }                                           \
                  }'
cat tools/pomgen/pom-end.xml
