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


:: Recognise -version option
IF NOT "%1" == "-version" GOTO no_version
CALL ant -f %XINS_HOME%\build.xml version
GOTO end

:: Determine the current drive and directory
:no_version
SETLOCAL
SET project_home=
FOR /f "Tokens=*" %%i IN ('CD') DO SET project_home=%%i
ECHO Current directory is %project_home%

:: Make sure XINS_HOME is set
IF NOT "%XINS_HOME%" == "" GOTO xins_home_set
ECHO FATAL: XINS_HOME not set
GOTO end

:xins_home_set
IF EXIST %XINS_HOME%\src\xslt\xins-project_to_ant-build.xslt GOTO stylesheet_found
ECHO FATAL: Cannot find stylesheet
%XINS_HOME%\src\xslt\xins-project_to_ant-build.xslt
GOTO end

:stylesheet_found
IF EXIST xins-project.xml GOTO project_file_found
ECHO FATAL: Cannot find input file xins-project.xml
GOTO end

:project_file_found
IF NOT EXIST build\NUL GOTO builddir_existent
MKDIR build

:builddir_existent
CALL ant -f %XINS_HOME%\src\ant\transform.xml -Din=%project_home%\xins-project.xml -Dout=%project_home%\build\build.xml -Dstyle=%XINS_HOME%\src\xslt\xins-project_to_ant-build.xslt -Dxins_home=%XINS_HOME% -Dproject_home=%project_home% -Dbuilddir=build

CALL ant -f %project_home%\build\build.xml %1 %2 %3 %4 %5 %6 %7 %8 %9
:end
