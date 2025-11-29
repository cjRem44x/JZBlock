@echo off
:: JZBlock v0.5 Build Script (Windows BAT version)
:: Apache License 2.0
echo Building JZBlock v0.5...
echo.
echo Select build target:
echo   [1] Java
echo   [2] Zig
echo.

choice /c 12 /n /m "Enter choice (1 or 2): "
set choice=%errorlevel%

:: Move to project root
pushd %~dp0..
set proj=%cd%
set bin=%proj%\bin\main
set src=%proj%\src\main\java
set zig_src=%proj%\src\main\zig
set args=

:: Make sure bin directory exists
if not exist "%bin%" mkdir "%bin%"

if "%choice%"=="2" (
    echo.
    echo === Building Zig version ===
    pushd "%zig_src%"
    zig build run
    if %errorlevel% neq 0 (
        echo Zig build failed!
        exit /b 1
    )
    popd
    goto end
)    

if "%choice%"=="1" (
    echo.
    echo === Building Java version ===
    pushd "%src%"
    javac -d "%bin%" *.java
    if %errorlevel% neq 0 (
        echo Java build failed!
        exit /b 1
    )
    popd
    echo.
    echo Java build complete.
    echo (Run with: java -cp "%bin%" Main)
    java -cp "%bin%" main.java.Main %args%
    goto end
)

echo Invalid option selected!
exit /b 1

:end
popd
