::
:: $Id$
::
:: Converts xins-project.xml in the current directory to an Ant build file and
:: then passes control to Ant.
::

@ECHO OFF

if "%OS%" == "Windows_NT" setlocal

:: Make sure XINS_HOME is set
IF NOT ""%XINS_HOME%"" == """" GOTO test_version

:: If XINS_HOME is not set, it should be the directory of the script.
IF NOT "%OS%" == "Windows_NT" GOTO show_error
for %%x in (%0) do set XINS_HOME=%%~dpsx
for %%x in (%XINS_HOME%) do set XINS_HOME=%%~dpsx
set XINS_HOME=%XINS_HOME%..

IF NOT EXIST "%XINS_HOME%\build.xml" GOTO show_error

ECHO XINS_HOME not set, Setting the scripts directory as XINS_HOME=%XINS_HOME%

:: Recognize version option
:test_version
IF "%1" == "-version" GOTO show_version
IF "%1" == "version"  GOTO show_version
GOTO start_build
:show_version
CALL ant -q -f %XINS_HOME%\build.xml version
GOTO end

:: Generate the Ant build file
:start_build
CALL ant -f "%XINS_HOME%\src\ant\make-build.xml" -Dxins_home=%XINS_HOME%
IF ERRORLEVEL 1 GOTO end

:: Execute the Ant build file
CALL ant -f build\build.xml %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO end

:: Displays the error message
:show_error
ECHO Please set XINS_HOME environment variable.
GOTO end

:end
if "%OS%" == "Windows_NT" endlocal
