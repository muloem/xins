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
								xmlns:xsd="http://www.w3.org/2001/XMLSchema"
								xmlns:soapbind="http://schemas.xmlsoap.org/wsdl/soap/"
								version="1.0">

	<xsl:include href="../types.xslt"  />
	
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />

	<xsl:output method="xml" indent="yes" />

	<xsl:template match="api">
	
		<xsl:variable name="apiname" select="@name" />
		
		<definitions name="{$apiname}"
			xmlns="http://schemas.xmlsoap.org/wsdl/"
			xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
			xmlns:soapbind="http://schemas.xmlsoap.org/wsdl/soap/"
			xmlns:http="http://schemas.xmlsoap.org/wsdl/http/"
			xmlns:xsd="http://www.w3.org/2001/XMLSchema">
			<types>
				<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"
										elementFormDefault="qualified"
										attributeFormDefault="unqualified">
					
					<!-- Write the elements -->
					<xsl:apply-templates select="function" mode="elements">
						<xsl:with-param name="project_file" select="$project_file" />
						<xsl:with-param name="specsdir"     select="$specsdir"     />
						<xsl:with-param name="api"          select="$apiname"      />
					</xsl:apply-templates>
				</xsd:schema>
			</types>
					
			<!-- Write the messages -->
			<xsl:apply-templates select="function" mode="messages" />

			<!-- Write the port types -->
			<portType name="{$apiname}PortType">
				<xsl:apply-templates select="function" mode="porttypes" />
			</portType>
			
			<!-- Write the bindings -->
			<binding name="{$apiname}SOAPBinding" type="{$apiname}PortType">
				<document>
					<xsl:value-of select="description" />
				</document>
				<soapbind:binding transport="http://schemas.xmlsoap.org/soap/http" style="document"/>
				<xsl:apply-templates select="function" mode="bindings" />
			</binding>
			
			<!-- Write the services -->
			<xsl:variable name="location">
				<xsl:choose>
					<xsl:when test="document($project_file)/project/api[@name=$apiname]/environments">
						<xsl:variable name="env_file" select="concat($project_home, '/apis/', $apiname, '/environments.xml')" />
						<xsl:value-of select="document($env_file)/environments/environment[0]/@url" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>http://localhost:8080/</xsl:text>
						<xsl:value-of select="$apiname" />
						<xsl:text>/</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<service name="{$apiname}Service">
				<port name="{$apiname}Port" binding="{$apiname}SOAPBinding">
					<soapbind:address location="{$location}" />
				</port>
			</service>
		</definitions>
	</xsl:template>
	
	<xsl:template match="function" mode="elements">

		<xsl:param name="project_file" />
		<xsl:param name="specsdir"     />
		<xsl:param name="api" />
		
		<xsl:variable name="functionname" select="@name" />
		<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.fnc')" />

		<!-- The input parameters of the function -->
		<xsd:element name="{$functionname}">
			<xsd:complexType>
				<xsd:sequence>
					<xsl:for-each select="document($function_file)/function/input/param">
						<xsl:variable name="soaptype">
							<xsl:call-template name="soaptype_for_type">
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
						<xsd:element minOccurs="{$minoccurs}" maxOccurs="1" name="{$paramname}" type="xsd:{$soaptype}" />
					</xsl:for-each>
				</xsd:sequence>
			</xsd:complexType>
		</xsd:element>
		
		<!-- The output parameters of the function -->
		<xsd:element name="{$functionname}Response">
			<xsd:complexType>
				<xsd:sequence>
					<xsl:for-each select="document($function_file)/function/output/param">
						<xsl:variable name="soaptype">
							<xsl:call-template name="soaptype_for_type">
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
						<xsd:element minOccurs="{$minoccurs}" maxOccurs="1" name="{$paramname}" type="xsd:{$soaptype}" />
					</xsl:for-each>
				</xsd:sequence>
			</xsd:complexType>
		</xsd:element>
	</xsl:template>
	
	<xsl:template match="function" mode="messages">
	
		<xsl:variable name="functionname" select="@name" />
		
		<message name="{$functionname}Input">
			<part name="parameters" element="tns:{$functionname}" />
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
	
		<xsl:variable name="functionname" select="@name" />
		<xsl:variable name="function_file" select="concat($specsdir, '/', @name, '.fnc')" />
		
		<operation name="{$functionname}">
			<documentation>
				<xsl:value-of select="document($function_file)/function/description" />
			</documentation>
			<input>
				<soapbind:body use="literal"/>
			</input>
			<output>
				<soapbind:body use="literal"/>
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
</xsl:stylesheet>
