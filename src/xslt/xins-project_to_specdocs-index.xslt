<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="project_home" />

	<xsl:output
	method="xml"
	indent="no"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:include href="header.xslt" />
	<xsl:include href="footer.xslt" />
	<xsl:include href="firstline.xslt" />

	<xsl:template match="project">
		<html>
			<head>
				<title>API index</title>
				<link rel="stylesheet" href="style.css" type="text/css" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">apilist</xsl:with-param>
				</xsl:call-template>

				<h1>API index</h1>
				<p>The following API&apos;s are specified:</p>
				<table class="apilist">
					<tr>
						<th>API</th>
						<th>Description</th>
						<th>Function count</th>
					</tr>
					<xsl:apply-templates select="api" />
				</table>

				<xsl:call-template name="footer" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="api">
		<xsl:variable name="path" select="concat($project_home, '/src/specs/', @name, '/api.xml')" />
		<xsl:variable name="functioncount" select="count(document($path)/api/function)" />

		<xsl:if test="not(document($path)/api/@name = @name)">
			<xsl:message terminate="yes">
				<xsl:text>API name specified in project.xml ('</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>') does not match the name specified in </xsl:text>
				<xsl:value-of select="$path" />
				<xsl:text> ('</xsl:text>
				<xsl:value-of select="document($path)/api/@name" />
				<xsl:text>').</xsl:text>
			</xsl:message>
		</xsl:if>
		<tr>
			<td>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@name" />
						<xsl:text>/api.html</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="@name" />
				</a>
			</td>      

			<td>
				<xsl:apply-templates select="document($path)/api/description" />
			</td>

			<td>
				<xsl:value-of select="$functioncount" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="api/description">
		<xsl:call-template name="firstline">
			<xsl:with-param name="text" select="text()" />
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
