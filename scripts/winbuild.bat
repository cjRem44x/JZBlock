@echo off
:: JZBlock v0.5 Build Script (Windows BAT version)
:: Apache License 2.0

echo Building JZBlock v0.5...

:: Move to project root (parent of script directory)
pushd %~dp0..
set proj=%cd%
set bin=%proj%\bin\main
set src=%proj%\src\main\java
set zig_src=%proj%\src\main\zig

set args=

:: Make sure bin directory exists
if not exist "%bin%" mkdir "%bin%"

:: Compile Java source
pushd "%src%"
javac -d "%bin%" *.java
if %errorlevel% neq 0 (
    echo Build failed!
    exit /b 1
)
popd

echo Running JZBlock v0.5...
pushd "%bin%"
java main.java.Main %args%
popd

cd %zig_src%
zig build run

:: Return to project root
popd
