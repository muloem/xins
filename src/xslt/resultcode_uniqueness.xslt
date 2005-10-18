<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 Utility XSLT that validates the result codes.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="resultcodeValidity">

		<xsl:param name="resultcode_name" />
		<xsl:param name="resultcode_value" />
		<xsl:param name="specsdirectory" />
		<xsl:param name="api_file" />

		<xsl:for-each select="document($api_file)/api/resultcode">
			<xsl:variable name="rcd_file" select="concat($specsdirectory, '/', @name, '.rcd')" />
			<xsl:variable name="rcd_node" select="document($rcd_file)/resultcode" />
			<xsl:variable name="elementName" select="$rcd_node/@name"/>
			<xsl:variable name="elementValue" select="$rcd_node/@value"/>

			<xsl:if test="$elementName != $resultcode_name and $elementValue = $resultcode_value">

				<xsl:message terminate="yes">
					<xsl:text>
The resultcode value must be unique. The resultcode: </xsl:text>
					<xsl:value-of select="$resultcode_name" />
					<xsl:text> has a value: </xsl:text>
					<xsl:value-of select="$resultcode_value" />
					<xsl:text> which is also present in the resultcode: </xsl:text>
					<xsl:value-of select="$elementName" />
				</xsl:message>

			</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>