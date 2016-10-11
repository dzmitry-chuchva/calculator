@echo off
echo ****** To quit calculator, press Ctrl-C :) ********
%JAVA_HOME%\bin\java -cp classes;tools\antlr.jar j2eetraining.tests.calc.Calc %*