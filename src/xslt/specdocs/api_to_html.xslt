<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the index.html of the specification documentation.

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

	<xsl:variable name="old_authors_file" select="concat($project_home, '/src/authors/authors.xml')" />
	<xsl:variable name="authors_file">
		<xsl:choose>
			<xsl:when test="document($old_authors_file)">
				<xsl:value-of select="$old_authors_file" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat($project_home, '/authors.xml')" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="sessionBased">
		<xsl:choose>
			<xsl:when test="boolean(//api/session-based)">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="sessionTimeout">
		<xsl:if test="$sessionBased = 'true'">
			<xsl:value-of select="//api/session-based/@timeout" />
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="sessionTimeoutPrecision">
		<xsl:if test="$sessionBased = 'true'">
			<xsl:value-of select="//api/session-based/@precision" />
		</xsl:if>
	</xsl:variable>

	<xsl:output
	method="html"
	indent="yes"
	encoding="US-ASCII"
	doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
	omit-xml-declaration="yes" />

	<xsl:include href="../header.xslt" />
	<xsl:include href="../footer.xslt" />
	<xsl:include href="../firstline.xslt" />

	<xsl:template match="api">

		<xsl:variable name="owner">
			<xsl:if test="boolean(@owner) and not(owner = '')">
				<xsl:choose>
					<xsl:when test="document($authors_file)/authors/author[@id=current()/@owner]">
						<xsl:value-of select="@owner" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">
							<xsl:text>Unable to find API owner '</xsl:text>
							<xsl:value-of select="@owner" />
							<xsl:text>' in </xsl:text>
							<xsl:value-of select="$authors_file" />
							<xsl:text>.</xsl:text>
						</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="prevcount" select="count(document($project_file)/project/api[@name = $api]/preceding::api)" />
		<xsl:variable name="prev"      select="document($project_file)/project/api[$prevcount]/@name" />
		<xsl:variable name="prev_url"  select="concat('../', $prev, '/index.html')" />
		<xsl:variable name="next"      select="document($project_file)/project/api[@name = $api]/following-sibling::api/@name" />
		<xsl:variable name="next_url"  select="concat('../', $next, '/index.html')" />

		<xsl:variable name="prev_title">
			<xsl:if test="boolean($prev) and not($prev = '')">
				<xsl:value-of select="$prev" />
				<xsl:text> API</xsl:text>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="next_title">
			<xsl:if test="boolean($next) and not($next = '')">
				<xsl:value-of select="$next" />
				<xsl:text> API</xsl:text>
			</xsl:if>
		</xsl:variable>

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:text>API overview: </xsl:text>
					<xsl:value-of select="@name" />
				</title>

				<meta name="generator" content="XINS" />

				<link rel="stylesheet" type="text/css" href="style.css"                    />
				<link rel="top"                        href="../index.html" title="API index" />
				<link rel="up"                         href="../index.html" title="API index" />
				<link rel="first">
					<xsl:attribute name="href">
						<xsl:text>../</xsl:text>
						<xsl:value-of select="document($project_file)/project/api[1]/@name" />
						<xsl:text>/index.html</xsl:text>
					</xsl:attribute>
				</link>
				<xsl:if test="boolean($prev) and not($prev = '')">
					<link rel="prev">
						<xsl:attribute name="href">
							<xsl:value-of select="$prev_url" />
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="$prev_title" />
						</xsl:attribute>
					</link>
				</xsl:if>
				<xsl:if test="boolean($next) and not($next = '')">
					<link rel="next">
						<xsl:attribute name="href">
							<xsl:value-of select="$next_url" />
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="$next_title" />
						</xsl:attribute>
					</link>
				</xsl:if>
			</head>
			<body>
				<xsl:call-template name="header">
					<xsl:with-param name="active">api</xsl:with-param>
					<xsl:with-param name="prev" select="$prev_title" />
					<xsl:with-param name="next" select="$next_title" />
					<xsl:with-param name="prev_url" select="$prev_url" />
					<xsl:with-param name="next_url" select="$next_url" />
				</xsl:call-template>

				<h1>
					<xsl:text>API overview </xsl:text>
					<em>
						<xsl:value-of select="@name" />
					</em>
				</h1>

				<xsl:apply-templates select="description" />

				<xsl:if test="$sessionBased = 'true'">
					<p />
					<xsl:text>This API is session-based. The session time-out is set to </xsl:text>
					<xsl:value-of select="$sessionTimeout" />
					<xsl:text> minute</xsl:text>
					<xsl:if test="not ($sessionTimeout = 1)">s</xsl:if>
					<xsl:text>, precision is </xsl:text>
					<xsl:value-of select="$sessionTimeoutPrecision" />
					<xsl:text> minute</xsl:text>
					<xsl:if test="not ($sessionTimeoutPrecision = 1)">s</xsl:if>
					<xsl:text>. The functions marked with an asterisk (*) are session-based.</xsl:text>
				</xsl:if>

				<h2>Functions</h2>
				<xsl:choose>
					<xsl:when test="count(function) = 0">
						<p>
							<em>This API defines no functions.</em>
						</p>
					</xsl:when>
					<xsl:otherwise>
						<table class="functionlist">
							<tr>
								<th>Function</th>
								<th>Version</th>
								<th>Status</th>
								<th>Description</th>
							</tr>
							<xsl:apply-templates select="function" />
						</table>
					</xsl:otherwise>
				</xsl:choose>

				<h2>Types</h2>
				<xsl:choose>
					<xsl:when test="count(type) = 0">
						<p>
							<em>This API defines no types.</em>
						</p>
					</xsl:when>
					<xsl:otherwise>
						<table class="functionlist">
							<tr>
								<th>Type</th>
								<th>Version</th>
								<th>Status</th>
								<th>Description</th>
							</tr>
							<xsl:apply-templates select="type" />
						</table>
					</xsl:otherwise>
				</xsl:choose>

				<h2>Result codes</h2>
				<xsl:choose>
					<xsl:when test="count(resultcode) = 0">
						<p>
							<em>This API defines no specific result codes.</em>
						</p>
					</xsl:when>
					<xsl:otherwise>
						<table class="functionlist">
							<tr>
								<th>Result code</th>
								<th>Version</th>
								<th>Status</th>
								<th>Description</th>
							</tr>
							<xsl:apply-templates select="resultcode" />
						</table>
					</xsl:otherwise>
				</xsl:choose>

				<h2>API Owner</h2>
				<xsl:choose>
					<xsl:when test="$owner != ''">
						<xsl:variable name="owner_name">
							<xsl:value-of select="document($authors_file)/authors/author[@id=$owner]/@name" />
						</xsl:variable>
						<xsl:variable name="owner_email">
							<xsl:value-of select="document($authors_file)/authors/author[@id=$owner]/@email" />
						</xsl:variable>

						<p>
							<xsl:value-of select="$owner_name" />
							<xsl:text> (</xsl:text>
							<a href="mailto:{$owner_email}">
								<xsl:value-of select="$owner_email" />
							</a>
							<xsl:text>)</xsl:text>
						</p>
					</xsl:when>
					<xsl:otherwise>
						<p>
							<em>No API Owner has been assigned to this API.</em>
						</p>
					</xsl:otherwise>
				</xsl:choose>

				<h2>Environments</h2>
				<xsl:choose>
					<xsl:when test="environment">
						<ul>
							<xsl:apply-templates select="environment" />
						</ul>
					</xsl:when>
					<xsl:when test="document($project_file)/projects/api[@name = $api]/environments">
						<ul>
							<xsl:variable name="env_file" select="concat($project_home, '/apis/', $api, '/environments.xml')" />
							<xsl:apply-templates select="document($env_file)/environments/environment" />
						</ul>
					</xsl:when>
					<xsl:otherwise>
						<p>
							<em>No environments have been defined for this API.</em>
						</p>
					</xsl:otherwise>
				</xsl:choose>

				<xsl:call-template name="footer">
					<xsl:with-param name="xins_version" select="$xins_version" />
				</xsl:call-template>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="environment">
		<li>
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="@url" />
				</xsl:attribute>
				<xsl:value-of select="@id" />
			</a>
			<!-- Generate the ( version statistics settings ) links. -->
			<font size="-1">(
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@url" />
						<xsl:text>?_function=_GetVersion</xsl:text>
					</xsl:attribute>
					version
				</a>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@url" />
						<xsl:text>?_function=_GetStatistics</xsl:text>
					</xsl:attribute>
					statistics
				</a>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@url" />
						<xsl:text>?_function=_GetSettings</xsl:text>
					</xsl:attribute>
					settings
				</a>
				)
			</font>
		</li>
	</xsl:template>

	<xsl:template match="api/description">
		<p>
			<xsl:apply-templates />
		</p>
	</xsl:template>

	<xsl:template match="function">

		<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.fnc')" />
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision">
					<xsl:value-of select="document($function_file)/function/@rcsversion" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<tr>
			<td>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@name" />
						<xsl:text>.html</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="@name" />
				</a>
				<xsl:if test="document($function_file)/function/@sessionBased = 'true'">
					<span title="This function is session-based">*</span>
				</xsl:if>
				<xsl:if test="@name = /api/@default">
					<xsl:text> (default)</xsl:text>
				</xsl:if>
			</td>
			<td>
				<xsl:value-of select="$version" />
			</td>
			<td class="status">
				<xsl:choose>
					<xsl:when test="@freeze = $version">Frozen</xsl:when>
					<xsl:when test="@freeze">
						<span class="broken_freeze">
							<xsl:attribute name="title">
								<xsl:text>Freeze broken after version </xsl:text>
								<xsl:value-of select="@freeze" />
								<xsl:text>.</xsl:text>
							</xsl:attribute>
							<xsl:text>Broken Freeze</xsl:text>
						</span>
					</xsl:when>
					<xsl:when test="document($function_file)/function/deprecated">
						<span class="broken_freeze" title="{document($function_file)/function/deprecated/text()}">
							<xsl:text>Deprecated</xsl:text>
						</span>
					</xsl:when>
				</xsl:choose>
			</td>
			<td>
				<xsl:apply-templates select="document($function_file)/function/description" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="function/description">
		<xsl:call-template name="firstline">
			<xsl:with-param name="text" select="text()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="type">

		<xsl:variable name="type_file" select="concat($specsdir, '/', @name, '.typ')" />
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision">
					<xsl:value-of select="document($type_file)/type/@rcsversion" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<tr>
			<td>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@name" />
						<xsl:text>.html</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="@name" />
				</a>
			</td>
			<td>
				<xsl:value-of select="$version" />
			</td>
			<td class="status">
				<xsl:choose>
					<xsl:when test="@freeze = $version">Frozen</xsl:when>
					<xsl:when test="@freeze">
						<span class="broken_freeze">
							<xsl:attribute name="title">
								<xsl:text>Freeze broken after version </xsl:text>
								<xsl:value-of select="@freeze" />
								<xsl:text>.</xsl:text>
							</xsl:attribute>
							<xsl:text>Broken Freeze</xsl:text>
						</span>
					</xsl:when>
					<xsl:when test="document($type_file)/type/deprecated">
						<span class="broken_freeze" title="{document($type_file)/type/deprecated/text()}">
							<xsl:text>Deprecated</xsl:text>
						</span>
					</xsl:when>
				</xsl:choose>
			</td>
			<td>
				<xsl:apply-templates select="document($type_file)/type/description" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="type/description">
		<xsl:call-template name="firstline">
			<xsl:with-param name="text" select="text()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="resultcode">

		<xsl:variable name="resultcode_file" select="concat($specsdir, '/', @name, '.rcd')" />
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision">
					<xsl:value-of select="document($resultcode_file)/resultcode/@rcsversion" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<tr>
			<td>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="@name" />
						<xsl:text>.html</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="@name" />
				</a>
			</td>
			<td>
				<xsl:value-of select="$version" />
			</td>
			<td class="status">
				<xsl:choose>
					<xsl:when test="@freeze = $version">Frozen</xsl:when>
					<xsl:when test="@freeze">
						<span class="broken_freeze">
							<xsl:attribute name="title">
								<xsl:text>Freeze broken after version </xsl:text>
								<xsl:value-of select="@freeze" />
								<xsl:text>.</xsl:text>
							</xsl:attribute>
							<xsl:text>Broken Freeze</xsl:text>
						</span>
					</xsl:when>
					<xsl:when test="document($resultcode_file)/resultcode/deprecated">
						<span class="broken_freeze" title="{document($resultcode_file)/resultcode/deprecated/text()}">
							<xsl:text>Deprecated</xsl:text>
						</span>
					</xsl:when>
				</xsl:choose>
			</td>
			<td>
				<xsl:apply-templates select="document($resultcode_file)/resultcode/description" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="resultcode/description">
		<xsl:call-template name="firstline">
			<xsl:with-param name="text" select="text()" />
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
