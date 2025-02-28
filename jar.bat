:: AUTHOR: cjRem44x ::
@echo off

set proj=%cd%
set src=%cd%\src\java
set bin=%cd%\bin\java

cd %bin%
jar cvfe "%proj%\JZBlock.jar" Main .
cd %proj%

pause