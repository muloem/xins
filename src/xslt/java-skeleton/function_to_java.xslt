<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="package" />
	<xsl:param name="classname" />

	<xsl:template match="function">
		<xsl:variable name="api" select="@api" />

		<xsl:text><![CDATA[/*
 * $]]><![CDATA[Id$
 */
package ]]></xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

import java.io.IOException;
import org.xins.server.Function;
import org.xins.server.Responder;

/**
 * Implementation of the <code>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</code> function.
 *
 * @author TODO
 * @version $]]><![CDATA[Revision$ $]]><![CDATA[Date$
 */
public class ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text> extends </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[  {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
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
		<xsl:text><![CDATA[(APIImpl api) {
      super(api);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public final void call(Responder responder]]></xsl:text>
		<xsl:for-each select="input/param">
			<xsl:text>, </xsl:text>
			<xsl:text>String </xsl:text>
			<xsl:value-of select="@name" />
		</xsl:for-each>
		<xsl:text><![CDATA[)
   throws IOException {
      // TODO
   }
}
]]></xsl:text>
	</xsl:template>

</xsl:stylesheet>
