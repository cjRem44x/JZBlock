#!/bin/bash
# JZBlock v0.5 Build Script (Linux bash version)
# Apache License 2.0

echo "Building JZBlock v0.5..."
echo ""
echo "Select build target:"
echo "  1) Java"
echo "  2) Zig"
echo ""
read -p "Enter choice (1/2, default=1): " choice

# Set default if empty
if [ -z "$choice" ]; then
    choice=1
fi

# Move to project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
proj="$(cd "$SCRIPT_DIR/.." && pwd)"
bin="$proj/bin/main"
src="$proj/src/main/java"
zig_src="$proj/src/main/zig"
args=""

# Make sure bin directory exists
mkdir -p "$bin"

if [ "$choice" = "2" ]; then
    echo ""
    echo "=== Building Zig version ==="
    cd "$zig_src"
    zig build run
    if [ $? -ne 0 ]; then
        echo "Zig build failed!"
        exit 1
    fi
    exit 0
fi

if [ "$choice" = "1" ]; then
    echo ""
    echo "=== Building Java version ==="
    cd "$src"
    javac -d "$bin" \
        util/*.java \
        launcher/*.java \
        *.java
    if [ $? -ne 0 ]; then
        echo "Java build failed!"
        exit 1
    fi
    echo ""
    echo "Java build complete."
    echo "(Run with: java -cp \"$bin\" Main)"
    java -cp "$bin" main.java.Main $args
    exit 0
fi

echo "Invalid option selected!"
exit 1
