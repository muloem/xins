<?xml version="1.0" encoding="UTF-8" ?>

<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the WSDL file from the API.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
								xmlns="http://schemas.xmlsoap.org/wsdl/"
								xmlns:xsd="http://www.w3.org/2001/XMLSchema"
								xmlns:soapbind="http://schemas.xmlsoap.org/wsdl/soap/"
								version="1.0">

	<xsl:include href="../types.xslt"  />
	
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />

	<xsl:output indent="yes" />

	<xsl:template match="api">
	
		<xsl:variable name="apiname" select="@name" />
		<xsl:variable name="location">
			<xsl:choose>
				<xsl:when test="document($project_file)/project/api[@name=$apiname]/environments">
					<xsl:variable name="env_file" select="concat($project_home, '/apis/', $apiname, '/environments.xml')" />
					<xsl:value-of select="document($env_file)/environments/environment[1]/@url" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>http://localhost:8080/</xsl:text>
					<xsl:value-of select="$apiname" />
					<xsl:text>/</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<definitions name="{$apiname}"
			targetNamespace="urn:{$apiname}"
			xmlns="http://schemas.xmlsoap.org/wsdl/"
			xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
			xmlns:soapbind="http://schemas.xmlsoap.org/wsdl/soap/"
			xmlns:http="http://schemas.xmlsoap.org/wsdl/http/"
			xmlns:xsd="http://www.w3.org/2001/XMLSchema"
			xmlns:tns="urn:apiname">
			<types>
				<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"
										targetNamespace="urn:{$apiname}">
					
					<!-- Write the elements -->
					<xsl:apply-templates select="function" mode="elements">
						<xsl:with-param name="project_file" select="$project_file" />
						<xsl:with-param name="specsdir"     select="$specsdir"     />
						<xsl:with-param name="api"          select="$apiname"      />
					</xsl:apply-templates>
					
					<!-- Write the defined types -->
					<xsl:apply-templates select="type" mode="types">
						<xsl:with-param name="project_file" select="$project_file" />
						<xsl:with-param name="specsdir"     select="$specsdir"     />
						<xsl:with-param name="api"          select="$apiname"      />
					</xsl:apply-templates>
				</xsd:schema>
			</types>
					
			<!-- Write the messages -->
			<xsl:apply-templates select="function" mode="messages">
				<xsl:with-param name="specsdir" select="$specsdir" />
			</xsl:apply-templates>

			<!-- Write the port types -->
			<portType name="{$apiname}PortType">
				<xsl:apply-templates select="function" mode="porttypes" />
			</portType>
			
			<!-- Write the bindings -->
			<binding name="{$apiname}SOAPBinding" type="tns:{$apiname}PortType">
				<documentation>
					<xsl:value-of select="description" />
				</documentation>
				<xsl:apply-templates select="function" mode="bindings">
					<xsl:with-param name="location" select="$location" />
					<xsl:with-param name="apiname" select="$apiname" />
				</xsl:apply-templates>
        <soapbind:binding style="document" transport="http://schemas.xmlsoap.org/soap/http" />
			</binding>
			
			<!-- Write the services -->
			<service name="{$apiname}Service">
				<port name="{$apiname}Port" binding="tns:{$apiname}SOAPBinding">
					<soapbind:address location="{$location}/?_convention=_xins-soap" />
				</port>
			</service>
		</definitions>
	</xsl:template>
	
	<xsl:template match="function" mode="elements">
		
		<xsl:param name="project_file" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		
		<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.fnc')" />

		<xsl:apply-templates select="document($function_file)/function/input" mode="elements">
			<xsl:with-param name="project_file" select="$project_file" />
			<xsl:with-param name="specsdir"     select="$specsdir"     />
			<xsl:with-param name="api"          select="$api"          />
			<xsl:with-param name="elementname"  select="concat(@name, 'Request')" />
		</xsl:apply-templates>
		<xsl:apply-templates select="document($function_file)/function/output" mode="elements">
			<xsl:with-param name="project_file" select="$project_file" />
			<xsl:with-param name="specsdir"     select="$specsdir"     />
			<xsl:with-param name="api"          select="$api"          />
			<xsl:with-param name="elementname"  select="concat(@name, 'Response')" />
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="input | output" mode="elements">

		<xsl:param name="project_file" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		<xsl:param name="elementname"  />
		
		<!-- The input or output parameters of the function -->
		<xsd:element name="{$elementname}">
			<xsd:complexType>
				<xsd:sequence>
					<xsl:for-each select="param">
						<xsl:variable name="elementtype">
							<xsl:call-template name="elementtype">
								<xsl:with-param name="project_file" select="$project_file" />
								<xsl:with-param name="specsdir"     select="$specsdir" />
								<xsl:with-param name="api"          select="$api" />
								<xsl:with-param name="type"         select="@type" />
							</xsl:call-template>
						</xsl:variable>
						<xsl:variable name="minoccurs">
							<xsl:choose>
								<xsl:when test="@required = 'true'">1</xsl:when>
								<xsl:otherwise>0</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:variable name="paramname" select="@name" />
						<xsd:element name="{$paramname}" type="{$elementtype}" minOccurs="{$minoccurs}" />
					</xsl:for-each>
					<xsl:if test="data">
						<xsd:element name="data" minOccurs="0">
							<xsd:complexType>
								<xsd:sequence>
									<xsl:if test="data/@contains">
										<xsl:variable name="contained_element" select="data/@contains" />
										<xsl:apply-templates select="data/element[@name=$contained_element]" mode="datasection">
											<xsl:with-param name="project_file" select="$project_file" />
											<xsl:with-param name="specsdir"     select="$specsdir" />
											<xsl:with-param name="api"          select="$api" />
										</xsl:apply-templates>
									</xsl:if>
									<xsl:for-each select="data/contains/contained">
										<xsl:variable name="contained_element" select="@element" />
										<xsl:apply-templates select="../../element[@name=$contained_element]" mode="datasection">
											<xsl:with-param name="project_file" select="$project_file" />
											<xsl:with-param name="specsdir"     select="$specsdir" />
											<xsl:with-param name="api"          select="$api" />
										</xsl:apply-templates>
									</xsl:for-each>
								</xsd:sequence>
							</xsd:complexType>
						</xsd:element>
					</xsl:if>
				</xsd:sequence>
			</xsd:complexType>
		</xsd:element>
	</xsl:template>
	
	<xsl:template match="element" mode="datasection">
	
		<xsl:param name="project_file" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		
		<xsd:element name="{@name}" minOccurs="0" maxOccurs="unbounded">
			<xsd:complexType>
				<xsl:if test="contains/contained">
					<xsd:sequence>
						<xsl:for-each select="contains/contained">
							<xsl:variable name="contained_element" select="@element" />
							<xsl:apply-templates select="../../../element[@name=$contained_element]" mode="datasection">
								<xsl:with-param name="project_file" select="$project_file" />
								<xsl:with-param name="specsdir"     select="$specsdir" />
								<xsl:with-param name="api"          select="$api" />
							</xsl:apply-templates>
						</xsl:for-each>
					</xsd:sequence>
				</xsl:if>
				<xsl:if test="contains/pcdata">
					<xsl:text disable-output-escaping="yes">
&lt;xsd:simpleContent>
&lt;xsd:extension base="xsd:string"></xsl:text>
				</xsl:if>
				<xsl:for-each select="attribute">
					<xsl:variable name="elementtype">
						<xsl:call-template name="elementtype">
							<xsl:with-param name="project_file" select="$project_file" />
							<xsl:with-param name="specsdir"     select="$specsdir" />
							<xsl:with-param name="api"          select="$api" />
							<xsl:with-param name="type"         select="@type" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="use">
						<xsl:choose>
							<xsl:when test="@required = 'true'">required</xsl:when>
							<xsl:otherwise>optional</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="attributename" select="@name" />
					<xsd:attribute name="{$attributename}" type="{$elementtype}" use="{$use}" />
				</xsl:for-each>
				<xsl:if test="contains/pcdata">
					<xsl:text disable-output-escaping="yes">
&lt;/xsd:extension>
&lt;/xsd:simpleContent>
</xsl:text>
				</xsl:if>
			</xsd:complexType>
		</xsd:element>
	</xsl:template>
	
	<xsl:template match="function" mode="messages">
	
		<xsl:param name="specsdir" />
		
		<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.fnc')" />
		<xsl:variable name="functionname" select="@name" />
		
		<message name="{$functionname}Input">
			<part name="parameters" element="tns:{$functionname}Request" />
		</message>
		<message name="{$functionname}Output">
			<part name="parameters" element="tns:{$functionname}Response" />
		</message>
	</xsl:template>
	
	<xsl:template match="function" mode="porttypes">
	
		<xsl:variable name="functionname" select="@name" />
		<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.fnc')" />
		
		<operation name="{$functionname}">
			<documentation>
				<xsl:value-of select="document($function_file)/function/description" />
			</documentation>
			<input message="tns:{$functionname}Input" />
			<output message="tns:{$functionname}Output" />
		</operation>
	</xsl:template>
	
	<xsl:template match="function" mode="bindings">
	
		<xsl:param name="location" />
		<xsl:param name="apiname" />
		
		<xsl:variable name="functionname" select="@name" />
		<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.fnc')" />
		
		<operation name="{$functionname}">
			<documentation>
				<xsl:value-of select="document($function_file)/function/description" />
			</documentation>
			<!--soapbind:operation soapAction="{$location}/{$functionname}" /-->
			<input>
				<soapbind:body use="literal" />
			</input>
			<output>
				<soapbind:body use="literal" />
			</output>
			<xsl:for-each select="document($function_file)/function/resultcode-ref">
				<xsl:variable name="rcd_file" select="concat($specsdir, '/', @name, '.rcd')" />
				<fault name="{$functionname}">
					<document>
						<xsl:value-of select="document($rcd_file)/resultcode/description" />
						<soapbind:body use="literal"/>
					</document>
				</fault>
			</xsl:for-each>
		</operation>
	</xsl:template>
	
	<xsl:template match="type" mode="types">
	
		<xsl:param name="project_file" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		
		<xsl:variable name="type_name" select="@name" />
		<xsl:variable name="type_file" select="concat($specsdir, '/', $type_name, '.typ')" />
		<xsl:variable name="base_type">
			<xsl:choose>
				<xsl:when test="document($type_file)/type/pattern or document($type_file)/type/enum or document($type_file)/type/list or document($type_file)/type/set">
					<xsl:text>string</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<!-- It's the same as for the basic type -->
					<xsl:call-template name="soaptype_for_type">
						<xsl:with-param name="project_file" select="$project_file" />
						<xsl:with-param name="specsdir"     select="$specsdir" />
						<xsl:with-param name="api"          select="$api" />
						<xsl:with-param name="type"         select="concat('_', local-name(document($type_file)/type/*[2]))" />
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsd:simpleType name="{$type_name}Type">
			<xsd:annotation>
				<xsd:documentation>
					<xsl:value-of select="document($type_file)/type/description/text()" />
				</xsd:documentation>
			</xsd:annotation>
			<xsd:restriction base="xsd:{$base_type}">
				<xsl:if test="document($type_file)/type/pattern">
					<xsl:variable name="pattern" select="document($type_file)/type/pattern/text()" />
					<xsd:pattern value="{$pattern}" />
				</xsl:if>
				<xsl:if test="document($type_file)/type/enum">
					<xsl:for-each select="document($type_file)/type/enum/item">
						<xsl:variable name="enumeration_value" select="@value" />
						<xsd:enumeration value="{$enumeration_value}" />
					</xsl:for-each>
				</xsl:if>
				<xsl:if test="document($type_file)/type/int8 or document($type_file)/type/int16 or document($type_file)/type/int32 or document($type_file)/type/int64 or document($type_file)/type/float32 or document($type_file)/type/float64">
					<xsl:if test="document($type_file)/type/*[2]/@min">
						<xsd:minInclusive value="{document($type_file)/type/*[2]/@min}" />
					</xsl:if>
					<xsl:if test="document($type_file)/type/*[2]/@max">
						<xsd:maxInclusive value="{document($type_file)/type/*[2]/@max}" />
					</xsl:if>
				</xsl:if>
				<xsl:if test="document($type_file)/type/base64">
					<xsl:if test="document($type_file)/base64/@min">
						<xsd:minLength value="{document($type_file)/base64/@min}" />
					</xsl:if>
					<xsl:if test="document($type_file)/base64/@max">
						<xsd:maxLength value="{document($type_file)/base64/@max}" />
					</xsl:if>
				</xsl:if>
			</xsd:restriction>
		</xsd:simpleType>
	</xsl:template>
	
	<xsl:template name="elementtype">
		
		<xsl:param name="project_file" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api"          />
		<xsl:param name="type"         />

		<xsl:choose>
			<xsl:when test="starts-with($type, '_') or string-length($type) = 0">
				<xsl:variable name="soaptype">
					<xsl:call-template name="soaptype_for_type">
						<xsl:with-param name="project_file" select="$project_file" />
						<xsl:with-param name="specsdir"     select="$specsdir" />
						<xsl:with-param name="api"          select="$api" />
						<xsl:with-param name="type"         select="@type" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:text>xsd:</xsl:text>
				<xsl:value-of select="$soaptype" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>tns:</xsl:text>
				<xsl:value-of select="$type" />
				<xsl:text>Type</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
