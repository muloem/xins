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

	<xsl:template match="returncodes">
		<xsl:call-template name="java-header" />
		<xsl:text>package org.xins.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Constants for the default return codes.
 *
 * @deprecated
 *    Deprecated since XINS 0.15.
 *    Use {@link DefaultResultCodes} instead.
 */
public interface DefaultReturnCodes {</xsl:text>
		<xsl:for-each select="code">
			<xsl:text><![CDATA[
   /**
    * Constant for the <em>]]></xsl:text>
			<xsl:value-of select="@value" />
			<xsl:text><![CDATA[</em> return code.
    * The description for this return code is:
    *
    * <blockquote>]]></xsl:text>
			<xsl:apply-templates select="description" />
			<xsl:text><![CDATA[</blockquote>
    */
   final String ]]></xsl:text>
			<xsl:call-template name="toupper">
				<xsl:with-param name="text">
					<xsl:value-of select="@name" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text> = "</xsl:text>
			<xsl:value-of select="@value" />
			<xsl:text>";
</xsl:text>
		</xsl:for-each>
		<xsl:text>

   /**
    * List containing the values of all defined return codes.
    */
   final List CODES = Collections.unmodifiableList(Arrays.asList(new String[]
{</xsl:text>
		<xsl:for-each select="code">
			<xsl:if test="position() &gt; 1">, </xsl:if>
			<xsl:text>"</xsl:text>
			<xsl:value-of select="@value" />
			<xsl:text>"</xsl:text>
		</xsl:for-each>
		<xsl:text>}));
}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
