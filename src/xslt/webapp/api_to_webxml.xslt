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
	<xsl:variable name="sessionBased">
		<xsl:choose>
			<xsl:when test="boolean(//api/session-based)">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

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
				<servlet-class>org.xins.server.APIServlet</servlet-class>
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
				<init-param>
					<param-name>org.xins.api.sessionBased</param-name>
					<param-value>
						<xsl:value-of select="$sessionBased" />
					</param-value>
				</init-param>
				<xsl:if test="$sessionBased = 'true'">
					<init-param>
						<param-name>org.xins.api.sessionTimeOut</param-name>
						<param-value>
							<xsl:value-of select="//api/session-based/@timeout" />
						</param-value>
					</init-param>
					<init-param>
						<param-name>org.xins.api.sessionTimeOutPrecision</param-name>
						<param-value>
							<xsl:value-of select="//api/session-based/@precision" />
						</param-value>
					</init-param>
				</xsl:if>
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
				<load-on-startup>
					<xsl:text>0</xsl:text> <!-- XXX: Should we be able to configure this? -->
				</load-on-startup>
			</servlet>
			<servlet-mapping>
				<servlet-name>
					<xsl:value-of select="$api" />
				</servlet-name>
				<url-pattern>
					<xsl:text>/</xsl:text>
					<xsl:value-of select="$api" />
				</url-pattern>
			</servlet-mapping>
		</web-app>
	</xsl:template>

</xsl:stylesheet>
