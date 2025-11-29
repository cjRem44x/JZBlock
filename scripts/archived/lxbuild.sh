#!/bin/bash
# JZBlock v0.5 Build Script
# Copyright 2024 JZBlock Project
# Licensed under the Apache License, Version 2.0

proj="$(cd ../.. && pwd)"
src="$proj/src/main/archived/java"
bin="$proj/bin/main/archvived"

# Create bin directory if it doesn't exist
if [ ! -d "$bin" ]; then
    mkdir -p "$bin"
fi

echo "Building JZBlock [ARCHIVED VERSION]..."
cd "$src"
javac -d "$bin" *.java

if [ $? -ne 0 ]; then
    echo "Build failed!"
    cd "$proj"
    read -p "Press enter to continue..."
    exit 1
fi

echo "Running JZBlock [ARCHIVED VERSION]..."
cd "$bin"
java Main
cd "$proj"
read -p "Press enter to continue..."
