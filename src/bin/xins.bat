:: -*- mode: Fundamental; tab-width: 4; -*-
:: ex:ts=4
::
:: $Id$
::
:: Converts xins-project.xml in the current directory to an Ant build file and
:: then passes control to Ant.

@ECHO OFF


:: Recognise -version option
IF NOT "%1" == "-version" GOTO no_version
java -version
CALL ant -version
CALL ant -f %XINS_HOME%\build.xml -listener org.apache.tools.ant.NoBannerLogger version
GOTO end

:: Determine the current drive and directory
ECHO @PROMPT SET CURDRV=$N:$_SET CURDIR=$P> %temp%.\drvdir01.bat
%COMSPEC% /E:2048 /C%temp%.\drvdir01.bat > %temp%.\drvdir02.bat
CALL %temp%.\drvdir02.bat
DEL %temp%.\drvdir01.bat
DEL %temp%.\drvdir02.bat
IF EXIST %CURDIR%\nul SET CURDIR=%CURDIR%\
ECHO. Current drive is %CURDRV%
ECHO. Current directory is %CURDIR%

:: Make sure XINS_HOME is set
:no_version
IF NOT "%XINS_HOME%" == "" GOTO xins_home_set
ECHO FATAL: XINS_HOME not set
GOTO end

:xins_home_set
IF EXIST %XINS_HOME%\src\xslt\xins-project_to_ant-build.xslt GOTO stylesheet_found
ECHO FATAL: Cannot find stylesheet %XINS_HOME%\src\xslt\xins-project_to_ant-build.xslt
GOTO end

:stylesheet_found
IF EXIST xins-project.xml GOTO project_file_found
ECHO FATAL: Cannot find input file xins-project.xml
GOTO end

:project_file_found
IF NOT EXIST build\NUL GOTO builddir_existent
MKDIR build

:builddir_existent
CALL ant -f %XINS_HOME%\src\ant\transform.xml -Din=%CURDRIVE%:\%CURDIR%xins-project.xml -Dout=%CURDRIVE%:\%CURDIR%build\build.xml -Dstyle=%XINS_HOME%\src\xslt\xins-project_to_ant-build.xslt -Dxins_home=%XINS_HOME% -Dproject_home=%CURDRV%:\%CURDIR% -Dbuilddir=build

CALL ant -f %CURDRV%:\%CURDIR%build\build.xml %1 %2 %3 %4 %5 %6 %7 %8 %9
:end
