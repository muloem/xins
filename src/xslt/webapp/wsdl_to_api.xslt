<?xml version="1.0" encoding="UTF-8" ?>

<!--
 XSLT that generates the XINS API from the WSDL.

 $Id$

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:saxon="http://icl.com/saxon"
                xmlns:xt="http://www.jclarck.com/xt"
                xmlns:xalan="http://org.apache.xalan.xslt.extensions.Redirect"
								extension-element-prefixes="saxon xt xalan"
                exclude-result-prefixes="xs xsd saxon xt xalan"
                version="2.0">

	<xsl:include href="../types.xslt" />
	<xsl:include href="xsd_to_types.xslt" />

	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api_name"     />

	<!-- Creates the different files -->
	<xsl:template match="definitions">
<xsl:message terminate="no">-- 1</xsl:message>
		<xsl:call-template name="apifile">
			<xsl:with-param name="api_name" select="$api_name" />
		</xsl:call-template>
<xsl:message terminate="no">-- 2</xsl:message>
		<xsl:apply-templates select="portType/operation" />
<xsl:message terminate="no">-- 3</xsl:message>
		<!--xsl:apply-templates select="types/xsd:schema/xsd:simpleType/xsd:restriction" mode="restriction" /-->
<xsl:message terminate="no">-- 4</xsl:message>
		<xsl:apply-templates select="portType/operation/fault">
			<xsl:sort select="@name" />
		</xsl:apply-templates>
	</xsl:template>

	<!-- Creates the api.xml file -->
	<xsl:template name="apifile">
		<xsl:param name="api_name" />

		<xsl:text disable-output-escaping="yes"><![CDATA[<!DOCTYPE api PUBLIC "-//XINS//DTD XINS API 2.0//EN" "http://www.xins.org/dtd/api_2_0.dtd">]]>

</xsl:text>
		<api name="{$api_name}" rcsversion="&#x24;Revision$" rcsdate="&#x24;Date$">
<xsl:text>

	</xsl:text>
			<!-- The description of the API -->
			<description>
				<xsl:choose>
					<xsl:when test="service/documentation/text()">
						<xsl:value-of select="service/documentation/text()" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>The </xsl:text>
						<xsl:value-of select="$api_name" />
						<xsl:text> API.</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</description>
<xsl:text>

	</xsl:text>
			<!-- The list of the functions -->
			<xsl:for-each select="portType/operation">
				<function name="{@name}" />
				<xsl:if test="position() != last()">
<xsl:text>
	</xsl:text>
				</xsl:if>
			</xsl:for-each>
<xsl:text>

	</xsl:text>
			<!-- The list of the defined types -->
			<!-- TODO namespace independance with $xsdns variable in XPath -->
			<xsl:for-each select="types/xsd:schema/xsd:simpleType">
				<type name="{@name}" />
				<xsl:if test="position() != last()">
<xsl:text>
	</xsl:text>
				</xsl:if>
			</xsl:for-each>
<xsl:text>

	</xsl:text>
			<!-- The list of the possible error codes -->
			<xsl:for-each select="portType/operation/fault">
				<xsl:sort select="@name" />
				<xsl:if test="not(preceding-sibling::fault/@name = @name)">
					<resultcode name="{@name}" />
<xsl:text>
	</xsl:text>
				</xsl:if>
			</xsl:for-each>
		</api>
	</xsl:template>

	<!-- Creates the function files (.fnc) -->
	<xsl:template match="operation">
		<xsl:variable name="functionName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="functionFile" select="concat($functionName, '.fnc')" />

		<xalan:write file="{$functionFile}">
			<xsl:call-template name="functionfile">
				<xsl:with-param name="functionName" select="$functionName" />
			</xsl:call-template>
			<xsl:fallback />
		</xalan:write>
	</xsl:template>

	<!-- The content for the function files (.fnc) -->
	<xsl:template name="functionfile">
		<xsl:param name="functionName" />

		<xsl:text disable-output-escaping="yes"><![CDATA[<!DOCTYPE function PUBLIC "-//XINS//DTD XINS Function 2.0//EN" "http://www.xins.org/dtd/function_2_0.dtd">]]>

</xsl:text>
		<function name="{$functionName}" rcsversion="&#x24;Revision$" rcsdate="&#x24;Date$">
<xsl:text>

	</xsl:text>
			<description>
				<xsl:value-of select="documentation/text()" />
			</description>
<xsl:text>

	</xsl:text>
			<input>
				<xsl:apply-templates select="input" />
<xsl:text>
	</xsl:text>
			</input>
<xsl:text>

	</xsl:text>
			<output>
				<xsl:apply-templates select="fault" mode="reference">
					<xsl:sort select="@name" />
				</xsl:apply-templates>
<xsl:text>
</xsl:text>
				<xsl:apply-templates select="output" />
<xsl:text>
	</xsl:text>
			</output>
<xsl:text>
</xsl:text>
		</function>
	</xsl:template>

	<xsl:template match="fault" mode="reference">
		<xsl:text>
		</xsl:text>
		<resultcode-ref name="{@name}" />
	</xsl:template>

	<!-- Creates the error code files (.rcd) -->
	<xsl:template match="fault">
		<xsl:variable name="errorcodeName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="errorcodeFile" select="concat($errorcodeName, '.rcd')" />

		<xsl:if test="not(preceding-sibling::fault/@name = @name)">
			<xalan:write file="{$errorcodeFile}">
				<xsl:call-template name="errorcodefile">
					<xsl:with-param name="errorcodeName" select="$errorcodeName" />
				</xsl:call-template>
				<xsl:fallback />
			</xalan:write>
		</xsl:if>
	</xsl:template>

	<!-- The content for the function files (.fnc) -->
	<xsl:template name="errorcodefile">
		<xsl:param name="errorcodeName" />
		<xsl:text disable-output-escaping="yes"><![CDATA[<!DOCTYPE resultcode PUBLIC "-//XINS//DTD XINS Result Code 2.0//EN" "http://www.xins.org/dtd/resultcode_2_0.dtd">]]>

</xsl:text>
		<resultcode name="{$errorcodeName}" rcsversion="&#x24;Revision$" rcsdate="&#x24;Date$">
<xsl:text>

	</xsl:text>
			<description>
				<xsl:value-of select="documentation/text()" />
			</description>
		<xsl:if test="output">
<xsl:text>

	</xsl:text>
			<output>
				<xsl:apply-templates select="output" />
				<xsl:text>
</xsl:text>
			</output>
		</xsl:if>
		<xsl:text>
</xsl:text>
		</resultcode>
	</xsl:template>

	<!-- Fills in input or output section -->
	<xsl:template match="input | output">
		<xsl:variable name="section" select="local-name()" />
		<xsl:variable name="message">
			<xsl:call-template name="localname">
				<xsl:with-param name="text" select="@message" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="messageElement">
			<xsl:call-template name="localname">
				<xsl:with-param name="text" select="/definitions/message[@name=$message]/part/@element" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:for-each select="/definitions/types/xsd:schema/xsd:element[@name=$messageElement]/xsd:complexType/xsd:sequence/xsd:element">
			<xsl:variable name="paramname">
				<xsl:call-template name="hungarianLower">
					<xsl:with-param name="text" select="@name" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="required">
				<xsl:choose>
					<xsl:when test="not(@minOccurs) or @minOccurs = '0'">false</xsl:when>
					<xsl:otherwise>true</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="localnametype">
				<xsl:call-template name="localname">
					<xsl:with-param name="text" select="@type" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="type">
				<xsl:choose>
					<xsl:when test="starts-with(@type, 'tns:')">
						<xsl:value-of select="$localnametype" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="type_for_xsdtype">
							<xsl:with-param name="xsdtype" select="$localnametype" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!-- TODO elements containing other elements, especially when maxOccurs='1' -->

			<xsl:text>
		</xsl:text>
			<param name="{$paramname}" required="{$required}" type="{$type}">
			<xsl:text>
			</xsl:text>
				<description>
					<xsl:value-of select="concat($paramname, ' ', $section, ' parameter.')" />
				</description>
			<xsl:text>
		</xsl:text>
			</param>
		</xsl:for-each>
	</xsl:template>
	
	<!-- Removes any namespace prefix to the text if any -->
	<xsl:template name="localname">
		<xsl:param name="text" />

		<xsl:choose>
			<xsl:when test="contains($text, ':')">
				<xsl:value-of select="substring-after($text, ':')" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
