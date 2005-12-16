<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the result code used in the functions of the api.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<!-- Define parameters -->
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<!-- Perform includes -->
	<xsl:include href="check_params.xslt"  />
	<xsl:include href="result_java.xslt"   />
	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../java.xslt"       />
	<xsl:include href="../types.xslt"      />
	<xsl:include href="../warning.xslt"    />

	<xsl:variable name="project_node" select="document($project_file)/project" />
	<xsl:variable name="api_node" select="document($api_file)/api" />

	<xsl:template match="resultcode">

		<xsl:variable name="resultcode" select="@name" />
		<xsl:variable name="className" select="concat($resultcode, 'Result')" />

		<xsl:variable name="resultcodeIncludes">
			<xsl:for-each select="$api_node/function">
				<xsl:call-template name="search-matching-resultcode">
					<xsl:with-param name="functionName" select="@name" />
					<xsl:with-param name="resultcode" select="$resultcode" />
				</xsl:call-template>
			</xsl:for-each>
		</xsl:variable>
		<!-- Truncate the first ", " -->
		<xsl:variable name="resultcodeIncludes2"    select="concat('implements ', substring($resultcodeIncludes, 2))" />

		<!-- Warn if name differs from value -->
		<xsl:if test="(string-length(@value) &gt; 0) and (not(@value = @name))">
			<xsl:call-template name="warn">
				<xsl:with-param name="message">
					<xsl:text>Errorcode name ('</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>') differs from value ('</xsl:text>
					<xsl:value-of select="@value" />
					<xsl:text>'). This may cause confusion and errors.</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<!-- Warn if no function uses this ResultCode -->
		<xsl:if test="$resultcodeIncludes = ''">
			<xsl:call-template name="warn">
				<xsl:with-param name="message">
					<xsl:text>Errorcode '</xsl:text>
					<xsl:value-of select="$resultcode" />
					<xsl:text>'is not used in any function.</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text>;

/**
 * UnsuccessfulResult due to a </xsl:text>
		<xsl:value-of select="$resultcode" />
		<xsl:if test="$resultcodeIncludes = ''">
			<xsl:call-template name="warn">
            <xsl:with-param name="message">
               <xsl:text>This errorcode is not used in any function.</xsl:text>
            </xsl:with-param>
         </xsl:call-template>
		</xsl:if>
		<xsl:text>.
 */
final class </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text> extends org.xins.server.FunctionResult </xsl:text>
		<!-- This class should implements the UnsuccessfulResult from all the functions
		     that reference to this result code. -->
		<xsl:if test="not($resultcodeIncludes = '')">
			<xsl:value-of select="$resultcodeIncludes2" />
		</xsl:if>
		<xsl:text> {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------
</xsl:text>
		<xsl:call-template name="constructor">
			<xsl:with-param name="className" select="$className" />
		</xsl:call-template>
		<xsl:text>

   //-------------------------------------------------------------------------
   // Field
   //-------------------------------------------------------------------------
</xsl:text>
		<!-- Generate the set methods, the inner classes and the add methods -->
		<xsl:apply-templates select="output">
		</xsl:apply-templates>
		<xsl:text>
}
</xsl:text>
		<xsl:apply-templates select="ouput/data/element" mode="addElementClass">
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template name="constructor">
		<xsl:param name="className" />
		<xsl:text><![CDATA[
   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    */
   ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>() {
      super("</xsl:text>
		<xsl:choose>
			<xsl:when test="@value">
				<xsl:value-of select="@value" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@name" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>");
   }</xsl:text>
	</xsl:template>

	<xsl:template name="search-matching-resultcode">
		<!-- Define parameters -->
		<xsl:param name="functionName" />
		<xsl:param name="resultcode" />

		<!-- Determine file that defines type -->
		<xsl:variable name="functionFile"    select="concat($specsdir, '/', $functionName, '.fnc')" />

		<xsl:for-each select="document($functionFile)/function/output/resultcode-ref">
			<xsl:if test="@name = $resultcode">
				<xsl:text>, </xsl:text>
				<xsl:value-of select="$functionName" />
				<xsl:text>.UnsuccessfulResult</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
