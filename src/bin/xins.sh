#!/bin/sh
#
# $Id$

if [ "${XINS_HOME}a" = "a" ]; then
	echo "XINS_HOME not set."
	exit 1
fi

if [ ! -d build ]; then
	mkdir build
fi

xsltproc -o build/build.xml ${XINS_HOME}/src/xslt/xins-project_to_ant-build.xslt xins-project.xml
