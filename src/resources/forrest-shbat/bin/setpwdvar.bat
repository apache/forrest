@echo off

if %1*==* goto recurse 
set PWD=%~f1

goto end
:recurse
%~dpn0 .

:end
