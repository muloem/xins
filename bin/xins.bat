::
:: $Id$
::
:: Converts xins-project.xml in the current directory to an Ant build file and
:: then passes control to Ant.
::

@ECHO OFF

:: Make sure XINS_HOME is set
set __XINS_HOME=%XINS_HOME%
IF NOT ""%XINS_HOME%"" == """" GOTO test_version

:: If XINS_HOME is not set, it should be the directory of the script.
for %%x in (%0) do set __XINS_HOME=%%~dpsx
for %%x in (%__XINS_HOME%) do set  __XINS_HOME=%%~dpsx
set __XINS_HOME=%__XINS_HOME%..\

ECHO XINS_HOME not set, Setting the scripts directory as XINS_HOME=%__XINS_HOME%
GOTO test_version

:: Recognize version option
:test_version
IF "%1" == "-version" GOTO show_version
IF "%1" == "version"  GOTO show_version
GOTO start_build
:show_version
CALL ant -q -f %__XINS_HOME%\build.xml version
GOTO end

:: Generate the Ant build file
:start_build
CALL ant -f %__XINS_HOME%\src\ant\make-build.xml -Dxins_home=%__XINS_HOME%
IF ERRORLEVEL 1 GOTO end

:: Execute the Ant build file
CALL ant -f build\build.xml %1 %2 %3 %4 %5 %6 %7 %8 %9

:end