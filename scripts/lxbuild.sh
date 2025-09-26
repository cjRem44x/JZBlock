#!/bin/bash

# project directories
cd ..
proj=$(pwd)
bin="$proj/bin"
lib="$proj/lib"
src="$proj/src"

args=""
test_args=""

# java source directory
java="$src/main/java"

# make sure bin/java exists
mkdir -p "$bin"

# compile java app
cd "$java" || exit
javac24 -d "$bin" \
    *.java

# java source directory
java="$src/test/java"

# make sure bin/java exists
mkdir -p "$bin"

# compile java app
cd "$java" || exit
javac24 -d "$bin" \
    *.java

# run applications
cd "$bin" || exit
java24 main.java.Main "$args"
java24 test.java.MainTest "$test_args"

# return to project root
cd "$proj" || exit
