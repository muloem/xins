<?xml version="1.0" encoding="UTF-8" ?>

<!--
 XSLT that generates the typ files from a XSD file.

 $Id$

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:saxon="http://icl.com/saxon"
                xmlns:xt="http://www.jclarck.com/xt"
                xmlns:xalan="http://org.apache.xalan.xslt.extensions.Redirect"
								extension-element-prefixes="saxon xalan xt"
                exclude-result-prefixes="xs saxon xt xalan"
                version="2.0">

	<!--
	Creates the file
	-->
	<xsl:template match="xs:restriction | xsd:restriction" mode="restriction">
		<xsl:variable name="elementName">
			<xsl:choose>
				<xsl:when test="../@name">
					<xsl:value-of select="../@name" />
				</xsl:when>
				<xsl:when test="../../@name">
					<xsl:value-of select="../../@name" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>Type</xsl:text>
					<xsl:value-of select="position()" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="typeName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="$elementName" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="typeFile" select="concat($typeName, '.typ')" />

		<!-- The XSLT processor will choose which tag it can interpret -->
		<!--xsl:result-document href="{$typeFile}" format="typ_doctype">
			<xsl:call-template name="xml_type">
				<xsl:with-param name="typeName" select="$typeName" />
				<xsl:with-param name="elementName" select="$elementName" />
			</xsl:call-template>
			<xsl:fallback />
		</xsl:result-document-->
		<xalan:write file="{$typeFile}">
			<xsl:call-template name="xml_type">
				<xsl:with-param name="typeName" select="$typeName" />
				<xsl:with-param name="elementName" select="$elementName" />
			</xsl:call-template>
			<xsl:fallback />
		</xalan:write>
		<saxon:output file="{$typeFile}">
			<xsl:call-template name="xml_type">
				<xsl:with-param name="typeName" select="$typeName" />
				<xsl:with-param name="elementName" select="$elementName" />
			</xsl:call-template>
			<xsl:fallback />
		</saxon:output>
		<xt:document href="{$typeFile}">
			<xsl:call-template name="xml_type">
				<xsl:with-param name="typeName" select="$typeName" />
				<xsl:with-param name="elementName" select="$elementName" />
			</xsl:call-template>
			<xsl:fallback />
		</xt:document>
	</xsl:template>

	<xsl:template name="xml_type">
		<xsl:param name="typeName" />
		<xsl:param name="elementName" />

		<xsl:text disable-output-escaping="yes"><![CDATA[<!DOCTYPE type PUBLIC "-//XINS//DTD XINS Type 2.0//EN" "http://www.xins.org/dtd/type_2_0.dtd">]]>

</xsl:text>
<type name="{$typeName}" rcsversion="&#x24;Revision$" rcsdate="&#x24;Date$">
	<xsl:text>
	</xsl:text>
	<description>
		<xsl:choose>
			<xsl:when test="../xs:annotation/xs:documentation">
				<xsl:value-of select="../xs:annotation/xs:documentation/text()" />
			</xsl:when>
			<xsl:when test="../xsd:annotation/xsd:documentation">
				<xsl:value-of select="../xsd:annotation/xsd:documentation/text()" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$typeName" />
			</xsl:otherwise>
		</xsl:choose>
	</description>
	<xsl:text>
	</xsl:text>
	<xsl:choose>
		<xsl:when test="xs:pattern or xsd:pattern">
			<pattern>
				<xsl:value-of select="xs:pattern/@value" />
				<xsl:value-of select="xsd:pattern/@value" />
			</pattern>
		</xsl:when>
		<xsl:when test="xs:enumeration or xsd:enumeration">
			<enum>
				<xsl:for-each select="xs:enumeration | xsd:enumeration">
					<xsl:text>
		</xsl:text>
					<item value="{@value}" />
				</xsl:for-each>
					<xsl:text>
	</xsl:text>
			</enum>
		</xsl:when>
		<xsl:when test="xs:maxLength or xs:minLength or xs:length or xsd:maxLength or xsd:minLength or xsd:length">
			<pattern>
				<xsl:text>.{</xsl:text>
				<xsl:if test="not(xs:minLength) and not(xs:length) and not(xsd:minLength) and not(xsd:length)">
					<xsl:text>0</xsl:text>
				</xsl:if>
				<xsl:value-of select="xs:minLength/@value" />
				<xsl:value-of select="xs:length/@value" />
				<xsl:value-of select="xsd:minLength/@value" />
				<xsl:value-of select="xsd:length/@value" />
				<xsl:if test="xs:minLength or xs:maxLength or xsd:minLength or xsd:maxLength">
					<xsl:text>,</xsl:text>
				</xsl:if>
				<xsl:value-of select="xs:maxLength/@value" />
				<xsl:value-of select="xsd:maxLength/@value" />
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
	<xsl:text>
</xsl:text>
</type>
	</xsl:template>
</xsl:stylesheet>
