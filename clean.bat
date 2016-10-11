@echo off
echo This will clean classes and some in src...
pause
rd /s /q classes\j2eetraining
del src\CalcParser*.* src\CalcLexer.*
