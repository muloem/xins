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
      super(</xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text><![CDATA[Function.SINGLETON);

      // Register all (input) parameters with their corresponding types]]></xsl:text>
		<xsl:for-each select="input/param">
			<xsl:text>
      super.parameterType("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>", </xsl:text>
			<xsl:call-template name="javatypeclass_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
			<xsl:text>.SINGLETON);</xsl:text>
		</xsl:for-each>
		<xsl:text>
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------></xsl:text>

		<xsl:apply-templates select="input/param" mode="field" />

		<xsl:text>

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------</xsl:text>

		<xsl:apply-templates select="input/param" mode="methods" />

		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template match="input/param" mode="field">

		<!-- Determine the Java class -->
		<xsl:variable name="javaclass">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="'false'"        />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<!-- Print field -->
		<xsl:text><![CDATA[

   /**
    * Value of the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> input parameter.
    * Can be <code>null</code>.
    */
   private ]]></xsl:text>
		<xsl:value-of select="$javaclass" />
		<xsl:text> _</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>;</xsl:text>
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

		<!-- Determine the names of the methods -->
		<xsl:variable name="getMethod">
			<xsl:text>get</xsl:text>
			<xsl:value-of select="translate(substring(@name,1,1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
			<xsl:value-of select="substring(@name,2)" />
		</xsl:variable>
		<xsl:variable name="setMethod">
			<xsl:text>set</xsl:text>
			<xsl:value-of select="translate(substring(@name,1,1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
			<xsl:value-of select="substring(@name,2)" />
		</xsl:variable>

		<!-- Print getter method -->
		<xsl:text><![CDATA[

   /**
    * Retrieves the value of the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> input parameter.
    * If the value was not set yet, then <code>null</code> is returned.
    *
    * @return
    *    the current value for the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em>
    *    input parameter, can be <code>null</code>.
    */
   public ]]></xsl:text>
		<xsl:value-of select="$javaclass" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$getMethod" />
		<xsl:text>() {
      return _</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>;
   }</xsl:text>

		<xsl:choose>

			<!-- Print setter method that accepts a Java primary data type -->
			<xsl:when test="$isJavaDatatype = 'true'">

				<xsl:text><![CDATA[

   /**
    * Sets the <em>]]></xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text><![CDATA[</em> parameter as ]]></xsl:text>
				<xsl:choose>
					<xsl:when test="translate(substring($javatype,1,1),'aeiouy','******') = '*'">
						<xsl:text>an </xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>a </xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text><![CDATA[<code>]]></xsl:text>
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
      _</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text> = new </xsl:text>
				<xsl:value-of select="$javaclass" />
				<xsl:text>(</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>);
      super.parameterValue("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>", _</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>);
   }</xsl:text>
			</xsl:when>

			<!-- Print setter method that accepts a Java object -->
			<xsl:otherwise>
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
				<xsl:value-of select="$setMethod" />
				<xsl:text>(</xsl:text>
				<xsl:value-of select="$javaclass" />
				<xsl:text> </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>) {
      _</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text> = </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>;
      super.parameterValue("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>", _</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>);
   }</xsl:text>

			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>

