<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the output section for a function or for a result code.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="parametertable">
		<xsl:param name="title" />
		<xsl:param name="content" />
		<xsl:param name="class" />

		<h3>
			<xsl:value-of select="$title" />
		</h3>

		<xsl:choose>
			<xsl:when test="param | property">
				<table class="parameters">
					<xsl:attribute name="class">
						<xsl:value-of select="$class" />
					</xsl:attribute>
					<tr>
						<th>Parameter</th>
						<th>Type</th>
						<th>Description</th>
						<th>Required</th>
					</tr>
					<xsl:for-each select="param[not(@required='true') and not(@required='false')] | property[not(@required='true') and not(@required='false')]">
						<xsl:message terminate="yes">
							<xsl:text>Parameter '</xsl:text>
							<xsl:value-of select="@name" />
							<xsl:text>' in </xsl:text>
							<xsl:value-of select="$content" />
							<xsl:text> has required attribute set to '</xsl:text>
							<xsl:value-of select="@required" />
							<xsl:text>', while only 'true' and 'false' are allowed values.</xsl:text>
						</xsl:message>
					</xsl:for-each>
					<xsl:apply-templates select="param" />
					<xsl:apply-templates select="property" />
				</table>
			</xsl:when>
			<xsl:otherwise>
				<p>
					<em>
						<xsl:text>This function defines no </xsl:text>
						<xsl:value-of select="$content" />
						<xsl:text>.</xsl:text>
					</em>
				</p>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<xsl:template match="param | property">

		<xsl:if test="boolean(deprecated) and (@required = 'true')">
			<xsl:message terminate="yes">
				<xsl:text>Parameter '</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>' is both deprecated and required!</xsl:text>
			</xsl:message>
		</xsl:if>

		<tr>
			<td class="name">
				<a>
					<xsl:attribute name="name">
						<xsl:value-of select="name(..)" />
						<xsl:text>_</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:attribute>
				</a>
				<xsl:value-of select="@name" />
			</td>
			<td class="type">
				<xsl:call-template name="typelink">
					<xsl:with-param name="api"      select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="type">
						<xsl:choose>
							<xsl:when test="boolean(@type)">
								<xsl:value-of select="@type" />
							</xsl:when>
							<xsl:otherwise>_text</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</td>
			<td class="description">
				<xsl:call-template name="description" />
			</td>
			<td class="required">
				<xsl:if test="@required = 'true'">
					<xsl:text>yes</xsl:text>
				</xsl:if>
				<xsl:if test="@required = 'false'">
					<xsl:text>no</xsl:text>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="data/element">
		<h4>
			<xsl:text>Element </xsl:text>
			<em>
				<xsl:text>&lt;</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>/&gt;</xsl:text>
			</em>
		</h4>

		<table class="element_details">
			<tr>
				<th>Description:</th>
				<td>
					<xsl:choose>
						<xsl:when test="description">
							<xsl:apply-templates select="description" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:text> </xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
			<tr>
				<th>Contains:</th>
				<td>
					<xsl:choose>
						<xsl:when test="contains/contained">
							<xsl:for-each select="contains/contained">
								<xsl:if test="position() != 1">
									<xsl:text>, </xsl:text>
								</xsl:if>
								<code>
									<xsl:text>&lt;</xsl:text>
									<xsl:value-of select="@element" />
									<xsl:text>/&gt;</xsl:text>
								</code>
							</xsl:for-each>
						</xsl:when>
						<xsl:when test="contains/pcdata">
							<xsl:text>Character data.</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<em>Nothing.</em>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
			<tr>
				<th>Attributes:</th>
				<td>
					<xsl:choose>
						<xsl:when test="attribute">
							<table class="parameters">
								<tr>
									<th>Name</th>
									<th>Type</th>
									<th>Description</th>
									<th>Required</th>
								</tr>
								<xsl:apply-templates select="attribute" />
							</table>
						</xsl:when>
						<xsl:otherwise>
							<em>none</em>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="data/element/attribute">
		<xsl:variable name="type">
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:value-of select="@type" />
				</xsl:when>
				<xsl:otherwise>_text</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<tr>
			<td class="value">
				<a>
					<xsl:attribute name="name">
						<xsl:value-of select="../@name" />
						<xsl:text>_</xsl:text>
						<xsl:value-of select="@name" />
					</xsl:attribute>
				</a>
				<xsl:value-of select="@name" />
			</td>
			<td>
				<xsl:call-template name="typelink">
					<xsl:with-param name="api"      select="$api"      />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="type">
						<xsl:choose>
							<xsl:when test="boolean(@type)">
								<xsl:value-of select="@type" />
							</xsl:when>
							<xsl:otherwise>_text</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
			</td>
			<td>
				<xsl:call-template name="description" />
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="@required = 'true'">
						<xsl:text>yes</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>no</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="description">
		<xsl:if test="description">
			<xsl:apply-templates select="description" />
			<xsl:if test="deprecated">
				<br />
			</xsl:if>
		</xsl:if>
		<xsl:if test="deprecated">
			<em>
				<strong>Deprecated: </strong>
				<xsl:apply-templates select="deprecated" />
			</em>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
