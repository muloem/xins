<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="project_home" />

	<xsl:variable name="api"          select="//api/@name"                                />
	<xsl:variable name="project_file" select="concat($project_home, '/xins-project.xml')" />

	<xsl:output
	doctype-public="-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
	doctype-system="http://java.sun.com/dtd/web-app_2_3.dtd" />

	<xsl:include href="../package_to_dir.xslt" />
	<xsl:include href="../package_for_api.xslt" />

	<xsl:template match="api">
		<xsl:apply-templates select="impl-java" />
	</xsl:template>

	<xsl:template match="api/impl-java">
		<web-app>
			<servlet>
				<servlet-name>
					<xsl:value-of select="$api" />
				</servlet-name>
				<display-name>
					<xsl:value-of select="$api" />
				</display-name>
				<description>
					<xsl:text>Implementation of '</xsl:text>
					<xsl:value-of select="$api" />
					<xsl:text>' API.</xsl:text>
				</description>
				<servlet-class>
					<xsl:call-template name="package_for_api">
						<xsl:with-param name="project_file">
							<xsl:value-of select="$project_file" />
						</xsl:with-param>
						<xsl:with-param name="api">
							<xsl:value-of select="$api" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:text>.APIImpl</xsl:text>
				</servlet-class>
				<xsl:for-each select="param">
					<init-param>
						<param-name>
							<xsl:value-of select="$api" />
						</param-name>
						<param-value>
							<xsl:value-of select="text()" />
						</param-value>
					</init-param>
				</xsl:for-each>
			</servlet>
		</web-app>
	</xsl:template>

</xsl:stylesheet>
