<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 Utility XSLT that converts a word to the hungarian notation.

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!--
	- Converts the first character of the specified name to lowercase.
	-
	- Deprecated. Use hungarianLower instead.
	-->
	<xsl:template name="hungarian">
		<xsl:param name="name" />
		<xsl:value-of select="translate(substring($name,1,1),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
		<xsl:value-of select="substring($name,2)" />
	</xsl:template>

	<!--
	- Converts the first character of the specified name to lowercase.
	-->
	<xsl:template name="hungarianLower">
		<xsl:param name="text" />
		<xsl:value-of select="translate(substring($text,1,1),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
		<xsl:value-of select="substring($text,2)" />
	</xsl:template>

	<!--
	- Converts the first character of the specified name to uppercase.
	-->
	<xsl:template name="hungarianUpper">
		<xsl:param name="text" />
		<xsl:value-of select="translate(substring($text,1,1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
		<xsl:value-of select="substring($text,2)" />
	</xsl:template>

	<!--
	- Splits a hungarian-formatted string into words using the specified
	- separator.
	-->
	<xsl:template name="hungarianWordSplit">
		<xsl:param name="text" />
		<xsl:param name="separator" select="' '" />
		<xsl:param name="lastWasLowercase" select="'false'" />

		<xsl:variable name="firstChar" select="substring($text, 1, 1)" />

		<xsl:if test="string-length($firstChar) &gt; 0">
			<xsl:variable name="rest" select="substring($text, 2)" />
			<xsl:variable name="isLowercase">
				<xsl:choose>
					<xsl:when test="string-length(translate($firstChar, 'abcdefghijklmnopqrstuvwxyz', '')) = 0">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<!-- Print a separator if the previous character was lowercase and
		     	this one is uppercase -->
			<xsl:if test="$lastWasLowercase = 'true' and $isLowercase = 'false'">
				<xsl:value-of select="$separator" />
			</xsl:if>

			<xsl:value-of select="$firstChar" />

			<xsl:call-template name="hungarianWordSplit">
				<xsl:with-param name="text"             select="$rest"        />
				<xsl:with-param name="separator"        select="$separator"   />
				<xsl:with-param name="lastWasLowercase" select="$isLowercase" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
