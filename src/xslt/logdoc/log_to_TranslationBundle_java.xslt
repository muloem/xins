<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="package_name"    />

	<!-- Set output method -->
	<xsl:output method="text" />

	<xsl:template match="log">
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package_name" />
		<xsl:text><![CDATA[;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Translation bundle for log messages.
 *
 * @see Log
 */
public class TranslationBundle extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The translation bundle with <em>raw</em> messages. The name of this
    * bundle is <code>"_raw"</code>.
    */
   public static final TranslationBundle RAW = new TranslationBundle();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------]]></xsl:text>

		<xsl:apply-templates select="messageset" />

		<xsl:text><![CDATA[


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>TranslationBundle</code> instance, not a subclass.
    */
   private TranslationBundle() {
      _name = "_raw";
   }

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
      MandatoryArgumentChecker.check("name", name);

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
   //-------------------------------------------------------------------------]]></xsl:text>

		<xsl:apply-templates select="entry" />

		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template match="entry">
		<xsl:text>

   /**
    * Get the translation for the log entry with ID </xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text><![CDATA[.
    * The description for this log entry is:
    * <blockquote><em>]]></xsl:text>
		<xsl:apply-templates select="description" />
		<xsl:text><![CDATA[</em></blockquote>
    */
   public String translation_]]></xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>(</xsl:text>
		<xsl:for-each select="param">
			<xsl:if test="position() &gt; 1">, </xsl:if>
			<xsl:text>String </xsl:text>
			<xsl:value-of select="@name" />
		</xsl:for-each>
		<xsl:text>) {
      return null; // TODO
   }</xsl:text>
	</xsl:template>
</xsl:stylesheet>
