#!/bin/bash
# JZBlock v0.5 Build Script
# Copyright 2024 JZBlock Project
# Licensed under the Apache License, Version 2.0

# project directories
cd "$(dirname "$0")/.." || exit
proj=$(pwd)
bin="$proj/bin/main"
src="$proj/src/main/java"

args=""

echo "Building JZBlock v0.5..."

# make sure bin directory exists
mkdir -p "$bin"

# compile java app
cd "$src" || exit
if command -v javac24 &> /dev/null; then
    javac24 -d "$bin" *.java
else
    javac -d "$bin" *.java
fi

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo "Running JZBlock v0.5..."
cd "$bin" || exit
if command -v java24 &> /dev/null; then
    java24 main.java.Main "$args"
else
    java main.java.Main "$args"
fi

# return to project root
cd "$proj" || exit
