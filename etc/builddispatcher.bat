@echo off
rem Copyright 2002-2006 The Apache Software Foundation or its licensors,
rem as applicable.
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

if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

rem ----- use the location of this script to infer $FORREST_HOME -------
if NOT "%OS%"=="Windows_NT" set DEFAULT_FORREST_HOME=..
if "%OS%"=="Windows_NT" set DEFAULT_FORREST_HOME=%~dp0\..
if "%OS%"=="WINNT" set DEFAULT_FORREST_HOME=%~dp0\..
if "%FORREST_HOME%"=="" set FORREST_HOME=%DEFAULT_FORREST_HOME%

rem ----- set the current working dir as the PROJECT_HOME variable  ----
if NOT "%OS%"=="Windows_NT" call "%FORREST_HOME%\bin\setpwdvar98.bat"
if "%OS%"=="Windows_NT" call "%FORREST_HOME%\bin\setpwdvar.bat"
if "%OS%"=="WINNT" call "%FORREST_HOME%\bin\setpwdvar.bat"
set PROJECT_HOME=%PWD%

cd "%FORREST_HOME%\main"
call build clean
call build
cd "%FORREST_HOME%\whiteboard\plugins\org.apache.forrest.plugin.internal.dispatcher"
call "%FORREST_HOME%\tools\ant\bin\ant.bat" local-deploy
cd "%FORREST_HOME%\whiteboard\plugins\org.apache.forrest.themes.core"
call "%FORREST_HOME%\tools\ant\bin\ant.bat" local-deploy
cd "%PROJECT_HOME%"
