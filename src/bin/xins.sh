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
xins_home=${XINS_HOME}
if [ "${xins_home}a" = "a" ]; then
	echo "${prog}: ERROR: XINS_HOME not set. Guessing."
	xins_home=`dirname $0`
	while [ ! `basename ${xins_home}` = "xins" ]; do
		xins_home=`dirname ${xins_home}`
	done
	if [ "${xins_home}a" = "a" ]; then
		echo "${prog}: FATAL: XINS_HOME not set and unable to guess."
		exit 1
	fi
	echo "${prog}: INFO: Assuming XINS_HOME is ${xins_home}."
fi

# Make sure the XSLT style sheet exists
style=${xins_home}/src/xslt/xins-project_to_ant-build.xslt
if [ ! -f ${style} ]; then
	echo "${prog}: FATAL: Cannot find stylesheet at:"
	echo ${style}
	exit 1
fi

# Make sure the input file exists
in=`pwd`/xins-project.xml
if [ ! -f ${in} ]; then
	echo "${prog}: ERROR: Cannot find input file:"
	echo ${style}
	exit 1
fi

# Create the build directory
builddir=`pwd`/build
if [ ! -d ${builddir} ]; then
	mkdir ${builddir}
fi

# Delete the Ant build file if it exists
buildfile=${builddir}/build.xml
if [ -f ${buildfile} ]; then
	rm -f ${buildfile} 2> /dev/null
	if [ -f ${buildfile} ]; then
		echo "${prog}: ERROR: Failed to remove ${buildfile}"
	fi
fi

# Create the Ant build file
out=${buildfile}
project_home=`pwd`
mktemp_template="/tmp/${prog}.XXXXXXXX"
tmpout=`mktemp -q ${mktemp_template}`
returncode=$?
if [ ! "${returncode}a" = "0a" ]; then
	echo "${prog}: ERROR: Unable to create temporary file using template ${mktemp_template}"
	exit 1
fi
echo -n ">> Generating `basename ${out}`..."
ant -f ${xins_home}/src/ant/transform.xml \
    -Din=${in} \
	-Dout=${out} \
	-Dstyle=${style} \
    -Dxins_home=${xins_home} \
    -Dproject_home=${project_home} \
	-Dbuilddir=${builddir} > ${tmpout} 2> /dev/null
returncode=$?
if [ ! "${returncode}a" = "0a" ]; then
	echo " [ FAILED ]"
	echo "${prog}: ERROR: Unable to transform `basename ${in}`:"
	cat ${tmpout}
	rm ${tmpout}
	exit 1
fi
rm ${tmpout}
echo " [ DONE ]"

# Run Ant against the build file
(cd ${builddir} && ant -f `basename ${buildfile}` $*)
