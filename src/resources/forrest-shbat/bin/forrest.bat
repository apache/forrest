@echo off
setlocal 

Rem ----- Test if ant is around ----------------------------------------
if %ANT_HOME%a==a goto noant

Rem ----- set the current working dir as the PROJECT_HOME variable  ----
call %FORREST_HOME%\bin\setpwdvar.bat 
set PROJECT_HOME=%PWD%

Rem ----- use the location of this script to infer $FORREST_HOME -------
set FORREST_HOME=%~dp0\..

Rem ----- set the ant file to use --------------------------------------
set ANTFILE=%FORREST_HOME%\forrest.build.xml

echo "Apache Forrest.  Run 'forrest -projecthelp' to list options"
echo

Rem ----- call ant.. ---------------------------------------------------
call %ANT_HOME%\bin\ant -buildfile %ANTFILE% -Dproject.home=%PROJECT_HOME% -Dforrest.home=%FORREST_HOME% -emacs -logger org.apache.tools.ant.NoBannerLogger %1 %2 %3 %4 %5 %6 %7 %8 %9

goto end

:noant
echo You must install Ant (http://jakarta.apache.org/ant) 
echo and set ANT_HOME to point at your Ant Installation directory
goto end

:end
endlocal
