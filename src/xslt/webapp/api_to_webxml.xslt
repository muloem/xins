<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the web.xml file that is included in the WAR file.

 $Id$

 Copyright 2004 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="api"          />
	<xsl:param name="hostname"     />
	<xsl:param name="timestamp"    />

	<xsl:output
	doctype-public="-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
	doctype-system="http://java.sun.com/dtd/web-app_2_3.dtd"
	indent="yes" />

	<xsl:include href="../package_for_api.xslt" />

	<xsl:template match="api">
		<xsl:if test="string-length($hostname) &lt; 1">
			<xsl:message terminate="yes">Parameter 'hostname' is not specified.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($timestamp) &lt; 1">
			<xsl:message terminate="yes">Parameter 'timestamp' is not specified.</xsl:message>
		</xsl:if>
		<xsl:apply-templates select="impl-java" />
		<xsl:if test="document($project_file)/project/api[@name = $api]/impl">
			<xsl:variable name="impl_file" select="concat($project_home, '/apis/', $api, '/impl/impl.xml')"/>
			<xsl:apply-templates select="document($impl_file)/impl" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="api/impl-java | impl">
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
				<servlet-class>org.xins.server.APIServlet</servlet-class>
				<init-param>
					<param-name>org.xins.api.name</param-name>
					<param-value>
						<xsl:value-of select="$api" />
					</param-value>
				</init-param>
				<init-param>
					<param-name>org.xins.api.class</param-name>
					<param-value>
						<xsl:call-template name="package_for_server_api">
							<xsl:with-param name="project_file">
								<xsl:value-of select="$project_file" />
							</xsl:with-param>
							<xsl:with-param name="api">
								<xsl:value-of select="$api" />
							</xsl:with-param>
						</xsl:call-template>
						<xsl:text>.APIImpl</xsl:text>
					</param-value>
				</init-param>
				<xsl:for-each select="param">
					<init-param>
						<param-name>
							<xsl:value-of select="@name" />
						</param-name>
						<param-value>
							<xsl:value-of select="text()" />
						</param-value>
					</init-param>
				</xsl:for-each>
				<init-param>
					<param-name>org.xins.api.build.version</param-name>
					<param-value>
						<xsl:value-of select="$xins_version" />
					</param-value>
				</init-param>
				<init-param>
					<param-name>org.xins.api.build.host</param-name>
					<param-value>
						<xsl:value-of select="$hostname" />
					</param-value>
				</init-param>
				<init-param>
					<param-name>org.xins.api.build.time</param-name>
					<param-value>
						<xsl:value-of select="$timestamp" />
					</param-value>
				</init-param>
				<xsl:if test="calling-convention">
					<init-param>
						<param-name>org.xins.api.calling-convention</param-name>
						<param-value>
							<xsl:value-of select="calling-convention/@name" />
						</param-value>
					</init-param>
				</xsl:if>
				<load-on-startup>
					<!-- XXX: Should we be able to configure the load-on-startup setting ? -->
					<xsl:text>0</xsl:text>
				</load-on-startup>
			</servlet>
			<servlet-mapping>
				<servlet-name>
					<xsl:value-of select="$api" />
				</servlet-name>
				<url-pattern>
					<xsl:text>/</xsl:text>
				</url-pattern>
			</servlet-mapping>
		</web-app>
	</xsl:template>

</xsl:stylesheet>
