@echo off
if "%OS%"=="Windows_NT" @setlocal

rem ----- use the location of this script to infer $FORREST_HOME -------
set DEFAULT_FORREST_HOME=%~dp0\..
if "%FORREST_HOME%"=="" set FORREST_HOME=%DEFAULT_FORREST_HOME%

rem ----- set the current working dir as the PROJECT_HOME variable  ----
call "%FORREST_HOME%\bin\setpwdvar.bat"
set PROJECT_HOME=%PWD%

rem ----- set the ant file to use --------------------------------------
set ANTFILE=%%FORREST_HOME%%\forrest.build.xml

rem ----- Save old ANT_HOME --------------------------------------------
set OLD_ANT_HOME=%ANT_HOME%
set ANT_HOME=%FORREST_HOME%\ant

rem ----- Save and set CLASSPATH --------------------------------------------
set OLD_CLASSPATH=%CLASSPATH%
set CLASSPATH=
for %%i in ("%FORREST_HOME%\lib\endorsed\*.jar") do call "%FORREST_HOME%\bin\appendcp.bat" "%%i"

echo.
echo Apache Forrest.  Run 'forrest -projecthelp' to list options
echo.
rem ----- call ant.. ---------------------------------------------------
echo.
call "%ANT_HOME%\bin\forrestant" -buildfile "%ANTFILE%" -Dbasedir="%PROJECT_HOME%" -Dforrest.home="%FORREST_HOME%" -emacs -logger org.apache.tools.ant.NoBannerLogger %1 %2 %3 %4 %5 %6 %7 %8 %9

rem ---- Restore old ANT_HOME
set ANT_HOME=%OLD_ANT_HOME%
set CLASSPATH=%OLD_CLASSPATH%
