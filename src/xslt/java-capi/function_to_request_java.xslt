<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the Request classes.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />
	<xsl:include href="../types.xslt"  />
	<xsl:include href="../java-server-framework/check_params.xslt"  />

	<xsl:template match="function">
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision" select="//function/@rcsversion" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="functionName" select="@name" />
		<xsl:variable name="className" select="concat($functionName,'Request')" />

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

/**
 * Request for a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 * <p>An instance of this class is accepted by the corresponding call method
 * in the CAPI class: {@link CAPI#call]]></xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>)}.
 *
 * @see CAPI
 * @see </xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text>Result
 */
public final class </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[
extends org.xins.client.AbstractCAPICallRequest {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    */
   public ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>() {
      super(&quot;</xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text>&quot;);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------</xsl:text>

		<xsl:apply-templates select="input/param" mode="methods" />

		<xsl:apply-templates select="input/data/element" mode="methods" />
		
		<xsl:text><![CDATA[

   /**
    * Validates whether this request is considered acceptable. If any
    * constraints are violated, then an {@link UnacceptableRequestException}
    * is returned.
    *
    * <p>This method is called automatically when this request is executed, so
    * it typically does not need to be called manually in advance.
    *
    * @return
    *    an {@link UnacceptableRequestException} instance if this request is
    *    considered unacceptable, otherwise <code>null</code>.
    */
   public org.xins.client.UnacceptableRequestException checkParameters() {
]]></xsl:text>
		<xsl:apply-templates select="input" mode="checkParams">
			<xsl:with-param name="side" select="'client'" />
		</xsl:apply-templates>
		<xsl:if test="not(input)">
			<xsl:text>
      return null;</xsl:text>
		</xsl:if>
		<xsl:text>
   }
</xsl:text>
		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template match="input/param" mode="methods">

		<!-- Determine the Java class or primary data type -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="'true'"        />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine if $javatype is a Java primary data type -->
		<xsl:variable name="isJavaDatatype">
			<xsl:call-template name="is_java_datatype">
				<xsl:with-param name="text" select="$javatype" />
			</xsl:call-template>
		</xsl:variable>

		<!-- If $javatype is a primary data type, determine class -->
		<xsl:variable name="javaclass">
			<xsl:choose>
				<xsl:when test="$isJavaDatatype = 'true'">
					<xsl:call-template name="javaclass_for_javatype">
						<xsl:with-param name="javatype" select="$javatype" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$javatype" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- Determine the Java class that represents the type -->
		<xsl:variable name="typeclass">
			<xsl:call-template name="javatypeclass_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine the method that transform the value to a String -->
		<xsl:variable name="typeToString">
			<xsl:call-template name="javatype_to_string_for_type">
				<xsl:with-param name="api"      select="$api" />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="required" select="@required" />
				<xsl:with-param name="type"     select="@type" />
				<xsl:with-param name="variable" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine the names of the methods -->
		<xsl:variable name="setMethod">
			<xsl:text>set</xsl:text>
			<xsl:value-of select="translate(substring(@name,1,1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
			<xsl:value-of select="substring(@name,2)" />
		</xsl:variable>

		<xsl:text><![CDATA[

   /**
    * Sets or resets the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> parameter as a]]></xsl:text>
		<xsl:if test="translate(substring($javatype,1,1),'aeiouy','******') = '*'">
			<xsl:text>n</xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[ <code>]]></xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text><![CDATA[</code>.
    *
    * @param ]]></xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text><![CDATA[
    *    the new value for the parameter.
    */
   public void ]]></xsl:text>
		<xsl:value-of select="$setMethod" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>) {
      </xsl:text>
		<xsl:if test="$isJavaDatatype = 'false'" >
			<xsl:text>if (</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> != null &amp;&amp; !</xsl:text>
			<xsl:value-of select="$typeToString" />
			<xsl:text>.equals("")) {
         </xsl:text>
		</xsl:if>
		<xsl:text>parameterValue("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>",  </xsl:text>
		<xsl:value-of select="$typeToString" />
		<xsl:text>);</xsl:text>
		<xsl:if test="$isJavaDatatype = 'false'" >
			<xsl:text>
      }</xsl:text>
		</xsl:if>
		<xsl:text>
   }</xsl:text>

	</xsl:template>

	<xsl:template match="input/data/element" mode="methods">
	
		<xsl:text><![CDATA[

   /**
    * Sets the data section. 
    * If the value is <code>null</code> any previous data section set is removed.
    * If a previous value was entered, the value will be overridden by this new
    * value.
    *
    * @param dataSection
    *    The data section.
    */
   public void addDataSection(org.xins.common.xml.Element dataSection) {
      putDataSection(dataSection);
   }]]></xsl:text>

	</xsl:template>
</xsl:stylesheet>

