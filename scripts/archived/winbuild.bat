:: JZBlock v0.5 Build Script
:: Copyright 2024 JZBlock Project
:: Licensed under the Apache License, Version 2.0
@echo off

set proj="%cd%\..\.."
set src=%proj%\src\main\archived\java
set bin=%proj%\bin\main\archvived

:: Create bin directory if it doesn't exist
if not exist "%bin%" mkdir "%bin%"

echo Building JZBlock [ARCHIVED VERSION]...
cd "%src%"
javac -d %bin% *.java

if %errorlevel% neq 0 (
    echo Build failed!
    cd "%proj%"
    pause
    exit /b 1
)

echo Running JZBlock [ARCHIVED VERSION]...
cd "%bin%"
java Main

cd "%proj%"
pause
