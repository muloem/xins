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
 * Exception thrown if a specified translation bundle does not exist.
 */
public final class NoSuchTranslationBundleException extends Exception {

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
    * Constructor a new <code>NoSuchTranslationBundleException</code>.
    *
    * @param name
    *    the name that does not identify an existing translation bundle,
    *    cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public NoSuchTranslationBundleException(String name)
   throws IllegalArgumentException {

      // Call superconstructor first
      super("Translation bundle \"" + name + "\" does not exist.");

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      // XXX: Store the name ?
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------
}
]]></xsl:text>
	</xsl:template>
</xsl:stylesheet>
