@echo off

set OLD_ANT_HOME=%ANT_HOME%
set ANT_HOME=tools\ant

set OLD_CLASSPATH=%CLASSPATH%
set CLASSPATH=
for %%i in (.\lib\endorsed\*.jar) do call appendcp.bat %%i

echo Using classpath: "%CLASSPATH%"
call %ANT_HOME%\bin\ant -Djava.endorsed.dirs=lib\endorsed -logger org.apache.tools.ant.NoBannerLogger -emacs %1 %2 %3 %4 %5 %6 %7 %8 %9

set ANT_HOME=%OLD_ANT_HOME%
set CLASSPATH=%OLD_CLASSPATH%
