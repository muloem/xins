<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="is_function_session_based">
		<xsl:choose>
			<xsl:when test="string-length(@sessionBased) &lt; 1">
				<xsl:text>false</xsl:text>
			</xsl:when>
			<xsl:when test="@sessionBased = 'false'">
				<xsl:text>false</xsl:text>
			</xsl:when>
			<xsl:when test="@sessionBased = 'true'">
				<xsl:text>true</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>The attribute 'sessionBased' has an invalid value: '</xsl:text>
					<xsl:value-of select="@sessionBased" />
					<xsl:text>'.</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
