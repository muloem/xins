<?xml version="1.0" encoding="UTF-8" ?>

<!--
 XSLT that generates the WSDL file from the API.

 $Id$

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="1.0">

	<xsl:include href="../hungarian.xslt"  />

	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />

	<xsl:output method="xml" indent="yes"
	doctype-public="-//XINS//DTD Type 2.0//EN"
	doctype-system="http://www.xins.org/dtd/type_2_0.dtd" />

	<!-- TODO test with XML special characters in xsd -->

	<xsl:template match="xs:schema">
		<xsl:apply-templates select="//xs:element/xs:simpleType/xs:restriction" />
	</xsl:template>

	<xsl:template match="xs:restriction">
		<xsl:variable name="elementName" select="../../@name" />
		<xsl:variable name="typeName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="$elementName" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="typeFile" select="concat($typeName, '.typ')" />
		<xsl:message terminate="no">
			<xsl:text>Found element: </xsl:text>
			<xsl:value-of select="$elementName" />
		</xsl:message>
		<!--xsl:result-document href="$typeFile" format="xml"-->
<type name="{$typeName}" rcsversion="&#x24;Revision$" rcsdate="&#x24;Date$">
	<xsl:text>
	</xsl:text>
	<description>
		<xsl:choose>
			<xsl:when test="../xs:annotation/xs:documentation">
				<xsl:value-of select="../xs:annotation/xs:documentation/text()" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$typeName" />
			</xsl:otherwise>
		</xsl:choose>
	</description>
	<xsl:text>
	</xsl:text>
	<xsl:choose>
		<xsl:when test="xs:pattern">
			<pattern>
				<xsl:value-of select="xs:pattern/@value" />
			</pattern>
		</xsl:when>
		<xsl:when test="xs:enumeration">
			<enum>
				<xsl:for-each select="xs:enumeration">
					<xsl:text>
		</xsl:text>
					<item value="{@value}" />
				</xsl:for-each>
			</enum>
		</xsl:when>
		<xsl:when test="xs:maxLength or xs:minLength or xs:length">
			<pattern>
				<xsl:text>.{</xsl:text>
				<xsl:if test="not(xs:minLength) and not(xs:length)">
					<xsl:text>0</xsl:text>
				</xsl:if>
				<xsl:value-of select="xs:minLength/@value" />
				<xsl:value-of select="xs:length/@value" />
				<xsl:if test="xs:minLength or xs:maxLength">
					<xsl:text>,</xsl:text>
				</xsl:if>
				<xsl:value-of select="xs:maxLength/@value" />
				<xsl:text>}</xsl:text>
			</pattern>
		</xsl:when>
		<xsl:otherwise>
			<xsl:message terminate="no">
				<xsl:text>No XINS type created for XSD type </xsl:text>
				<xsl:value-of select="$elementName" />
			</xsl:message>
		</xsl:otherwise>
	</xsl:choose>
</type>
		<!--/xsl:result-document-->
	</xsl:template>
</xsl:stylesheet>
