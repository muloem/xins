<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="project_home" />
	<xsl:param name="environment" />

	<xsl:variable name="api"          select="//function/@api" />
	<xsl:variable name="project_file" select="concat($project_home, '/xins-project.xml')" />

	<xsl:output
	method="xml"
	indent="no"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:include href="../header.xslt" />
	<xsl:include href="../footer.xslt" />
	<xsl:include href="../firstline.xslt" />

	<xsl:variable name="env_url" select="document($project_file)/project/environment[@id=$environment]/@url" />

	<xsl:template match="function">

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:value-of select="@name" />
					<xsl:text> test form</xsl:text>
				</title>
				<!-- TODO: Use separate stylesheet? -->
				<link rel="stylesheet" type="text/css" href="../style.css" />
				<link rel="top" href="../index.html" title="API index" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">testform</xsl:with-param>
					<xsl:with-param name="name">
						<xsl:value-of select="@name" />
					</xsl:with-param>
				</xsl:call-template>

				<h1>
					<xsl:text>Function </xsl:text>
					<em>
						<xsl:value-of select="@name" />
					</em>
					<xsl:text> test form</xsl:text>
				</h1>

				<p>
					<xsl:text>This form can be used to test the </xsl:text>
					<a>
						<xsl:attribute name="href">
							<xsl:value-of select="$env_url" />
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="$env_url" />
						</xsl:attribute>
						<xsl:value-of select="$environment" />
					</a>
					<xsl:text> environment.</xsl:text>
				</p>

				<xsl:call-template name="input_section" />
				<xsl:call-template name="footer" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="em">
		<em>
			<xsl:apply-templates />
		</em>
	</xsl:template>

	<xsl:template match="strong">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<xsl:template match="list">
		<ul>
			<xsl:apply-templates />
		</ul>
	</xsl:template>

	<xsl:template match="list/item">
		<li>
			<xsl:apply-templates />
		</li>
	</xsl:template>

	<xsl:template name="input_section">
		<h2>Test form</h2>
		<form method="GET">
			<xsl:attribute name="action">
				<xsl:value-of select="$env_url" />
			</xsl:attribute>
			<input name="function" type="hidden">
				<xsl:attribute name="value">
					<xsl:value-of select="@name" />
				</xsl:attribute>
			</input>
			<xsl:choose>
				<xsl:when test="input/param">
					<table>
						<xsl:apply-templates select="input/param" />
						<tr>
							<td colspan="2">
								<input type="submit" name="submit" value="Submit" />
							</td>
						</tr>
					</table>
				</xsl:when>
				<xsl:otherwise>
					<em>This function supports no input parameters.</em>
					<p>
						<input type="submit" name="submit" value="Submit" />
					</p>
				</xsl:otherwise>
			</xsl:choose>
		</form>
	</xsl:template>

	<xsl:template match="param">

		<xsl:variable name="type">
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:value-of select="@type" />
				</xsl:when>
				<xsl:otherwise>text</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="type_file" select="concat($project_home, '/src/specs/', $api, '/', $type, '.typ')" />

		<xsl:variable name="isenum">
			<xsl:choose>
				<xsl:when test="document($type_file)/type/enum">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- TODO: Deprecated parameters -->
		<!-- TODO: Move 'typelink' template to typelink.xslt? -->

		<tr>
			<td class="name">
				<span>
					<xsl:if test="boolean(description/text())">
						<xsl:attribute name="title">
							<xsl:call-template name="firstline">
								<xsl:with-param name="text">
									<xsl:value-of select="description/text()" />
								</xsl:with-param>
							</xsl:call-template>
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="@name" />
				</span>
				<xsl:text> (</xsl:text>
				<xsl:call-template name="typelink">
					<xsl:with-param name="type" select="$type" />
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</td>
			<td class="value">
				<xsl:choose>
					<xsl:when test="$isenum = 'true'">
						<select>
							<xsl:attribute name="name">
								<xsl:value-of select="@name" />
							</xsl:attribute>
							<xsl:attribute name="class">
								<xsl:choose>
									<xsl:when test="@required = 'true'">required</xsl:when>
									<xsl:otherwise>optional</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<option></option>
							<xsl:for-each select="document($type_file)/type/enum/item">
								<option>
									<xsl:attribute name="value">
										<xsl:value-of select="@value" />
									</xsl:attribute>
									<xsl:choose>
										<xsl:when test="@name">
											<xsl:value-of select="@name" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="@value" />
										</xsl:otherwise>
									</xsl:choose>
								</option>
							</xsl:for-each>
						</select>
					</xsl:when>
					<xsl:otherwise>
						<input type="text">
							<xsl:attribute name="name">
								<xsl:value-of select="@name" />
							</xsl:attribute>
							<xsl:attribute name="class">
								<xsl:choose>
									<xsl:when test="@required = 'true'">required</xsl:when>
									<xsl:otherwise>optional</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="@required = 'true'"> *</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="typelink">
		<xsl:param name="type" />

		<xsl:variable name="type_file">
			<xsl:value-of select="concat($project_home, '/src/specs/', $api, '/', $type, '.typ')" />
		</xsl:variable>

		<xsl:variable name="type_url">
			<xsl:text>../types/</xsl:text>
			<xsl:value-of select="$type" />
			<xsl:text>.html</xsl:text>
		</xsl:variable>

		<xsl:variable name="type_title">
			<xsl:call-template name="firstline">
				<xsl:with-param name="text">
					<xsl:value-of select="document($type_file)/type/description/text()" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="not(boolean(document($type_file)))">
			<xsl:message terminate="yes">
				<xsl:text>The type '</xsl:text>
				<xsl:value-of select="$type" />
				<xsl:text>' does not exist.</xsl:text>
			</xsl:message>
		</xsl:if>

		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="$type_url" />
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="$type_title" />
			</xsl:attribute>
			<xsl:value-of select="$type" />
		</a>
	</xsl:template>

</xsl:stylesheet>
