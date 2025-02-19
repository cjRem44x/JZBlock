:: AUTHOR: cjRem44x ::
@echo off

set proj=%cd%
set src=%cd%\src\java
set bin=%cd%\bin\java

cd %bin%
jar cvfe JZBlock.jar Main *.class
cd %proj%

pause