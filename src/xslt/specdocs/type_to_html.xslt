<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the type.html files that conatins
 the description of the type.

 $Id$

 Copyright 2004 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
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
	method="html"
	indent="yes"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:include href="broken_freeze.xslt" />
	<xsl:include href="../header.xslt"     />
	<xsl:include href="../footer.xslt"     />
	<xsl:include href="../types.xslt"      />
	<xsl:include href="../urlencode.xslt"  />

	<xsl:template match="type">

		<xsl:variable name="type_name"    select="@name" />

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:text>Type </xsl:text>
					<xsl:value-of select="@name" />
				</title>

				<meta name="generator" content="XINS" />

				<link rel="stylesheet" type="text/css" href="style.css"                               />
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

				<br />

				<!-- Broken freezes -->
				<xsl:call-template name="broken_freeze">
					<xsl:with-param name="project_home" select="$project_home" />
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="api" select="$api" />
					<xsl:with-param name="api_file" select="$api_file" />
					<xsl:with-param name="frozen_version" select="document($api_file)/api/type[@name=$type_name]/@freeze" />
					<xsl:with-param name="broken_file" select="concat($type_name, '.typ')" />
				</xsl:call-template>

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

				<xsl:apply-templates select="enum"       />
				<xsl:apply-templates select="pattern"    />
				<xsl:apply-templates select="properties" />
				<xsl:apply-templates select="int8"       />
				<xsl:apply-templates select="int16"      />
				<xsl:apply-templates select="int32"      />
				<xsl:apply-templates select="int64"      />
				<xsl:apply-templates select="float32"    />
				<xsl:apply-templates select="float64"    />
				<xsl:apply-templates select="base64"    />
				<xsl:apply-templates select="list"       />
				<xsl:apply-templates select="set"        />

				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="enum">
		<xsl:if test="item">
			<p />
			This is an enumeration type.<br />
			The only possible items are:
			<table class="typelist">
				<tr>
					<th>Name</th>
					<th>Value</th>
				</tr>
				<xsl:apply-templates select="item" />
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="enum/item">
		<tr>
			<td>
				<xsl:choose>
					<xsl:when test="@name">
						<xsl:value-of select="@name" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@value" />
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td>
				<xsl:value-of select="@value" />
			</td>
		</tr>
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
		<p />
		<!-- If no pattern URL is provided, use the default one on sourceforge. -->
		<xsl:variable name="pattern_url">
			<xsl:choose>
				<xsl:when test="document($project_file)/project/patterntest">
					<xsl:value-of select="document($project_file)/project/patterntest/@href" />
				</xsl:when>
				<xsl:otherwise>http://www.xins.org/patterntest.php</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="$pattern_url" />
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

	<xsl:template match="int8 | int16 | int32 | int64 | float32 | float64">
		<p />
		This is a <em>
		<xsl:value-of select="name()" />
		type</em>.<br/>
		<xsl:if test="@min">
			<xsl:text>The minimum value is </xsl:text>
			<xsl:value-of select="@min" />
			<xsl:text>.</xsl:text><br />
		</xsl:if>
		<xsl:if test="@max">
			<xsl:text>The maximum value is </xsl:text>
			<xsl:value-of select="@max" />
			<xsl:text>.</xsl:text><br />
		</xsl:if>
	</xsl:template>

	<xsl:template match="base64">
		<p />
		This is a <em>
		<xsl:value-of select="name()" />
		type</em>.<br/>
		<xsl:if test="@min">
			<xsl:text>The minimum size is </xsl:text>
			<xsl:value-of select="@min" />
			<xsl:text> bytes.</xsl:text><br />
		</xsl:if>
		<xsl:if test="@max">
			<xsl:text>The maximum size is </xsl:text>
			<xsl:value-of select="@max" />
			<xsl:text> bytes.</xsl:text><br />
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="list | set">
		<p />
		<xsl:text>This is a </xsl:text>
		<em>
		<xsl:value-of select="name()" />
		type</em>
		<xsl:text>. The elements must conform to the type </xsl:text>
		<xsl:call-template name="typelink">
			<xsl:with-param name="api"      select="$api"      />
			<xsl:with-param name="specsdir" select="$specsdir" />
			<xsl:with-param name="type"     select="@type" />
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
