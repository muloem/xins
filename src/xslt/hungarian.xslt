<?xml version="1.0" encoding="US-ASCII"?>
<!--
 Utility XSLT that converts a word to the hungarian notation.

 $Id$

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!--
	- Transform a property name in a hungarian-formatted string starting with an
	- uppercase.
	-->
	<xsl:template name="hungarianUpper">
		<xsl:param name="text" />
		<xsl:param name="startWithUpperCase" select="true()" />

		<xsl:variable name="firstChar" select="substring($text, 1, 1)" />

		<xsl:if test="string-length($firstChar) &gt; 0">
			<xsl:variable name="skipChar" select="$firstChar = '.' or $firstChar = '-' or $firstChar = '_'" />
			<xsl:if test="not($skipChar)">
				<xsl:choose>
					<xsl:when test="$startWithUpperCase">
						<xsl:value-of select="translate($firstChar,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$firstChar" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:variable name="rest" select="substring($text, 2)" />
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text"               select="$rest"  />
				<xsl:with-param name="startWithUpperCase" select="$skipChar" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
	- Transform a property name in a hungarian-formatted string starting with an
	- lowercase.
	-->
	<xsl:template name="hungarianLower">
		<xsl:param name="text" />

		<xsl:variable name="firstChar" select="substring($text, 1, 1)" />
		<xsl:variable name="rest" select="substring($text, 2)" />

		<xsl:value-of select="translate($firstChar,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')" />
		<xsl:call-template name="hungarianUpper">
			<xsl:with-param name="text"               select="$rest"  />
			<xsl:with-param name="startWithUpperCase" select="false()" />
		</xsl:call-template>
	</xsl:template>

	<!--
	- Splits a hungarian-formatted string into words using the specified
	- separator.
	-->
	<xsl:template name="hungarianWordSplit">
		<xsl:param name="text" />
		<xsl:param name="separator" select="' '" />
		<xsl:param name="lastWasLowercase" select="false()" />

		<xsl:variable name="firstChar" select="substring($text, 1, 1)" />

		<xsl:if test="string-length($firstChar) &gt; 0">
			<xsl:variable name="rest" select="substring($text, 2)" />
			<xsl:variable name="isLowercase" select="string-length(translate($firstChar, 'abcdefghijklmnopqrstuvwxyz', '')) = 0" />

			<!-- Print a separator if the previous character was lowercase and
		     	this one is uppercase -->
			<xsl:if test="$lastWasLowercase and not($isLowercase)">
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
