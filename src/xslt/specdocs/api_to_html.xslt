<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />

	<xsl:variable name="api"          select="//api/@name" />
	<xsl:variable name="project_file" select="concat($project_home, '/xins-project.xml')" />
	<xsl:variable name="authors_file" select="concat($project_home, '/src/authors/authors.xml')" />

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
		<xsl:variable name="prev_url"  select="concat('../', $prev, '/api.html')" />
		<xsl:variable name="next"      select="document($project_file)/project/api[@name = $api]/following-sibling::api/@name" />
		<xsl:variable name="next_url"  select="concat('../', $next, '/api.html')" />

		<xsl:variable name="prev_title">
			<xsl:if test="boolean($prev) and not($prev = '')">
				<xsl:call-template name="firstline">
					<xsl:with-param name="text">
						<xsl:value-of select="document(concat($project_home, '/', $specsdir, '/', $prev, '/api.xml'))/api/description/text()" />
						<xsl:text> (</xsl:text>
						<xsl:value-of select="$prev" />
						<xsl:text>)</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
		</xsl:variable>

		<xsl:variable name="next_title">
			<xsl:if test="boolean($next) and not($next = '')">
				<xsl:call-template name="firstline">
					<xsl:with-param name="text">
						<xsl:value-of select="document(concat($project_home, '/', $specsdir, '/', $next, '/api.xml'))/api/description/text()" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:text> (</xsl:text>
				<xsl:value-of select="$next" />
				<xsl:text>)</xsl:text>
			</xsl:if>
		</xsl:variable>

		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<title>
					<xsl:text>API overview: </xsl:text>
					<xsl:value-of select="@name" />
				</title>
				<link rel="stylesheet" type="text/css" href="../style.css" />
				<link rel="top" href="../index.html" title="API index" />
				<link rel="up" href="../index.html" title="API index" />
				<link rel="first">
					<xsl:attribute name="href">
						<xsl:text>../</xsl:text>
						<xsl:value-of select="document($project_file)/project/api[1]/@name" />
						<xsl:text>/api.html</xsl:text>
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
							<a href="{$owner_email}">
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
					<xsl:when test="document($project_file)/project/environment">
						<ul>
							<xsl:for-each select="document($project_file)/project/environment">
								<li>
									<a>
										<xsl:attribute name="href">
											<xsl:value-of select="@url" />
										</xsl:attribute>
										<xsl:value-of select="@id" />
									</a>
								</li>
							</xsl:for-each>
						</ul>
					</xsl:when>
					<xsl:otherwise>
						<p>
							<em>No environments have been defined for this API.</em>
						</p>
					</xsl:otherwise>
				</xsl:choose>

				<xsl:call-template name="footer" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="api/description">
		<p>
			<xsl:apply-templates />
		</p>
	</xsl:template>

	<xsl:template match="function">

		<xsl:variable name="function_file" select="concat($project_home, '/', $specsdir, '/', $api, '/', @name, '.fnc')" />
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
				<xsl:if test="@name = /api/@default">
					<xsl:text> (default)</xsl:text>
				</xsl:if>
			</td>
			<td>
				<xsl:value-of select="$version" />
			</td>
			<td class="status">
				<xsl:if test="@freeze">
					<xsl:choose>
						<xsl:when test="@freeze = $version">Frozen</xsl:when>
						<xsl:otherwise>
							<span class="broken_freeze">
								<xsl:attribute name="title">
									<xsl:text>Freeze broken after version </xsl:text>
									<xsl:value-of select="@freeze" />
									<xsl:text>.</xsl:text>
								</xsl:attribute>
								<xsl:text>Broken Freeze</xsl:text>
							</span>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
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

		<xsl:variable name="type_file" select="concat($project_home, '/', $specsdir, '/', $api, '/', @name, '.typ')" />
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
				<xsl:if test="@freeze">
					<xsl:choose>
						<xsl:when test="@freeze = $version">Frozen</xsl:when>
						<xsl:otherwise>
							<span class="broken_freeze">
								<xsl:attribute name="title">
									<xsl:text>Freeze broken after version </xsl:text>
									<xsl:value-of select="@freeze" />
									<xsl:text>.</xsl:text>
								</xsl:attribute>
								<xsl:text>Broken Freeze</xsl:text>
							</span>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
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

</xsl:stylesheet>
