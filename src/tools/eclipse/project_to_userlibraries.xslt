<?xml version="1.0" encoding="UTF-8" ?>
<!--
 Creates an Eclipse xins-eclipse.userlibraries.

 $Id$

 Copyright 2003-2006 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output 
		omit-xml-declaration="no" 
		encoding="UTF-8"
		method="xml" 
		indent="yes" />

	<!-- Define parameters -->
	<xsl:param name="xins_home" />

	<xsl:template match="project">
		<eclipse-userlibraries version="2">
			<library name="xins" systemlibrary="false">
				<archive javadoc="file:/{$xins_home}/docs/javadoc/" path="{$xins_home}/build/xins-server.jar" source="{$xins_home}/src/java-server-framework"/>
				<archive javadoc="file:/{$xins_home}/docs/javadoc/" path="{$xins_home}/build/xins-common.jar" source="{$xins_home}/src/java-common"/>
				<archive javadoc="file:/{$xins_home}/docs/javadoc/" path="{$xins_home}/build/xins-client.jar" source="{$xins_home}/src/java-client"/>
				<archive javadoc="file:/{$xins_home}/docs/javadoc/" path="{$xins_home}/build/logdoc.jar" source="{$xins_home}/src/java-common"/>
				<archive path="{$xins_home}/lib/xmlenc.jar"/>
				<archive path="{$xins_home}/lib/commons-httpclient.jar"/>
				<archive path="{$xins_home}/lib/commons-logging.jar"/>
				<archive path="{$xins_home}/lib/jakarta-oro.jar"/>
				<archive path="{$xins_home}/lib/junit.jar"/>
				<archive path="{$xins_home}/lib/log4j.jar"/>
				<archive path="{$xins_home}/lib/commons-codec.jar"/>
				<archive path="{$xins_home}/lib/servlet.jar"/>
			</library>
		</eclipse-userlibraries>
	</xsl:template>

</xsl:stylesheet>
