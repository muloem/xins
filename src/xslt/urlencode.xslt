<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that URL encodes the given text.

 $Id$

 Copyright 2004 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
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
					<xsl:when test="$firstchar=' '">+</xsl:when>
					<xsl:when test="$firstchar='!'">%21</xsl:when>
					<xsl:when test="$firstchar='&quot;'">%22</xsl:when>
					<xsl:when test="$firstchar='#'">%23</xsl:when>
					<xsl:when test="$firstchar='$'">%24</xsl:when>
					<xsl:when test="$firstchar='%'">%25</xsl:when>
					<xsl:when test="$firstchar='&amp;'">%26</xsl:when>
					<xsl:when test="$firstchar='&quot;'">%27</xsl:when>
					<xsl:when test="$firstchar='('">%28</xsl:when>
					<xsl:when test="$firstchar=')'">%29</xsl:when>
					<xsl:when test="$firstchar='*'">%2A</xsl:when>
					<xsl:when test="$firstchar='+'">%2B</xsl:when>
					<xsl:when test="$firstchar=','">%2C</xsl:when>
					<xsl:when test="$firstchar='/'">%2F</xsl:when>
					<xsl:when test="$firstchar=':'">%3A</xsl:when>
					<xsl:when test="$firstchar=';'">%3B</xsl:when>
					<xsl:when test="$firstchar='&lt;'">%3C</xsl:when>
					<xsl:when test="$firstchar='='">%3D</xsl:when>
					<xsl:when test="$firstchar='&gt;'">%3E</xsl:when>
					<xsl:when test="$firstchar='?'">%3F</xsl:when>
					<xsl:when test="$firstchar='@'">%40</xsl:when>
					<xsl:when test="$firstchar='^'">%5E</xsl:when>
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
