<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="urlencode">
		<xsl:param name="text" />

		<xsl:variable name="firstchar">
			<xsl:value-of select="substring($text, 1, 1)" />
		</xsl:variable>

		<xsl:variable name="rest">
			<xsl:value-of select="substring($text, 2)" />
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="string-length($text) &lt; 1" />
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$firstchar='%'">%25</xsl:when>
					<xsl:when test="$firstchar='&amp;'">%26</xsl:when>
					<xsl:when test="$firstchar='+'">%2b</xsl:when>
					<xsl:when test="$firstchar=' '">+</xsl:when>
					<xsl:when test="$firstchar='='">%3d</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$firstchar" />
					</xsl:otherwise>
				</xsl:choose>

				<xsl:call-template name="urlencode">
					<xsl:with-param name="text" select="$rest" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
