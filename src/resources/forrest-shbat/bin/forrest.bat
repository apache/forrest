@echo off
setlocal 

Rem ----- set the current working dir as the PROJECT_HOME variable  ----
call "%FORREST_HOME%\bin\setpwdvar.bat"
set PROJECT_HOME="%PWD%"

Rem ----- use the location of this script to infer $FORREST_HOME -------
set FORREST_HOME=%~dp0\..

Rem ----- set the ant file to use --------------------------------------
set ANTFILE="%FORREST_HOME%\forrest.build.xml"

Rem ----- Save old ANT_HOME --------------------------------------------
set OLD_ANT_HOME=%ANT_HOME%
set ANT_HOME="%FORREST_HOME%\ant"

echo "Apache Forrest.  Run 'forrest -projecthelp' to list options"

Rem ----- call ant.. ---------------------------------------------------
call "%ANT_HOME%\bin\ant" -buildfile %ANTFILE% -Dbasedir="%PROJECT_HOME%" -Dproject.home="%PROJECT_HOME%" -Dforrest.home="%FORREST_HOME%" -emacs -logger org.apache.tools.ant.NoBannerLogger %1 %2 %3 %4 %5 %6 %7 %8 %9

Rem ---- Restore old ANT_HOME
set ANT_HOME=%OLD_ANT_HOME%
set CLASSPATH=%OLD_CLASSPATH%

endlocal
