<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the abstract class as specified in the function.
 The abtract class is responsible for checking the parameters.
 It also includes the style sheets request_java.xslt and result_java.xslt.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<!-- Perform includes -->
	<xsl:include href="../java.xslt"  />
	<xsl:include href="../rcs.xslt"   />
	<xsl:include href="../types.xslt" />

	<xsl:output method="text" />

	<xsl:template match="impl | api">

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text><![CDATA[;

/**
 * Class containing the method to access the properties of the <code>]]></xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text><![CDATA[</code> API.
 */
public class RuntimeProperties extends org.xins.server.RuntimeProperties {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
]]></xsl:text>

		<xsl:apply-templates select="runtime-properties/property | impl-java/runtime-properties/property" mode="field" />

		<xsl:text>
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected void init(org.xins.common.collections.PropertyReader runtimeSettings)
   throws org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException {

      // Get the properties</xsl:text>

		<xsl:apply-templates select="runtime-properties/property | impl-java/runtime-properties/property" mode="check" />

		<xsl:text>
   }</xsl:text>
		<xsl:apply-templates select="runtime-properties/property | impl-java/runtime-properties/property" mode="method" />
}
	</xsl:template>

	<!-- Generates the checking code. -->
	<xsl:template match="impl/runtime-properties/property[@type='_descriptor'] | api/impl-java/runtime-properties/property[@type='_descriptor']" mode="check">
		<xsl:variable name="variableName">
			<xsl:call-template name="hungarianPropertyLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:text>
      _</xsl:text>
		<xsl:value-of select="$variableName" />
		<xsl:text> = org.xins.common.service.DescriptorBuilder.build(runtimeSettings, "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>");</xsl:text>
	</xsl:template>

	<xsl:template match="impl/runtime-properties/property | api/impl-java/runtime-properties/property" mode="check">
		<xsl:variable name="variableName">
			<xsl:call-template name="hungarianPropertyLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:text>
      java.lang.String </xsl:text>
		<xsl:value-of select="$variableName" />
		<xsl:text> = runtimeSettings.get("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>");</xsl:text>
		<xsl:if test="@required = 'true'">
			<xsl:text>
      if (</xsl:text>
			<xsl:value-of select="$variableName" />
			<xsl:text> == null || </xsl:text>
			<xsl:value-of select="$variableName" />
			<xsl:text>.equals("")) {
         throw new org.xins.common.collections.MissingRequiredPropertyException("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>");
      }</xsl:text>
		</xsl:if>

		<xsl:if test="@required = 'false'">
			<xsl:text>
      if (</xsl:text>
			<xsl:value-of select="$variableName" />
			<xsl:text> != null &amp;&amp; !</xsl:text>
			<xsl:value-of select="$variableName" />
			<xsl:text>.equals("")) {</xsl:text>
		</xsl:if>
		<xsl:if test="@type and not(@type = '_text')">
			<xsl:text>
      try {</xsl:text>
		</xsl:if>
		<xsl:text>
         _</xsl:text>
			<xsl:value-of select="$variableName" />
			<xsl:text> = </xsl:text>
			<xsl:call-template name="javatype_from_string_for_type">
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="required" select="@required" />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="type"     select="@type"     />
			<xsl:with-param name="variable" select="$variableName"     />
			</xsl:call-template>
			<xsl:text>;</xsl:text>
		<xsl:if test="@type and not(@type = '_text')">
			<xsl:text>
      } catch (org.xins.common.types.TypeValueException exception) {
         throw new org.xins.common.collections.InvalidPropertyValueException("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>", </xsl:text>
			<xsl:value-of select="$variableName" />
			<xsl:text>);
      }</xsl:text>
		</xsl:if>
		<xsl:if test="@required = 'false'">
			<xsl:text>
      }</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Generates the fields. -->
	<xsl:template match="impl/runtime-properties/property | api/impl-java/runtime-properties/property" mode="field">
		<!-- TODO translate the variable -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="@required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="variableName">
			<xsl:call-template name="hungarianPropertyLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:text>

   private </xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text> _</xsl:text>
		<xsl:value-of select="$variableName" />
		<xsl:text>;</xsl:text>
	</xsl:template>

	<!-- Generates the get methods. -->
	<xsl:template match="impl/runtime-properties/property | api/impl-java/runtime-properties/property" mode="method">
		<!-- TODO translate the variable -->
		<xsl:variable name="basetype">
			<xsl:call-template name="basetype_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="type"     select="@type"     />
			</xsl:call-template>
		</xsl:variable>
		<!-- Get the name of the variable. -->
		<xsl:variable name="variableName">
			<xsl:call-template name="hungarianPropertyLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<!-- Get the name of the get method. -->
		<xsl:variable name="methodName">
			<xsl:call-template name="hungarianPropertyUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<!-- Get the return type of the variable. -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="@required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<!-- Generates the get method. -->
		<xsl:text><![CDATA[

   /**
    * Gets the value of the ]]></xsl:text>
		<xsl:choose>
			<xsl:when test="@required = 'true'">
				<xsl:text>required</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>optional</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text><![CDATA[ property <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em>,
    * which is ]]></xsl:text>
		<xsl:value-of select="description/text()" />
		<xsl:text><![CDATA[
    *
    * @return
    *    the value of the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> property]]></xsl:text>
		<xsl:choose>
			<xsl:when test="@required = 'true'">
				<xsl:text><![CDATA[, never <code>null</code>.]]></xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text><![CDATA[, can be <code>null</code>.]]></xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>
    */
   </xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text> get</xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>() </xsl:text>
		<xsl:text>{
      return _</xsl:text>
			<xsl:value-of select="$variableName" />
		<xsl:text>;
   }</xsl:text>
	</xsl:template>

</xsl:stylesheet>