<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api"          />

	<xsl:output
	method="xml"
	indent="no"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:include href="../header.xslt" />
	<xsl:include href="../footer.xslt" />

	<xsl:template match="resultcode">

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:text>Result code </xsl:text>
					<xsl:value-of select="@name" />
				</title>

				<meta name="generator" content="XINS" />

				<link rel="stylesheet" type="text/css" href="../style.css"                               />
				<link rel="top"                        href="../index.html" title="API index"            />
				<link rel="up"                         href="index.html"    title="Overview of this API" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">resultcode</xsl:with-param>
				</xsl:call-template>

				<h1>
					<xsl:text>Result code </xsl:text>
					<em>
						<xsl:value-of select="@name" />
					</em>
				</h1>

				<xsl:apply-templates select="description" />
				<xsl:call-template name="footer" />
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
