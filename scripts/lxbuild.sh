#!/bin/bash
# JZBlock v0.5 Build Script
# Apache License 2.0

echo "Building JZBlock v0.5..."

# Move to project root (parent of script directory)
cd "$(dirname "$0")/.." || exit
proj="$(pwd)"
bin="$proj/bin/main"
src="$proj/src/main/java"
zig_src="$proj/src/main/zig"

args=""

# Make sure bin directory exists
mkdir -p "$bin"

# Compile Java sources
cd "$src" || exit
javac -d "$bin" *.java
if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

# Run Java app
echo "Running JZBlock v0.5..."
cd "$bin" || exit
java main.java.Main $args

# Run Zig build
cd "$zig_src" || exit
zig build run

# Return to project root
cd "$proj" || exit
