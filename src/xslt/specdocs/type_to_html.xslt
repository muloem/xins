<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:output
	method="xml"
	indent="no"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:include href="../header.xslt"    />
	<xsl:include href="../footer.xslt"    />
	<xsl:include href="../types.xslt"     />
	<xsl:include href="../urlencode.xslt" />

	<xsl:template match="type">

		<xsl:variable name="supertype">
			<xsl:choose>
				<xsl:when test="string-length(@extends) &gt; 0">
					<xsl:value-of select="@extends" />
				</xsl:when>
				<xsl:when test="count(properties) = 1">_properties</xsl:when>
				<xsl:otherwise>_text</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:text>Type </xsl:text>
					<xsl:value-of select="@name" />
				</title>

				<meta name="generator" content="XINS" />

				<link rel="stylesheet" type="text/css" href="../style.css"                               />
				<link rel="top"                        href="../index.html" title="API index"            />
				<link rel="up"                         href="index.html"    title="Overview of this API" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">type</xsl:with-param>
				</xsl:call-template>

				<h1>
					<xsl:text>Type </xsl:text>
					<em>
						<xsl:value-of select="@name" />
					</em>
				</h1>

				<blockquote>
					<pre>
						<xsl:call-template name="extends_tree">
							<xsl:with-param name="type_name">
								<xsl:value-of select="@name" />
							</xsl:with-param>
							<xsl:with-param name="supertype_name">
								<xsl:value-of select="$supertype" />
							</xsl:with-param>
						</xsl:call-template>
					</pre>
				</blockquote>
				<br />

				<xsl:apply-templates select="description" />

				<xsl:if test="boolean(see)">
					<table class="metadata">
						<tr>
							<td class="key">See also:</td>
							<td class="value">
								<xsl:apply-templates select="see" />
							</td>
						</tr>
					</table>
				</xsl:if>

				<xsl:if test="not(enum or pattern or properties)">
					<xsl:message terminate="yes">
						<xsl:text>Type </xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text> defines neither an enum nor a pattern nor properties.</xsl:text>
					</xsl:message>
				</xsl:if>

				<xsl:if test="enum and pattern">
					<xsl:message terminate="yes">
						<xsl:text>Type </xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text> defines both an enum and a pattern.</xsl:text>
					</xsl:message>
				</xsl:if>

				<xsl:if test="enum and properties">
					<xsl:message terminate="yes">
						<xsl:text>Type </xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text> defines both an enum and properties.</xsl:text>
					</xsl:message>
				</xsl:if>

				<xsl:if test="pattern and properties">
					<xsl:message terminate="yes">
						<xsl:text>Type </xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text> defines both an pattern and properties.</xsl:text>
					</xsl:message>
				</xsl:if>

				<xsl:apply-templates select="enum"       />
				<xsl:apply-templates select="pattern"    />
				<xsl:apply-templates select="properties" />

				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="enum">
		<xsl:if test="item">
			<p />
			<xsl:text>This is an enumeration type. The only possible values are:</xsl:text>
			<ul>
				<xsl:apply-templates select="item" />
			</ul>
		</xsl:if>
	</xsl:template>

	<xsl:template match="enum/item">
		<li>
			<xsl:value-of select="@value" />
		</li>
	</xsl:template>

	<xsl:template match="pattern">
		<p />
		<xsl:text>This is a </xsl:text>
		<em>pattern type</em>
		<xsl:text>. Allowed values must match the following pattern:</xsl:text>
		<blockquote>
			<code>
				<xsl:value-of select="text()" />
			</code>
		</blockquote>
		<xsl:if test="document($project_file)/project/patterntest">
			<xsl:variable name="pattern" select="text()" />
			<p />
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="document($project_file)/project/patterntest/@href" />
					<xsl:text>?pattern=</xsl:text>
					<xsl:call-template name="urlencode">
						<xsl:with-param name="text">
							<xsl:text>^(</xsl:text>
							<xsl:value-of select="text()" />
							<xsl:text>)$</xsl:text>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:text>Test this pattern</xsl:text>
			</a>
			<xsl:text>.</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="properties">
		<p />
		<xsl:text>This is a </xsl:text>
		<em>properties type</em>
		<xsl:text>. Property names must conform to the </xsl:text>
		<xsl:call-template name="typelink">
			<xsl:with-param name="api"      select="$api"      />
			<xsl:with-param name="specsdir" select="$specsdir" />
			<xsl:with-param name="type"     select="@nameType" />
		</xsl:call-template>
		<xsl:text> type. Property values must conform to the </xsl:text>
		<xsl:call-template name="typelink">
			<xsl:with-param name="api"      select="$api"       />
			<xsl:with-param name="specsdir" select="$specsdir"  />
			<xsl:with-param name="type"     select="@valueType" />
		</xsl:call-template>
		<xsl:text> type.</xsl:text>
	</xsl:template>

	<xsl:template name="extends_tree">
		<xsl:param name="type_name" />
		<xsl:param name="supertype_name" />

		<xsl:call-template name="typelink">
			<xsl:with-param name="api"      select="$api"      />
			<xsl:with-param name="specsdir" select="$specsdir" />
			<xsl:with-param name="type"     select="$supertype_name" />
		</xsl:call-template>
		<br />
		<xsl:text> |</xsl:text>
		<br />
		<xsl:text> +--</xsl:text>
		<xsl:value-of select="$type_name" />
	</xsl:template>
</xsl:stylesheet>
