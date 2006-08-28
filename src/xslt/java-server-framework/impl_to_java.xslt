<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the RuntimeProperties class. This class contains methods
 to access the avlue of the runtime properties.

 $Id$

 Copyright 2003-2006 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />

	<!-- Perform includes -->
	<xsl:include href="../java.xslt"  />
	<xsl:include href="../rcs.xslt"   />
	<xsl:include href="../types.xslt" />

	<xsl:output method="text" />

	<xsl:variable name="project_node" select="document($project_file)/project" />

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
   // Fields
   //-------------------------------------------------------------------------
]]></xsl:text>

		<xsl:apply-templates select="runtime-properties/property | impl-java/runtime-properties/property" mode="field" />

		<xsl:text>

   /**
    * The list of descriptors with one or more target descriptors.
    */
   private java.util.List _descriptors;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected void init(org.xins.common.collections.PropertyReader runtimeSettings)
   throws org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException {

      // Initializing descriptor list.
      _descriptors = new java.util.ArrayList();

      // Get the properties</xsl:text>

		<xsl:apply-templates select="runtime-properties/property | impl-java/runtime-properties/property" mode="check" />

		<xsl:text>

      // The list is made unmodifiable so that no one can change it.
      _descriptors = java.util.Collections.unmodifiableList(_descriptors);
   }</xsl:text>
		<xsl:apply-templates select="runtime-properties/property | impl-java/runtime-properties/property" mode="method" />

   /**
    * Gets the descriptor list. The list is created by getting all the
    * properties which are marked as <i>_descriptor</i> in runtime properties
    * file.
    *
    * @return
    *    the unmodifiable list of all descriptors, never <code>null</code>.
    */
   protected java.util.List descriptors() {
      return _descriptors;
   }
}
	</xsl:template>

	<!-- Generates the checking code. -->
	<xsl:template match="impl/runtime-properties/property | api/impl-java/runtime-properties/property" mode="check">
		<xsl:variable name="variableName">
			<xsl:call-template name="hungarianLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="@type = '_descriptor'">
				<xsl:text>
      _</xsl:text>
				<xsl:value-of select="$variableName" />
				<xsl:text> = org.xins.common.service.DescriptorBuilder.build(runtimeSettings, "</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>");</xsl:text>
            <xsl:text>
      _descriptors.add(_</xsl:text>
            <xsl:value-of select="$variableName" />
            <xsl:text>);</xsl:text>

			</xsl:when>
			<xsl:otherwise>
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

				<xsl:if test="not(starts-with(@type, '_')) and not(string-length(@type) = 0)">
					<xsl:variable name="class">
						<xsl:call-template name="javatype_for_customtype">
							<xsl:with-param name="project_node" select="$project_node" />
							<xsl:with-param name="api"          select="$api"          />
							<xsl:with-param name="type"         select="@type"         />
						</xsl:call-template>
					</xsl:variable>
					<xsl:text>
      if (!</xsl:text>
					<xsl:value-of select="$class" />
					<xsl:text>.SINGLETON.isValidValue(</xsl:text>
					<xsl:value-of select="$variableName" />
					<xsl:text>)) {
         throw new org.xins.common.collections.InvalidPropertyValueException("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>", </xsl:text>
					<xsl:value-of select="$variableName" />
					<xsl:text>);
      }</xsl:text>
				</xsl:if>

				<xsl:text>
      try {</xsl:text>
				<xsl:text>
         _</xsl:text>
					<xsl:value-of select="$variableName" />
					<xsl:text> = </xsl:text>
					<xsl:call-template name="javatype_from_string_for_type">
						<xsl:with-param name="api"      select="$api"      />
						<xsl:with-param name="required" select="@required" />
						<xsl:with-param name="specsdir" select="$specsdir" />
						<xsl:with-param name="type"     select="@type"     />
						<xsl:with-param name="variable" select="$variableName" />
					</xsl:call-template>
					<xsl:text>;
      } catch (org.xins.common.types.TypeValueException exception) {
         throw new org.xins.common.collections.InvalidPropertyValueException("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>", </xsl:text>
					<xsl:value-of select="$variableName" />
					<xsl:text>);
      }</xsl:text>
				<xsl:if test="@required = 'false'">
					<xsl:text>
      }</xsl:text>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Generates the fields. -->
	<xsl:template match="impl/runtime-properties/property | api/impl-java/runtime-properties/property" mode="field">
		<!-- TODO translate the variable -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="@required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="variableName">
			<xsl:call-template name="hungarianLower">
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
			<xsl:call-template name="hungarianLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<!-- Get the name of the get method. -->
		<xsl:variable name="methodName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<!-- Get the return type of the variable. -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
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
   public </xsl:text>
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
