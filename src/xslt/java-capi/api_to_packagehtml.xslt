<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the package.html which is used by the javadoc.

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<!-- Determine if this API is session-based -->
	<xsl:variable name="sessionBased">
		<xsl:choose>
			<xsl:when test="boolean(//api/session-based)">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- If this API is session-based, determine if sessions are shared among
	     different instances of the API -->
	<xsl:variable name="sessionsShared">
		<xsl:choose>
			<xsl:when test="$sessionBased = 'true' and //api/session-based/@shared-sessions = 'true'">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- If this API is session-based, determine the Java type for session
	     identifiers, either a Java primary data type (byte, short, int, char,
	     etc.) or a Java class (java.lang.String, etc.) -->
	<xsl:variable name="sessionIDJavaType">
		<xsl:choose>
			<xsl:when test="$sessionBased = 'true' and $sessionsShared = 'false'">
				<xsl:text>java.lang.String</xsl:text>
			</xsl:when>
			<xsl:when test="$sessionBased = 'true'">
				<xsl:call-template name="javatype_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="specsdir"     select="$specsdir"     />
					<xsl:with-param name="required"     select="'true'"        />
					<xsl:with-param name="type"         select="_text"         />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:variable>

	<!-- For the sessionIDJavaType, determine if this is a Java primary data
	     type or a Java class. -->
	<xsl:variable name="sessionIDJavaTypeIsPrimary">
		<xsl:if test="$sessionBased = 'true'">
			<xsl:call-template name="is_java_datatype">
				<xsl:with-param name="text" select="$sessionIDJavaType" />
			</xsl:call-template>
		</xsl:if>
	</xsl:variable>

	<!-- Determine the location of the online specification docs -->
	<xsl:variable name="specdocsURL">
		<xsl:value-of select="document($project_file)/project/specdocs/@href" />
	</xsl:variable>

	<!-- Output is text/plain -->
	<xsl:output method="html" />

	<!-- Perform includes -->
	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../function.xslt"   />
	<xsl:include href="../rcs.xslt"        />
	<xsl:include href="../types.xslt"      />


	<!-- ***************************************************************** -->
	<!-- Match the root element: api                                       -->
	<!-- ***************************************************************** -->

	<xsl:template match="api">
		<html>
			<body>
				<xsl:text>Client-side calling interface (CAPI) for the </xsl:text>
				<em>
					<xsl:value-of select="$api" />
				</em>
				<xsl:text> API.</xsl:text>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
