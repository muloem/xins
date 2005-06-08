<?xml version="1.0" encoding="UTF-8" ?>

<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the stub implementation from the examples in the function.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<!-- Perform import -->
	<xsl:import href="function_to_impl_java.xslt" />

	<!-- Write the content of the call method -->
	<xsl:template name="callcontent">
		<xsl:apply-templates select="example" mode="if" />
		<xsl:text>
      throw new Exception("Not implemented in stub");</xsl:text>
	</xsl:template>
	
	<!-- Write the text that should be generated after the call method -->
	<xsl:template name="aftercall">
		<xsl:apply-templates select="example" mode="method">
			<xsl:with-param name="api" select="$api" />
			<xsl:with-param name="specsdir" select="$specsdir" />
		</xsl:apply-templates>
	</xsl:template>

	<!-- Write the if statements -->
	<xsl:template match="example" mode="if">
		<xsl:choose>
			<xsl:when test="@resultcode and starts-with(@resultcode, '_')" />
			<xsl:otherwise>
				<xsl:text>
      if (</xsl:text>
				<xsl:for-each select="input-example">
					<!-- Get the name of the get method. -->
					<xsl:variable name="hungarianName">
						<xsl:call-template name="hungarianUpper">
							<xsl:with-param name="text" select="@name" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:if test="position() &gt; 1">
						<xsl:text> &amp;&amp; </xsl:text>
					</xsl:if>
					<xsl:text>String.valueOf(request.get</xsl:text>
					<xsl:value-of select="$hungarianName" />
					<xsl:text>()).equals("</xsl:text>
					<xsl:value-of select="text()" />
					<xsl:text>")</xsl:text>
				</xsl:for-each>
				<xsl:text>) {
         return example</xsl:text>
				<xsl:value-of select="position()" />
				<xsl:text>();
      }</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Write the methods -->
	<xsl:template match="example" mode="method">
		<xsl:param name="api" />
		<xsl:param name="specsdir" />
		
		<xsl:choose>
			<xsl:when test="@resultcode and starts-with(@resultcode, '_')" />
			<xsl:otherwise>
			
				<xsl:variable name="resultclass">
					<xsl:choose>
						<xsl:when test="@resultcode">
							<xsl:value-of select="@resultcode" />
							<xsl:text>Result</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>SuccessfulResult</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				
				<xsl:text>

   public Result example</xsl:text>
				<xsl:value-of select="position()" />
				<xsl:text>() throws Exception {
      </xsl:text>
				<xsl:value-of select="$resultclass" />
				<xsl:text> result = new </xsl:text>
				<xsl:value-of select="$resultclass" />
				<xsl:text>();</xsl:text>
				<xsl:for-each select="output-example">

					<xsl:variable name="parametername" select="@name" />
					<xsl:variable name="hungarianName">
						<xsl:call-template name="hungarianUpper">
							<xsl:with-param name="text" select="@name" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="required" select="/function/output/param[@name=$parametername]/@required" />
					<xsl:variable name="type" select="/function/output/param[@name=$parametername]/@type" />
					
					<xsl:text>
      result.set</xsl:text>
					<xsl:value-of select="$hungarianName" />
					<xsl:text>(</xsl:text>
					<xsl:call-template name="javatype_from_string_for_type">
						<xsl:with-param name="api"      select="$api"      />
						<xsl:with-param name="required" select="$required" />
						<xsl:with-param name="specsdir" select="$specsdir" />
						<xsl:with-param name="type"     select="$type"     />
						<xsl:with-param name="variable" select="concat('&quot;', text(), '&quot;')" />
					</xsl:call-template>
					<xsl:text>);</xsl:text>
				</xsl:for-each>
				<xsl:text>

      return result;
   }</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
