<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="sourcedir" />
	<xsl:param name="entry"     />

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
					<xsl:text>Log entry </xsl:text>
					<xsl:value-of select="$entry" />
				</title>
				<meta name="generator" content="logdoc" />
				<link rel="stylesheet" type="text/css" href="style.css" />
			</head>
			<body>
				<h1>
					<xsl:text>Log entry </xsl:text>
					<xsl:value-of select="$entry" />
				</h1>
				<xsl:apply-templates select="entry[@id = $entry]" />

				<h2>Message sets</h2>
				<xsl:choose>
					<xsl:when test="messageset">
						<table>
							<xsl:apply-templates select="messageset">
								<!--
								<xsl:with-param name=""></xsl:with-param>
								-->
							</xsl:apply-templates>
						</table>
					</xsl:when>
					<xsl:otherwise>
						<em>No message sets defined.</em>
					</xsl:otherwise>
				</xsl:choose>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="entry">
		<h2>Details for this entry</h2>
		<table type="entry">
			<tr>
				<th>ID</th>
				<td>
					<xsl:value-of select="@id" />
				</td>
			<tr>
			</tr>
				<th>Description</th>
				<td>
					<xsl:apply-templates select="description" />
				</td>
			<tr>
			</tr>
				<th>Level</th>
				<td>
					<xsl:value-of select="@level" />
				</td>
			<tr>
			</tr>
				<th>Category</th>
				<td>
					<xsl:value-of select="@category" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="messageset">
		<xsl:variable name="messageset_doc">
			<xsl:value-of select="$sourcedir" />
			<xsl:text>/messages-</xsl:text>
			<xsl:value-of select="@id" />
			<xsl:text>.xml</xsl:text>
		</xsl:variable>

		<tr>
			<th>
				<xsl:value-of select="@id" />
			</th>
			<td>
				<xsl:value-of select="document($messageset_doc)/messages/message[@entry=$entry]" />
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
