:: JZBlock v0.5 Build Script
:: Copyright 2024 JZBlock Project
:: Licensed under the Apache License, Version 2.0
@echo off

set proj=%cd%
set src=%cd%\src\main\java
set bin=%cd%\bin\main

:: Create bin directory if it doesn't exist
if not exist "%bin%" mkdir "%bin%"

echo Building JZBlock v0.5...
cd "%src%"
javac -d "%bin%" *.java

if %errorlevel% neq 0 (
    echo Build failed!
    cd "%proj%"
    pause
    exit /b 1
)

echo Running JZBlock v0.5...
cd "%bin%"
java main.java.Main

cd "%proj%"
pause