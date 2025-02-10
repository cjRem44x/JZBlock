@echo off



set proj=%cd%
set src=%cd%\src\java
set bin=%cd%\bin\java

cd %src%
javac -d %bin% *.java 
java -cp %bin% Main
cd %proj%

pause