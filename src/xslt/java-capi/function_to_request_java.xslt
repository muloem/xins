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
 */
public final class ]]></xsl:text>
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
   public java.lang.String validateImpl()
   throws org.xins.client.UnacceptableRequestException {
      return null; // TODO
   }]]></xsl:text>

		<xsl:for-each select="input/param">
			<xsl:text><![CDATA[

   /**
    * Sets the <em>]]></xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text><![CDATA[</em> parameter.
    *
    * @param ]]></xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text><![CDATA[
    *    the new value for the parameter, can be <code>null</code>.
    */
   public void set]]></xsl:text>
			<xsl:value-of select="translate(substring(@name,1,1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
			<xsl:value-of select="substring(@name,2)" />
			<xsl:text>(java.lang.String </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>) {
      _request.setParameter("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>", </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>);
   }</xsl:text>
		</xsl:for-each>

		<xsl:text>
}
</xsl:text>
	</xsl:template>
</xsl:stylesheet>

