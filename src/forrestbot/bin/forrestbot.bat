rem Copyright 2002-2004 The Apache Software Foundation
rem
rem Licensed under the Apache License, Version 2.0 (the "License");
rem you may not use this file except in compliance with the License.
rem You may obtain a copy of the License at
rem
rem     http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

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
