<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the skeleton for the implementation of the function.

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="classname"    />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../types.xslt"  />
	<xsl:include href="../author.xslt" />

	<xsl:template match="function">

		<xsl:text><![CDATA[/*
 * $]]><![CDATA[Id$
 */
package ]]></xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

/**
 * Implementation of the <code>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</code> function.
 *
 * @version $]]><![CDATA[Revision$ $]]><![CDATA[Date$
 * @author ]]></xsl:text>
				<xsl:variable name="owner_info">
					<xsl:apply-templates select="document($api_file)/api" mode="owner" />
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$owner_info != ''">
						<xsl:value-of select="$owner_info" disable-output-escaping="yes"/>
					</xsl:when>
					<!-- Split the text up, so it does not match when searched for -->
					<xsl:otherwise>
						<xsl:text>TO</xsl:text>
						<xsl:text>DO</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
		<xsl:text><![CDATA[
 */
public class ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text> extends </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[  {

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
		<xsl:value-of select="$classname" />
		<xsl:text><![CDATA[</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   public ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text>(APIImpl api) {
      super(api);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public final Result call(Request request) throws Throwable {
      SuccessfulResult result = new SuccessfulResult();
      // TO</xsl:text>
		<!-- Split this text up so it does not match when searched for -->
		<xsl:text>DO
      return result;
   }
}
</xsl:text>
	</xsl:template>

</xsl:stylesheet>
