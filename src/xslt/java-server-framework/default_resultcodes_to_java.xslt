<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../java.xslt" />

	<xsl:template match="resultcodes">
		<xsl:call-template name="java-header" />
		<xsl:text>package org.xins.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Constants for the default result codes.
 */
public interface DefaultResultCodes {</xsl:text>
		<xsl:for-each select="code">
			<xsl:text><![CDATA[
   /**
    * Constant for the <em>]]></xsl:text>
			<xsl:value-of select="@value" />
			<xsl:text><![CDATA[</em> result code.
    * The description for this result code is:
    *
    * <blockquote>"]]></xsl:text>
			<xsl:apply-templates select="description" />
			<xsl:text><![CDATA["</blockquote>
    */
   final ResultCode ]]></xsl:text>
			<xsl:call-template name="toupper">
				<xsl:with-param name="text">
					<xsl:value-of select="@name" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text> = new ResultCode(</xsl:text>
			<!-- TODO: Make sure success is either 'true' or 'false' -->
			<xsl:value-of select="@success" />
			<xsl:text>, "</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>", "</xsl:text>
			<xsl:value-of select="@value" />
			<xsl:text>");
</xsl:text>
		</xsl:for-each>
		<xsl:text>
}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
