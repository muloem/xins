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

import java.util.HashMap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xins.util.MandatoryArgumentChecker;

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
   private static final Level DEBUG;

   /**
    * The <em>info</em> log level.
    */
   private static final Level INFO;

   /**
    * The <em>notice</em> log level.
    */
   private static final Level NOTICE;

   /**
    * The <em>warning</em> log level.
    */
   private static final Level WARNING;

   /**
    * The <em>error</em> log level.
    */
   private static final Level ERROR;

   /**
    * The <em>fatal</em> log level.
    */
   private static final Level FATAL;

   /**
    * Associations from name to translation bundle.
    */
   private static final HashMap TRANSLATION_BUNDLES_BY_NAME;

   /**
    * The active translation bundle.
    */
   private static TranslationBundle TRANSLATION_BUNDLE;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes this class.
    */
   static {

      // Determine the int value for the NOTICE level
      int noticeInt = (Level.INFO_INT + Level.WARN_INT) / 2;
      if (noticeInt <= Level.INFO_INT || noticeInt >= Level.WARN_INT) {
         throw new Error("Unable to determine int value for NOTICE level between INFO and WARN. Value for INFO level is " + Level.INFO_INT + ". Value for WARN level is " + Level.WARN_INT + '.');
      }

      // Initialize all the log levels
      DEBUG   = Level.DEBUG;
      INFO    = Level.INFO;
      NOTICE  = new CustomLevel(noticeInt, "NOTICE", 5);
      WARNING = Level.WARN;
      ERROR   = Level.ERROR;
      FATAL   = Level.FATAL;

      // Reference all translation bundles by name
      TRANSLATION_BUNDLES_BY_NAME = new HashMap();
      TRANSLATION_BUNDLES_BY_NAME.put("_raw", TranslationBundle.RAW);]]></xsl:text>
			<xsl:for-each select="messageset">
				<xsl:text>
      TRANSLATION_BUNDLES_BY_NAME.put("</xsl:text>
				<xsl:value-of select="@id" />
				<xsl:text>", TranslationBundle_</xsl:text>
				<xsl:value-of select="@id" />
				<xsl:text>.SINGLETON);</xsl:text>
			</xsl:for-each>
			<xsl:text><![CDATA[

      // Default translation bundle is _raw
      TRANSLATION_BUNDLE = TranslationBundle.RAW;
   }

   /**
    * Retrieves the active translation bundle.
    *
    * @return
    *    the translation bundle that is currently in use, never
    *    <code>null</code>.
    */
   public static final TranslationBundle getTranslationBundle() {
      return TRANSLATION_BUNDLE;
   }

   /**
    * Activates the specified translation bundle.
    *
    * @param name
    *    the name of the translation bundle to activate, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    *
    * @throws NoSuchTranslationBundleException
    *    if there is no translation bundle by that name.
    */
   public static final void setTranslationBundle(String name)
   throws IllegalArgumentException, NoSuchTranslationBundleException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      // Get the bundle by name
      TranslationBundle bundle = (TranslationBundle) TRANSLATION_BUNDLES_BY_NAME.get(name);

      // Make sure there is such a bundle
      if (bundle == null) {
         throw new NoSuchTranslationBundleException(name);
      }

      // Store the bundle
      TRANSLATION_BUNDLE = bundle;
   }]]></xsl:text>

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
   private static class CustomLevel extends Level {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>CustomLevel</code> object.
       *
       * @param value
       *    the <code>int</code> value for this level.
       *
       * @param name
       *    the name for this level, should not be <code>null</code>.
       *
       * @param syslogEquivalent
       *    the syslog equivalent.
       *
       * @throws IllegalArgumentException
       *    if <code>name == null</code>.
       */
      private CustomLevel(int value, String name, int syslogEquivalent)
      throws IllegalArgumentException {

         // Call superconstructor
         super(value, name, syslogEquivalent);

         // Check preconditions
         MandatoryArgumentChecker.check("name", name);
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
		<xsl:text><![CDATA[. The description for this log entry is:
    * <blockquote><em>]]></xsl:text>
		<xsl:apply-templates select="description" />
		<xsl:text><![CDATA[</em></blockquote>
    */
   public static final void log_]]></xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>(</xsl:text>
		<!-- TODO: Parameters -->
		<xsl:text>) {
      final Logger LOG = Logger.getLogger("</xsl:text>
		<xsl:value-of select="@category" />
		<xsl:text>.</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>");
      LOG.log(FQCN, </xsl:text>
		<xsl:value-of select="@level" />
		<xsl:text>, TRANSLATION_BUNDLE.translation_</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>(</xsl:text>
		<!-- TODO: Parameters -->
		<!-- TODO: Support exception? -->
		<xsl:text>), null);
   }</xsl:text>
	</xsl:template>
</xsl:stylesheet>
