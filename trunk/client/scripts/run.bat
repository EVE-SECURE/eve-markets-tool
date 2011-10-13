@echo off

set _ROOT=%~dp0
set _JAVACMD=%JAVACMD%

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
goto runApp

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe

:runApp
%_JAVACMD% -cp %_ROOT%;evemarketstool-commons.jar;evemarketstool-client.jar org.shadanakar.eve.markets.Main

 