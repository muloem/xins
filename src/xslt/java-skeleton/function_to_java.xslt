<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="classname"    />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../function.xslt" />
	<xsl:include href="../types.xslt"  />

	<xsl:template match="function">
		<xsl:variable name="api" select="@api" />

		<xsl:variable name="sessionBased">
			<xsl:call-template name="is_function_session_based" />
		</xsl:variable>

		<xsl:text><![CDATA[/*
 * $]]><![CDATA[Id$
 */
package ]]></xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

import org.xins.server.Function;
import org.xins.server.Responder;
import org.xins.server.Session;

/**
 * Implementation of the <code>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</code> function.
 *
 * @version $]]><![CDATA[Revision$ $]]><![CDATA[Date$
 * @author TODO
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
		<xsl:if test="$sessionBased = 'true'">
			<xsl:text>, Session session</xsl:text>
		</xsl:if>
		<xsl:for-each select="input/param">
			<xsl:text>, </xsl:text>
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="required" select="@required" />
				<xsl:with-param name="type"     select="@type"     />
			</xsl:call-template>
			<xsl:text> </xsl:text>
			<xsl:value-of select="@name" />
		</xsl:for-each>
		<xsl:text><![CDATA[)
   throws Throwable {
      // TODO
   }
}
]]></xsl:text>
	</xsl:template>

</xsl:stylesheet>
