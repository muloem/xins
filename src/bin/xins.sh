#!/bin/sh
#
# $Id$

if [ "${XINS_HOME}a" = "a" ]; then
	echo "XINS_HOME not set."
	exit 1
fi

builddir="build"

if [ ! -d build ]; then
	mkdir ${builddir}
fi

in="xins-project.xml"
out=${builddir}/build.xml
style=${XINS_HOME}/src/xslt/xins-project_to_ant-build.xslt
xsltproc -o ${out} ${style} ${in}
