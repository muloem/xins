::
:: $Id$
::
:: Converts xins-project.xml in the current directory to an Ant build file and
:: then passes control to Ant.
::

@ECHO OFF

:: Make sure XINS_HOME is set
IF NOT ""%XINS_HOME%"" == """" GOTO test_version
ECHO xins.bat: FATAL: XINS_HOME not set
GOTO end

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
CALL ant -f %XINS_HOME%\src\ant\make-build.xml -Dxins_home=%XINS_HOME%
IF ERRORLEVEL 1 GOTO end

:: Execute the Ant build file
CALL ant -f build\build.xml %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
