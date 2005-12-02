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

				<xsl:apply-templates select="document('../../xml/cc-spec/xins-std/cc-spec.xml')/cc-spec" />

				<h2>Notes on the examples</h2>
				<p>The examples are non-normative and should not be
				interpreted literally. However, both clients are server must
				respect the outlined requirements.</p>
				<p>For example, the encoding in the result XML document may be
				different from the one displayed in the example, there can be
				additional or less ignorable whitespace, there can be
				ignorable attributes or elements, etc.</p>
				<p>An XML parser should be used to interpret the
				response.</p>

				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="terminology">
		<h2>Terminology</h2>
		<p>The following terminology is used in the definition of the
		requirements:</p>
		<table class="functionlist">
			<thead>
				<tr>
					<th>Term</th>
					<th>Definition</th>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="terminology/term">
		<tr>
			<td>
				<xsl:value-of select="@name" />
			</td>
			<td>
				<xsl:apply-templates />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="keyword">
		<small>
			<xsl:apply-templates />
		</small>
	</xsl:template>

	<xsl:template match="code">
		<code>
			<xsl:apply-templates />
		</code>
	</xsl:template>

	<xsl:template match="requirements">
		<h2>Requirements</h2>
		<p>The XINS calling convention is defined in terms of client- and
		server-side requirements.</p>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="requirements/group">
		<h3>
			<xsl:choose>
				<xsl:when test="@side = 'client'">Client-side</xsl:when>
				<xsl:when test="@side = 'server'">Server-side</xsl:when>
				<xsl:otherwise>General</xsl:otherwise>
			</xsl:choose>
			<xsl:text> requirements: </xsl:text>
			<em>
				<xsl:apply-templates select="title" />
			</em>
		</h3>
		<p>
			<xsl:apply-templates select="description" />
		</p>
		<table class="functionlist">
			<thead>
				<tr>
					<th><acronym title="Unique requirement identifier">ID</acronym></th>
					<th>Definition</th>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select="rule" />
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="rule">
		<tr>
			<td>
				<xsl:value-of select="../@prefix" />
				<xsl:text>_</xsl:text>
				<xsl:number />
			</td>
			<td>
				<xsl:apply-templates />
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>
