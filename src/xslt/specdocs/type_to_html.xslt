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

	<xsl:include href="../header.xslt" />
	<xsl:include href="../footer.xslt" />

	<xsl:template match="type">

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:text>Type </xsl:text>
					<xsl:value-of select="@name" />
				</title>
				<link rel="stylesheet" type="text/css" href="../style.css" />
				<link rel="top" href="../index.html" title="API index" />
				<link rel="up" href="index.html" title="Type index" />
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

				<xsl:if test="boolean(@extends)">
					<blockquote>
						<pre>
							<xsl:call-template name="extends_tree">
								<xsl:with-param name="type_name">
									<xsl:value-of select="@name" />
								</xsl:with-param>
								<xsl:with-param name="supertype_name">
									<xsl:value-of select="@extends" />
								</xsl:with-param>
							</xsl:call-template>
						</pre>
					</blockquote>
					<br />
				</xsl:if>

				<xsl:apply-templates select="description" />

				<xsl:if test="boolean(@extends) or boolean(see)">
					<table class="metadata">
						<xsl:if test="boolean(@extends)">
							<tr>
								<td class="key">Extends:</td>
								<td class="value">
									<a>
										<xsl:attribute name="href">
											<xsl:value-of select="@extends" />
											<xsl:text>.html</xsl:text>
										</xsl:attribute>
										<xsl:value-of select="@extends" />
									</a>
								</td>
							</tr>
						</xsl:if>
						<xsl:if test="boolean(see)">
							<tr>
								<td class="key">See also:</td>
								<td class="value">
									<xsl:apply-templates select="see" />
								</td>
							</tr>
						</xsl:if>
					</table>
				</xsl:if>

				<xsl:apply-templates select="enum" />

				<xsl:call-template name="footer" />
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

	<xsl:template name="extends_tree">
		<xsl:param name="type_name" />
		<xsl:param name="supertype_name" />

		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="supertype_name" />
				<xsl:text>.html</xsl:text>
			</xsl:attribute>
			<xsl:value-of select="$supertype_name" />
		</a>
		<br />
		<xsl:text> |</xsl:text>
		<br />
		<xsl:text> +--</xsl:text>
		<xsl:value-of select="$type_name" />
	</xsl:template>
</xsl:stylesheet>
