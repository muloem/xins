#!/bin/sh
#
# $Id$

# Make sure XINS_HOME is set
if [ "${XINS_HOME}a" = "a" ]; then
	echo "XINS_HOME not set."
	exit 1
fi

style=${XINS_HOME}/src/xslt/xins-project_to_ant-build.xslt

# Make sure the XSLT style sheet exists
if [ ! -f ${style} ]; then
	echo "Cannot find stylesheet at:"
	echo ${style}
	exit 1
fi


builddir="build"

if [ ! -d build ]; then
	mkdir ${builddir}
fi

in="xins-project.xml"
out=${builddir}/build.xml
xsltproc -o ${out} ${style} ${in}
