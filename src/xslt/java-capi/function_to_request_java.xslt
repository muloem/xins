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

	<xsl:template match="function">
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision">
					<xsl:value-of select="//function/@rcsversion" />
				</xsl:with-param>
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

   /**
    * Creates a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    */
   public static final ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[ create() {
      return new ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------
		
   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    */
   private ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>() {
      super("</xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text><![CDATA[");
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Validates whether this request is considered acceptable (implementation
    * method). If required parameters are missing or if certain parameter
    * values are out of bounds, then a description or the problem is returned.
    *
    * <p>This method is called by {@link #validate()}. It should not be called
    * from anywhere else.
    *
    * @return
    *    <code>null</code> if this request is considered acceptable or a
    *    non-<code>null</code> description if this request is considered
    *    unacceptable.
    */
   public java.lang.String validateImpl() {
      return null; // TODO
   }]]></xsl:text>

		<xsl:apply-templates select="input/param"/>

		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template match="input/param">

		<!-- Determine if this parameter is required -->
		<xsl:variable name="required">
			<xsl:choose>
				<xsl:when test="string-length(@required) &lt; 1">false</xsl:when>
				<xsl:when test="@required = 'false'">false</xsl:when>
				<xsl:when test="@required = 'true'">true</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<!-- Determine the Java class or primary data type -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="$required"     />
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

		<!-- Determine the name of the setter method -->
		<xsl:variable name="methodName">
			<xsl:text>set</xsl:text>
			<xsl:value-of select="translate(substring(@name,1,1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
			<xsl:value-of select="substring(@name,2)" />
		</xsl:variable>

		<!-- Print setter method that accepts a Java object -->
		<xsl:text><![CDATA[

   /**
    * Sets or resets the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> parameter.
    *
    * @param ]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[
    *    the new value for the parameter, can be <code>null</code>.
    */
   public void ]]></xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$javaclass" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>) {
      // TODO: _request.setParameter("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>);
   }</xsl:text>
	</xsl:template>

</xsl:stylesheet>

