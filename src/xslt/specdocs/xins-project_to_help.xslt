<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the help page.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />

	<xsl:output
	method="html"
	indent="yes"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:include href="header.xslt"       />
	<xsl:include href="footer.xslt"       />
	<xsl:include href="../firstline.xslt" />

	<xsl:template match="project">
		<html>
			<head>
				<title>Help</title>
				<meta name="generator" content="XINS" />
				<link rel="stylesheet" href="style.css" type="text/css" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">help</xsl:with-param>
				</xsl:call-template>

				<h1>Help</h1>
				<p>This page explains how the information presented in these
                specifications should be interpreted.</p>
				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
