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
								extension-element-prefixes="saxon xalan"
                exclude-result-prefixes="xsd saxon xt xalan"
                version="1.0">

	<xsl:include href="../hungarian.xslt"  />

	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api_name"     />

	<xsl:output method="xml" indent="yes"
	doctype-public="-//XINS//DTD XINS API 2.0//EN"
	doctype-system="http://www.xins.org/dtd/api_2_0.dtd" />

	<xsl:template match="definitions">
		<xsl:call-template name="apifile">
			<xsl:with-param name="api_name" select="$api_name" />
		</xsl:call-template>
		<xsl:apply-templates select="portType/operation" />
	</xsl:template>

	<!-- Creates the api.xml file -->
	<xsl:template name="apifile">
		<api name="{$api_name}" rcsversion="&#x24;Revision$" rcsdate="&#x24;Date$">
<xsl:text>

	</xsl:text>
			<description>
				<xsl:value-of select="portType/documentation/text()" />
				<xsl:value-of select="binding/documentation/text()" />
			</description>
<xsl:text>

	</xsl:text>
			<xsl:for-each select="binding/operation">
				<function name="{@name}" />
<xsl:text>
	</xsl:text>
			</xsl:for-each>
<xsl:text>

	</xsl:text>
			<xsl:for-each select="type/xsd:schema/xsd:simpleTypes">
				<type name="{@name}" />
<xsl:text>
	</xsl:text>
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

		<xsl:output method="xml" indent="yes"
		doctype-public="-//XINS//DTD Function 2.0//EN"
		doctype-system="http://www.xins.org/dtd/function_2_0.dtd" />
<xsl:text>
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
				<xsl:variable name="wsdlFunctionName" select="@name" />
				<xsl:variable name="inputMessage" select="/definitions/portType/operation[@name='$wsdlFunctionName']/input/@message" />
				<xsl:variable name="messageElement" select="/definitions/message[@name='$inputMessage']/part/@element" />
				
				<xsl:for-each select="/definitions/types/xsd:schema/xsd:element[@name='$messageElement']/xsd:complexType/xsd:sequence/xsd:element">
					<input name="@name" required="false" type="@type" />
				</xsl:for-each>
			</input>
		</function>

	</xsl:template>

</xsl:stylesheet>
