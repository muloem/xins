<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$

 Copyright 2004 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="package_name" />
	<xsl:param name="sourcedir" />
	<xsl:param name="group"     />

	<!-- Define variables -->

	<!-- Configure output method -->
	<xsl:output
	method="xml"
	indent="no"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:template match="log">
		<html>
			<head>
				<title>
					<xsl:text>Log entry group '</xsl:text>
					<xsl:value-of select="group[@id = $group]/@name" />
					<xsl:text>'</xsl:text>
				</title>
				<meta name="generator" content="logdoc" />
				<link rel="stylesheet" type="text/css" href="style.css" />
			</head>
			<body>
				<table class="headerlinks">
					<tr>
						<td>
							<a href="index.html">Logdoc index</a>
							<xsl:text> | </xsl:text>
							<a href="entry-list.html">Logdoc entry list</a>
							<xsl:text> | </xsl:text>
							<span class="active">Log entry group</span>
							<xsl:text> | </xsl:text>
							<span class="disabled">Log entry</span>
						</td>
					</tr>
				</table>
				<xsl:apply-templates select="group[@id = $group]" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="group">
		<xsl:variable name="category">
			<xsl:value-of select="$package_name" />
			<xsl:text>.</xsl:text>
			<xsl:value-of select="@id" />
		</xsl:variable>

		<h1>
			<xsl:text>Log entry group '</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>'</xsl:text>
		</h1>

		<xsl:text>The category for this group is: </xsl:text>
		<code>
			<xsl:value-of select="$category" />
		</code>
		<table type="entries">
			<tr>
				<th title="The unique identifier of the entry">ID</th>
				<th title="A description of the message entry, in US English">Description</th>
				<th title="The log level for the message, ranging from DEBUG to FATAL">Level</th>
				<th title="Number of parameters the message accepts">Parameters</th>
			</tr>
			<xsl:for-each select="entry">
				<xsl:variable name="entry_link">
					<xsl:text>entry-</xsl:text>
					<xsl:value-of select="@id" />
					<xsl:text>.html</xsl:text>
				</xsl:variable>
				<tr>
					<td>
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="$entry_link" />
							</xsl:attribute>
							<xsl:value-of select="@id" />
						</a>
					</td>
					<td>
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="$entry_link" />
							</xsl:attribute>
							<xsl:apply-templates select="description" />
						</a>
					</td>
					<td>
						<xsl:value-of select="@level" />
					</td>
					<td>
						<xsl:value-of select="count(param)" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>
