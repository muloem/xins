<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="package_name" />
	<xsl:param name="locale"       />

	<!-- Set output method -->
	<xsl:output method="text" />

	<xsl:template match="messages">
		<xsl:variable name="classname">
			<xsl:text>TranslationBundle_</xsl:text>
			<xsl:value-of select="$locale" />
		</xsl:variable>

		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package_name" />
		<xsl:text><![CDATA[;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Translation bundle for the <em>]]></xsl:text>

		<xsl:value-of select="$locale" />

		<xsl:text><![CDATA[</em> locale.
 */
public final class ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text><![CDATA[ extends TranslationBundle {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * The one and only instance of this class.
    */
   public static final ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text> SINGLETON = new </xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text><![CDATA[();

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructor for this class. Intentionally made <code>private</code>,
    * since no instances of this class should be created. Instead, the class
    * functions should be used.
    */
   private ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text>() {
      super("</xsl:text>
		<xsl:value-of select="$locale" />
		<xsl:text>");
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
</xsl:text>
	</xsl:template>
</xsl:stylesheet>
