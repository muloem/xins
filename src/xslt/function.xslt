<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 Utiltity XSLT that provides templates to know whether a function is
 session based or it creates the session.

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

	<xsl:template name="does_function_creates_session">
		<xsl:choose>
			<xsl:when test="string-length(@createsSession) &lt; 1">
				<xsl:text>false</xsl:text>
			</xsl:when>
			<xsl:when test="@createsSession = 'false'">
				<xsl:text>false</xsl:text>
			</xsl:when>
			<xsl:when test="@createsSession = 'true'">
				<xsl:text>true</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>The attribute 'createsSession' has an invalid value: '</xsl:text>
					<xsl:value-of select="@createsSession" />
					<xsl:text>'.</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
