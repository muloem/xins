<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="xins_home"    />
	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="enable_statistics">true</xsl:param>

	<xsl:variable name="api" select="//api/@name" />

	<xsl:output method="text" />

	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../hungarian.xslt" />
	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />

	<xsl:template match="api">
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;</xsl:text>
		<xsl:text><![CDATA[

import java.io.IOException;
import org.xins.client.CallResult;
import org.xins.client.CallResultParser;
import org.xins.client.InvalidCallResultException;
import org.xins.client.RemoteAPI;

/**
 * Stub for <code>]]></xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text><![CDATA[</code> API.
 */
public class API extends Object {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}]]></xsl:text>
	</xsl:template>
</xsl:stylesheet>
