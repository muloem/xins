<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

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

</xsl:stylesheet>
