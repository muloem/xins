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
		<xsl:variable name="className">
			<xsl:value-of select="$functionName" />
			<xsl:text>Function</xsl:text>
		</xsl:variable>

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

/**
 * Representation of the <em>]]></xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text><![CDATA[</em> function.
 *
 * @see CAPI
 * @see ]]></xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text>Request
 * @see </xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text>Result
 */
final class </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>
extends org.xins.client.AbstractCAPIFunction {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The one and only instance of this class.
    */
   static final </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text> SINGLETON = new </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[();


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
      super("</xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text><![CDATA[", null, null);
      // TODO: Pass input constraints to superclass
      // TODO: Pass output constraints to superclass
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}]]>
</xsl:text>
	</xsl:template>
</xsl:stylesheet>
