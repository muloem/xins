<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="xins_version" />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="api"          />
	<xsl:param name="deployment"   />
	<xsl:param name="hostname"     />
	<xsl:param name="timestamp"    />

	<xsl:variable name="sessionBased">
		<xsl:choose>
			<xsl:when test="boolean(//api/session-based)">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:output
	doctype-public="-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
	doctype-system="http://java.sun.com/dtd/web-app_2_3.dtd"
	indent="yes" />

	<xsl:include href="../package_to_dir.xslt" />
	<xsl:include href="../package_for_api.xslt" />

	<xsl:template match="api">
		<xsl:if test="string-length($hostname) &lt; 1">
			<xsl:message terminate="yes">Parameter 'hostname' is not specified.</xsl:message>
		</xsl:if>
		<xsl:if test="string-length($timestamp) &lt; 1">
			<xsl:message terminate="yes">Parameter 'timestamp' is not specified.</xsl:message>
		</xsl:if>
		<xsl:apply-templates select="impl-java" />
	</xsl:template>

	<xsl:template match="api/impl-java">
		<xsl:if test="(not ($deployment = '')) and not(boolean(deployment[@name = $deployment]))">
			<xsl:message terminate="yes">
				<xsl:text>No deployment named '</xsl:text>
				<xsl:value-of select="$deployment" />
				<xsl:text>' defined.</xsl:text>
			</xsl:message>
		</xsl:if>
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
				<xsl:choose>
					<xsl:when test="not ($deployment = '')">
						<init-param>
							<param-name>org.xins.api.deployment</param-name>
							<param-value>
								<xsl:value-of select="$deployment" />
							</param-value>
						</init-param>
						<xsl:for-each select="deployment[@name = $deployment]/param">
							<init-param>
								<param-name>
									<xsl:value-of select="@name" />
								</param-name>
								<param-value>
									<xsl:value-of select="text()" />
								</param-value>
							</init-param>
						</xsl:for-each>
						<xsl:for-each select="param">
							<xsl:variable name="param_name" select="@name" />
							<xsl:if test="not (//api/impl-java/deployment[@name = $deployment]/param[@name = $param_name])">
								<init-param>
									<param-name>
										<xsl:value-of select="@name" />
									</param-name>
									<param-value>
										<xsl:value-of select="text()" />
									</param-value>
								</init-param>
							</xsl:if>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
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
					</xsl:otherwise>
				</xsl:choose>
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
					<xsl:choose>
						<xsl:when test="@mapping">
							<xsl:value-of select="@mapping" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>/</xsl:text>
							<xsl:value-of select="$api" />
						</xsl:otherwise>
					</xsl:choose>
				</url-pattern>
			</servlet-mapping>
		</web-app>
	</xsl:template>

</xsl:stylesheet>
