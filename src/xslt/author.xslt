<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that gets and display the information about the API author.

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="api" method="owner">
		<xsl:variable name="owner">
			<xsl:if test="boolean(@owner) and not(owner = '')">
				<xsl:variable name="new_authors_file" select="concat($project_home, '/authors.xml')" />
				<xsl:variable name="authors_file">
					<xsl:choose>
						<xsl:when test="document($new_authors_file)">
							<xsl:value-of select="$new_authors_file" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="concat($project_home, '/src/authors/authors.xml')" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

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

		<xsl:if test="$owner != ''">
			<xsl:variable name="new_authors_file" select="concat($project_home, '/authors.xml')" />
			<xsl:variable name="authors_file">
				<xsl:choose>
					<xsl:when test="document($new_authors_file)">
						<xsl:value-of select="$new_authors_file" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($project_home, '/src/authors/authors.xml')" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="owner_name">
				<xsl:value-of select="document($authors_file)/authors/author[@id=$owner]/@name" />
			</xsl:variable>
			<xsl:variable name="owner_email">
				<xsl:value-of select="document($authors_file)/authors/author[@id=$owner]/@email" />
			</xsl:variable>

				<xsl:value-of select="$owner_name" />
				<xsl:text> (</xsl:text>
				<a href="mailto:{$owner_email}">
					<xsl:value-of select="$owner_email" />
				</a>
				<xsl:text>)</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>