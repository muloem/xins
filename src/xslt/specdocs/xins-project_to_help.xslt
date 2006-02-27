<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the help page.

 $Id$

 Copyright 2003-2006 Wanadoo Nederland B.V.
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

	<xsl:key name="keywords" match="keyword" use="@term" />

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
				<p>This page explains how the information presented in these specifications should be interpreted.</p>

				<h2>Examples</h2>
				<p>The examples are not normative and should not be interpreted literally.</p>
 
				<p>Examples of how real result documents can be different from the examples include:</p>
				<ul>
					<li>the encoding in the result XML document may be different from the one displayed in the example</li>
					<li>there can be different (additional or less) ignorable whitespace</li>
					<li>there can be additional undescribed attributes set</li>
					<li>there can be additional undescribed elements</li>
				</ul>				
				
				<h2>Parsing</h2>
				<p>An XML parser should be used to interpret the response.</p>

				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="terminology">
		<h2>Terminology</h2>
		<p>The following keywords from <a href="http://www.faqs.org/rfcs/rfc2119.html"><acronym title="Request For Comments">RFC</acronym> 2119</a> are used in the definition of the requirements:</p>
		<table class="functionlist">
			<thead>
				<tr>
					<th>Term</th>
					<th>Definition</th>
				</tr>
			</thead>
			<tbody>
				<xsl:for-each select="//cc-spec/requirements/group/rule/keyword[generate-id() = generate-id(key('keywords', @term))]">
					<xsl:sort select="@term" />
					<tr>
						<td class="keyword">
							<xsl:variable name="key" select="translate(@term, ' ', '_')" />
							<span id="{$key}">
								<xsl:value-of select="@term" />
							</span>
						</td>
						<td>
							<xsl:call-template name="keyword-definition">
								<xsl:with-param name="term" select="@term" />
							</xsl:call-template>
						</td>
					</tr>
				</xsl:for-each>
			</tbody>
		</table>

		<p>Additionally, the following terminology is used:</p>
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
		<xsl:variable name="term" select="@term" />
		<xsl:variable name="def">
			<xsl:call-template name="keyword-definition">
				<xsl:with-param name="term" select="@term" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="key" select="translate($term, ' ', '_')" />

		<a class="keyword" href="#{$key}">
			<acronym title="{$def}">
				<xsl:value-of select="$term" />
			</acronym>
		</a>
	</xsl:template>

	<xsl:template name="keyword-definition">
		<xsl:param name="term" />

		<xsl:choose>
			<xsl:when test="$term='MUST'       or $term = 'REQUIRED' or $term = 'SHALL'">This word means that the definition is an absolute requirement of the specification</xsl:when>
			<xsl:when test="$term='MUST NOT'   or $term = 'SHALL NOT'"                  >These words mean that the definition is an absolute prohibition of the specification</xsl:when>
			<xsl:when test="$term='SHOULD'     or $term = 'RECOMMENDED'"                >This word means that there may exist valid reasons in particular circumstances to ignore a particular item, but the full implications must be understood and carefully weighed before choosing a different course</xsl:when>
			<xsl:when test="$term='SHOULD NOT' or $term = 'NOT RECOMMENDED'"            >These words mean that there may exist valid reasons in particular circumstances when the particular behavior is acceptable or even useful, but the full implications should be understood and the case carefully weighed before implementing any behavior described with this label</xsl:when>
			<xsl:when test="$term='MAY'        or $term = 'OPTIONAL'"                   >This word means that an item is truly optional. One vendor may choose to include the item because a particular marketplace requires it or because the vendor feels that it enhances the product while another vendor may omit the same item. An implementation which does not include a particular option MUST be prepared to interoperate with another implementation which does include the option, though perhaps with reduced functionality. In the same vein an implementation which does include a particular option MUST be prepared to interoperate with another implementation which does not include the option (except, of course, for the feature the option provides)</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>Unknown RFC-2119 keyword: "</xsl:text>
					<xsl:value-of select="$term" />
					<xsl:text>"</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
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
