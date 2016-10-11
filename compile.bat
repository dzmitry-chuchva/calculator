@echo off
%JAVA_HOME%\bin\java -cp tools\antlr.jar antlr.Tool -o src Calc.g
%JAVA_HOME%\bin\javac -cp tools\antlr.jar -d classes src\*.java
if errorlevel 0 goto exit
echo compile fails :(.
goto exit
:exit
echo on