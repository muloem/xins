<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="package2dir">
		<xsl:param name="package" />

		<xsl:variable name="firstchar">
			<xsl:value-of select="substring($package, 1, 1)" />
		</xsl:variable>

		<xsl:variable name="rest">
			<xsl:value-of select="substring($package, 2)" />
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="string-length($package) &lt; 1" />
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$firstchar='.'">/</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$firstchar" />
					</xsl:otherwise>
				</xsl:choose>

				<xsl:call-template name="package2dir">
					<xsl:with-param name="package" select="$rest" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
