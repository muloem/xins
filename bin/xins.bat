:: -*- mode: Fundamental; tab-width: 4; -*-
:: ex:ts=4
::
:: $Id$
::
:: Converts xins-project.xml in the current directory to an Ant build file and
:: then passes control to Ant.
::
:: This batch file is intended to be used on Windows 2000 and higher. It has
:: been tested on:
::
::     * Windows 2000 SP 3 (build 5.00.2195)

@ECHO OFF

:: Make sure XINS_HOME is set
IF NOT "%XINS_HOME%" == "" GOTO show_version
ECHO FATAL: XINS_HOME not set
GOTO end

:show_version
IF NOT "%1" == "-version" GOTO start_build
CALL ant -q -f %XINS_HOME%\build.xml version
GOTO end

:start_build
CALL ant -q -f %XINS_HOME%\src\ant\make-build.xml -Dxins_home=%XINS_HOME%

IF NOT ERRORLEVEL 1 CALL ant -f build\build.xml %1 %2 %3 %4 %5 %6 %7 %8 %9
:end
