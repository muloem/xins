<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="package_for_server_api">
		<xsl:param name="project_file" />
		<xsl:param name="api" />

		<xsl:variable name="prefix" select="document($project_file)/project/java-impls/@packageprefix" />
		<xsl:variable name="suffix" select="document($project_file)/project/java-impls/@packagesuffix" />

		<xsl:if test="string-length($project_file) = 0">
			<xsl:message terminate="yes">Mandatory parameter 'project_file' is not defined.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($api) = 0">
			<xsl:message terminate="yes">Mandatory parameter 'api' is not defined.</xsl:message>
		</xsl:if>

		<xsl:if test="string-length($prefix) &gt; 0">
			<xsl:value-of select="$prefix" />
			<xsl:text>.</xsl:text>
		</xsl:if>

		<xsl:value-of select="$api" />

		<xsl:if test="string-length($suffix) &gt; 0">
			<xsl:text>.</xsl:text>
			<xsl:value-of select="$suffix" />
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
