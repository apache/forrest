@echo off
setlocal 

Rem ----- use the location of this script to infer $FORREST_HOME -------
set DEFAULT_FORREST_HOME=%~dp0\..
if "%FORREST_HOME%"=="" set FORREST_HOME=%DEFAULT_FORREST_HOME%

Rem ----- set the current working dir as the PROJECT_HOME variable  ----
call "%FORREST_HOME%\bin\setpwdvar.bat"

set FORRESTBOT_HOME="%FORREST_HOME%\forrestbot"

Rem ----- set the ant file to use --------------------------------------
set ANTFILE="%FORRESTBOT_HOME%\forrestbot.build.xml"

Rem ----- Save old ANT_HOME --------------------------------------------
set OLD_ANT_HOME=%ANT_HOME%
set ANT_HOME="%FORREST_HOME%\ant"

set OLD_CLASSPATH=%CLASSPATH%
set CLASSPATH=
for %%i in ("%FORREST_HOME%\lib\endorsed\*.jar") do call appendcp.bat %%i
for %%i in ("%FORRESTBOT_HOME%\lib\*.jar") do call appendcp.bat %%i

echo "Apache Forrest.  Run 'forrest -projecthelp' to list options"

Rem ----- call ant.. ---------------------------------------------------
call "%ANT_HOME%\bin\ant" -buildfile %ANTFILE% -Dbasedir="%PWD%" -Dforrest.home="%FORREST_HOME%" -emacs -logger org.apache.tools.ant.NoBannerLogger %1 %2 %3 %4 %5 %6 %7 %8 %9

Rem ---- Restore old ANT_HOME
set ANT_HOME=%OLD_ANT_HOME%
set CLASSPATH=%OLD_CLASSPATH%

endlocal
