:: JZBlock v0.5 JAR Packaging Script
:: Copyright 2024 JZBlock Project
:: Licensed under the Apache License, Version 2.0
@echo off

set proj=%cd%
set src=%cd%\src\main\java
set bin=%cd%\bin\main
set build=%cd%\build

:: Create directories if they don't exist
if not exist "%bin%" mkdir "%bin%"
if not exist "%build%" mkdir "%build%"

echo Building JZBlock v0.5...
cd "%src%"
javac -d "%bin%" *.java

if %errorlevel% neq 0 (
    echo Build failed!
    cd "%proj%"
    pause
    exit /b 1
)

echo Creating JAR package...
cd "%bin%"
jar cvfe "%build%\JZBlock-v0.5.jar" main.java.Main .

if %errorlevel% neq 0 (
    echo JAR creation failed!
    cd "%proj%"
    pause
    exit /b 1
)

echo JAR created successfully: build\JZBlock-v0.5.jar
cd "%proj%"
pause