<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$

 Copyright 2004 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="package_name" />
	<xsl:param name="accesslevel"  />

	<xsl:include href="shared.xslt" />
	<xsl:include href="../xml_to_java.xslt" />

	<!-- Set output method -->
	<xsl:output method="text" />

	<xsl:template match="log">
		<xsl:variable name="accessmodifier">
			<xsl:choose>
				<xsl:when test="(string-length($accesslevel) = 0) or $accesslevel = 'package'" />
				<xsl:when test="$accesslevel = 'public'">public </xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package_name" />
		<xsl:text><![CDATA[;

/**
 * Translation bundle for log messages.
 *
 * @see Log
 */
]]></xsl:text>
		<xsl:value-of select="$accessmodifier" />
		<xsl:text>abstract class TranslationBundle extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------</xsl:text>

		<xsl:text><![CDATA[


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>TranslationBundle</code> subclass instance.
    *
    * @param name
    *    the name of this translation bundle, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   protected TranslationBundle(String name)
   throws IllegalArgumentException {

      // Check preconditions
			if (name == null) {
			   throw new IllegalArgumentException("name == null");
			}

      // TODO: Check the name

      // Store information
      _name = name;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of this translation bundle.
    */
   private final String _name;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Retrieves the name of this translation bundle.
    *
    * @return
    *    the name of this translation bundle.
    */
   public final String getName() {
      return _name;
   }]]></xsl:text>

		<xsl:apply-templates select="group/entry" />

		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template match="group/entry">
		<xsl:variable name="description" select="description/text()" />
		<xsl:variable name="exception">
			<xsl:choose>
				<xsl:when test="@exception = 'true'">true</xsl:when>
				<xsl:when test="@exception = 'false'">false</xsl:when>
				<xsl:when test="string-length(@exception) = 0">false</xsl:when>
				<xsl:otherwise>
					<xsl:message terminate="yes">
						<xsl:text>The attribute "exception" is set to "</xsl:text>
						<xsl:value-of select="@exception" />
						<xsl:text>" instead of either "true" or "false".</xsl:text>
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>

   /**
    * Get the translation for the log entry with ID </xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text><![CDATA[, in the log entry group <em>]]></xsl:text>
		<xsl:value-of select="../@name" />
		<xsl:text><![CDATA[</em>.
    * The description for this log entry is:
    * <blockquote><em>]]></xsl:text>
		<xsl:apply-templates select="description" />
		<xsl:text><![CDATA[</em></blockquote>
    */
   public String translation_]]></xsl:text>
		<!-- TODO: Generate @param tags for parameters -->
		<xsl:value-of select="@id" />
		<xsl:text>(</xsl:text>
		<xsl:if test="$exception = 'true'">
			<xsl:text>java.lang.Throwable __exception__</xsl:text>
		</xsl:if>
		<xsl:apply-templates select="param" mode="method-argument">
			<xsl:with-param name="exception" select="$exception" />
		</xsl:apply-templates>
		<xsl:text>) {
      return "</xsl:text>
		<xsl:call-template name="xml_to_java_string">
			<xsl:with-param name="text" select="$description" />
		</xsl:call-template>
		<xsl:text>";
   }</xsl:text>
	</xsl:template>
</xsl:stylesheet>
