<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the function-testform-environment HTML form.
 This form is use to test an API on a given environment.

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="environment"  />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../footer.xslt" />
	<xsl:include href="../function.xslt"  />
	<xsl:include href="../header.xslt" />
	<xsl:include href="../types.xslt" />

	<xsl:variable name="env_url" select="document($api_file)/api/environment[@id=$environment]/@url" />
	<xsl:variable name="functionName" select="//function/@name" />
	<xsl:variable name="sessionBased">
		<xsl:for-each select="//function">
			<xsl:call-template name="is_function_session_based" />
		</xsl:for-each>
	</xsl:variable>

	<xsl:output
	method="xml"
	indent="no"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:template match="function">

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:value-of select="$functionName" />
					<xsl:text> test form</xsl:text>
				</title>
				<link rel="stylesheet" type="text/css" href="style.css" />
				<link rel="top" href="../index.html" title="API index" />
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">testform</xsl:with-param>
					<xsl:with-param name="name">
						<xsl:value-of select="$functionName" />
					</xsl:with-param>
				</xsl:call-template>

				<h1>
					<xsl:text>Function </xsl:text>
					<em>
						<xsl:value-of select="$functionName" />
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
				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
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
					<xsl:value-of select="$functionName" />
				</xsl:attribute>
			</input>
			<xsl:choose>
				<xsl:when test="input/param or $sessionBased = 'true'">
					<table>
						<xsl:if test="$sessionBased = 'true'">
							<tr>
								<td class="name">
									<span title="session identifier">_session</span>
								</td>
								<td class="value">
									<input type="text" name="_session" class="required" />
									<xsl:text> *</xsl:text>
								</td>
							</tr>
						</xsl:if>
						<xsl:apply-templates select="input/param" />
						<tr>
							<td colspan="2">
								<input type="submit" name="submit" value="Submit" />
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<hr />
								<xsl:text>Control of the </xsl:text>
								<em>
									<xsl:value-of select="$functionName" />
								</em>
								<xsl:text> function on the </xsl:text>
								<em>
									<xsl:value-of select="$environment" />
								</em>
								<xsl:text>: </xsl:text>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$env_url" />
										<xsl:text>?_function=_DisableFunction&amp;functionName=</xsl:text>
										<xsl:value-of select="$functionName" />
									</xsl:attribute>
									<xsl:text>Disable</xsl:text>
								</a>
								<xsl:text> | </xsl:text>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="$env_url" />
										<xsl:text>?_function=_EnableFunction&amp;functionName=</xsl:text>
										<xsl:value-of select="$functionName" />
									</xsl:attribute>
									<xsl:text>Enable</xsl:text>
								</a>
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
				<xsl:otherwise>_text</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="type_file" select="concat($specsdir, '/', $api, '/', $type, '.typ')" />

		<xsl:variable name="isenum">
			<xsl:choose>
				<xsl:when test="starts-with($type, '_')">false</xsl:when> <!-- TODO -->
				<xsl:when test="document($type_file)/type/enum">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- TODO: Deprecated parameters -->

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
					<xsl:with-param name="api"      select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="type"     select="$type" />
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</td>
			<td class="value">
				<xsl:choose>
					<xsl:when test="$isenum = 'true'">
						<select name="{@name}">
							<xsl:attribute name="class">
								<xsl:choose>
									<xsl:when test="@required = 'true'">required</xsl:when>
									<xsl:otherwise>optional</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<option></option>
							<xsl:for-each select="document($type_file)/type/enum/item">
								<option value="{@value}">
									<xsl:value-of select="@value" />
								</option>
							</xsl:for-each>
						</select>
					</xsl:when>
					<xsl:when test="$type = '_boolean'">
						<select name="{@name}">
							<option></option>
							<option value="true">true</option>
							<option value="false">false</option>
						</select>
					</xsl:when>
					<xsl:otherwise>
						<input type="text" name="{@name}">
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
</xsl:stylesheet>
