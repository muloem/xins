<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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
				<title>Log documentation</title>
				<meta name="generator" content="logdoc" />
				<link rel="stylesheet" type="text/css" href="style.css" />
			</head>
			<body>
				<h1>Log documentation</h1>

				<h2>Message sets</h2>
				The following message sets are available:
				<ul>
					<li>RAW (default)</li>
					<xsl:for-each select="messageset">
						<li>
							<xsl:value-of select="@id" />
						</li>
					</xsl:for-each>
				</ul>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
