#!/bin/sh
#
# -*- mode: Fundamental; tab-width: 4; -*-
# ex:ts=4
#
# $Id$
#
# Converts xins-project.xml in the current directory to an Ant build file and
# then passes control to Ant.
#

prog=`basename $0`

# Make sure XINS_HOME is set
if [ "${XINS_HOME}a" = "a" ]; then
	echo "${prog}: XINS_HOME not set."
	exit 1
fi

# Make sure the XSLT style sheet exists
style=${XINS_HOME}/src/xslt/xins-project_to_ant-build.xslt
if [ ! -f ${style} ]; then
	echo "${prog}: Cannot find stylesheet at:"
	echo ${style}
	exit 1
fi

# Create the build directory
builddir=build
if [ ! -d ${builddir} ]; then
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
(cd ${builddir} && ant -Dxins_home=${XINS_HOME} \
                       -Dproject_home=${project_home} \
                       $*)
