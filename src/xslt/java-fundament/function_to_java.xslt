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
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../java.xslt" />

	<xsl:template match="function">
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text><![CDATA[;

import java.io.IOException;
import org.xins.server.Function;
import org.xins.server.Responder;

/**
 * Abstract base class for <code>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</code> function implementation.
 */
public abstract class ]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[ extends Function {

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
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   protected ]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>(APIImpl api) {
      super(api);</xsl:text>
		<xsl:for-each select="document($api_file)/api/impl-java/instance">
			<xsl:text>
      </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> = api.</xsl:text>
			<xsl:value-of select="@getter" />
			<xsl:text>();</xsl:text>
		</xsl:for-each>
		<xsl:text><![CDATA[
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
]]></xsl:text>
		<xsl:for-each select="document($api_file)/api/impl-java/instance">
			<xsl:text>
   protected final </xsl:text>
			<xsl:value-of select="@class" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;

</xsl:text>
		</xsl:for-each>
		<xsl:text><![CDATA[
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Calls this function.
    *
    * @param responder
    *    the responder to be used, never <code>null</code>.]]></xsl:text>
		<xsl:for-each select="input/param">
			<xsl:text><![CDATA[
    *
    * @param ]]></xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text><![CDATA[
    *    ]]></xsl:text>
			<!-- TODO: Use an intelligent template -->
			<xsl:value-of select="description/text()" />
		</xsl:for-each>
		<xsl:text><![CDATA[
    */
   public abstract void call(Responder responder]]></xsl:text>
		<xsl:for-each select="input/param">
			<xsl:text>, </xsl:text>
			<!-- TODO: Decide what the class of the parameter is -->
			<xsl:text>String </xsl:text>
			<xsl:value-of select="@name" />
		</xsl:for-each>
		<xsl:text><![CDATA[)
   throws Throwable;
}
]]></xsl:text>
	</xsl:template>

</xsl:stylesheet>
