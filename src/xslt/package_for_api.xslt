<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Determines the name for the Java package that will contain the
	     implementation of the specified API -->
	<xsl:template name="package_for_server_api">
		<xsl:param name="project_file" />
		<xsl:param name="api"          />

		<xsl:call-template name="package_for">
			<xsl:with-param name="project_file" select="$project_file" />
			<xsl:with-param name="api"          select="$api"          />
			<xsl:with-param name="suffix"       select="'api'"         />
		</xsl:call-template>
	</xsl:template>

	<!-- Determines the name for the Java package that will contain the
	     CAPI for the specified API -->
	<xsl:template name="package_for_client_api">
		<xsl:param name="project_file" />
		<xsl:param name="api"          />

		<xsl:call-template name="package_for">
			<xsl:with-param name="project_file" select="$project_file" />
			<xsl:with-param name="api"          select="$api"          />
			<xsl:with-param name="suffix"       select="'capi'"        />
		</xsl:call-template>
	</xsl:template>

	<!-- Determines the name for the Java package that will contain the
	     types for the specified API -->
	<xsl:template name="package_for_type_classes">
		<xsl:param name="project_file" />
		<xsl:param name="api"          />

		<xsl:call-template name="package_for">
			<xsl:with-param name="project_file" select="$project_file" />
			<xsl:with-param name="api"          select="$api"          />
			<xsl:with-param name="suffix"       select="'types'"       />
		</xsl:call-template>
	</xsl:template>

	<!-- Determines a Java package name based on a project file, an API and a
	     suffix to use. -->
	<xsl:template name="package_for">

		<!-- Parameter definitions -->
		<xsl:param name="project_file" />
		<xsl:param name="api"          />
		<xsl:param name="suffix"       />

		<!-- Variable definitions -->
		<xsl:variable name="domain" select="document($project_file)/project/@domain" />

		<!-- Check preconditions -->
		<xsl:if test="string-length($domain) = 0">
			<xsl:message terminate="yes">No domain specified for project.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($project_file) = 0">
			<xsl:message terminate="yes">Mandatory parameter 'project_file' is not defined.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($api) = 0">
			<xsl:message terminate="yes">Mandatory parameter 'api' is not defined.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($suffix) = 0">
			<xsl:message terminate="yes">Mandatory parameter 'suffix' is not defined.</xsl:message>
		</xsl:if>

		<!-- Append domain, API name and suffix -->
		<xsl:value-of select="$domain" />
		<xsl:text>.</xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text>.</xsl:text>
		<xsl:value-of select="$suffix" />
	</xsl:template>
</xsl:stylesheet>
