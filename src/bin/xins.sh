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

# Create the build directory
if [ ! -d build ]; then
	mkdir ${builddir}
fi

# Create the Ant build file
in="xins-project.xml"
out=${builddir}/build.xml
xsltproc -o ${out} ${style} ${in}

# Run Ant against the build file
projectdir=`pwd`
(cd ${builddir} && ant -Dxins_home=${XINS_HOME} -Dprojectdir=${projectdir})
