#!/bin/sh
#
# $Id$

prog=`basename $0`

# Make sure XINS_HOME is set
if [ "${XINS_HOME}a" = "a" ]; then
	echo "${prog}: XINS_HOME not set."
	exit 1
fi

style=${XINS_HOME}/src/xslt/xins-project_to_ant-build.xslt

# Make sure the XSLT style sheet exists
if [ ! -f ${style} ]; then
	echo "${prog}: Cannot find stylesheet at:"
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
returncode=$?
if [ ! "${returncode}a" = "0a" ]; then
	echo "${prog}: Unable to transform ${in}."
	exit 1
fi

# Run Ant against the build file
project_home=`pwd`
(cd ${builddir} && ant -Dxins_home=${XINS_HOME} -Dproject_home=${project_home})
