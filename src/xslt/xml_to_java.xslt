<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

  Utility XSLT that provide a template that return a string with the
 " and \ character escaped.

$Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="xml_to_java_string">
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
					<xsl:when test="$firstchar='\'">\\</xsl:when>
					<xsl:when test="$firstchar='&quot;'">\"</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$firstchar" />
					</xsl:otherwise>
				</xsl:choose>

				<xsl:call-template name="xml_to_java_string">
					<xsl:with-param name="text" select="$rest" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Normalize the text. This means that consecutive spaces (including tabs,
	     \r and \n) are merged to one space.
	     On the contrary to the normalize() method this template does not
	     troncate the leading and trailing spaces.
	-->
	<xsl:template name="normalize">
		<xsl:param name="text" />
		<xsl:param name="previouschar" />

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
					<xsl:when test="($previouschar=' ' or $previouschar='&#xA;' or $previouschar='&#x9;' or $previouschar='&#xD;') and ($firstchar=' ' or $firstchar='&#xA;' or $firstchar='&#x9;' or $firstchar='&#xD;')" />
					<xsl:when test="$firstchar=' ' or $firstchar='&#xA;' or $firstchar='&#x9;' or $firstchar='&#xD;'">
						<xsl:text> </xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$firstchar" />
					</xsl:otherwise>
				</xsl:choose>

				<xsl:call-template name="normalize">
					<xsl:with-param name="text" select="$rest" />
					<xsl:with-param name="previouschar" select="$firstchar" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
