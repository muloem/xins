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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Central logging handler.
 */
public class Log extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The fully-qualified name for this class.
    */
   private static final String FQCN = "]]></xsl:text>
		<xsl:value-of select="$package_name" />
		<xsl:text><![CDATA[.Log";

   /**
    * The <em>debug</em> log level.
    */
   private static final Level DEBUG = Level.DEBUG;

   /**
    * The <em>info</em> log level.
    */
   private static final Level INFO = Level.INFO;

   /**
    * The <em>notice</em> log level.
    */
   private static final Level NOTICE = new NoticeLevel();

   /**
    * The <em>warning</em> log level.
    */
   private static final Level WARNING = Level.WARN;

   /**
    * The <em>error</em> log level.
    */
   private static final Level ERROR = Level.ERROR;

   /**
    * The <em>fatal</em> log level.
    */
   private static final Level FATAL = Level.FATAL;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------]]></xsl:text>

		<xsl:apply-templates select="entry" />

		<xsl:text><![CDATA[

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructor for this class. Intentionally made <code>private</code>,
    * since no instances of this class should be created. Instead, the class
    * functions should be used.
    */
   private Log() {
      // empty
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

   /**
    * The <em>notice</em> log level.
    */
   private static class NoticeLevel extends Level {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>NoticeLevel</code> object.
       */
      private NoticeLevel() {
         super((Level.INFO_INT + Level.WARN_INT) / 2, "NOTICE", 5);
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }
}
]]></xsl:text>
	</xsl:template>

	<xsl:template match="entry">
		<xsl:text>

   /**
    * Logs the entry with ID </xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>.
    */
   public static final log_</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>() {
      final Logger LOG = Logger.getLogger("</xsl:text>
		<xsl:value-of select="@category" />
		<xsl:text>");
      LOG.log(FQCN, Level.</xsl:text>
		<xsl:value-of select="@level" />
		<xsl:text>, "TODO: Actual message", null);
   }</xsl:text>
	</xsl:template>
</xsl:stylesheet>
