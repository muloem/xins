<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="package_name" />
	<xsl:param name="accesslevel"  />

	<!-- Define variables -->
	<xsl:variable name="domain" select="/log/@domain" />

	<!-- Perform includes -->
	<xsl:include href="shared.xslt" />

	<!-- Set output method -->
	<xsl:output method="text" />

	<xsl:template match="log">

		<xsl:variable name="default_locale">
			<xsl:if test="string-length(@default-locale) &lt; 1">
				<xsl:message terminate="yes">No default locale has been set.</xsl:message>
			</xsl:if>
			<xsl:value-of select="@default-locale" />
		</xsl:variable>

		<xsl:variable name="accessmodifier">
			<xsl:choose>
				<xsl:when test="(string-length($accesslevel) = 0) or $accesslevel = 'package'" />
				<xsl:when test="$accesslevel = 'public'">public </xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="not(boolean(translation-bundle[@locale=$default_locale]))">
			<xsl:message terminate="yes">
				<xsl:text>The default locale "</xsl:text>
				<xsl:value-of select="$default_locale" />
				<xsl:text>" does not exist.</xsl:text>
			</xsl:message>
		</xsl:if>

		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package_name" />
		<xsl:text>;

import java.util.HashMap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xins.logdoc.AbstractLog;
import org.xins.logdoc.LogStatistics;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Central logging handler.
 */
</xsl:text>
		<xsl:value-of select="$accessmodifier" />
		<xsl:text>class Log extends AbstractLog {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The fully-qualified name for this class.
    */
   private static final String FQCN = "</xsl:text>
		<xsl:value-of select="$package_name" />
		<xsl:text><![CDATA[.Log";

   /**
    * Controller for this <em>logdoc</em> <code>Log</code> class.
    */
   private static final LogController CONTROLLER;

   /**
    * Associations from name to translation bundle.
    */
   private static final HashMap TRANSLATION_BUNDLES_BY_NAME;

   /**
    * The active translation bundle.
    */
   private static TranslationBundle TRANSLATION_BUNDLE;]]></xsl:text>

		<xsl:apply-templates select="group/entry" mode="counter" />

		<xsl:text><![CDATA[


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes this class.
    */
   static {

      // Create LogController instance
      CONTROLLER = new Controller();

      // Reference all translation bundles by name
      TRANSLATION_BUNDLES_BY_NAME = new HashMap();]]></xsl:text>
			<xsl:for-each select="translation-bundle">
				<xsl:text>
      TRANSLATION_BUNDLES_BY_NAME.put("</xsl:text>
				<xsl:value-of select="@locale" />
				<xsl:text>", TranslationBundle_</xsl:text>
				<xsl:value-of select="@locale" />
				<xsl:text>.SINGLETON);</xsl:text>
			</xsl:for-each>
			<xsl:text>

      // Initialize to the default translation bundle
      TRANSLATION_BUNDLE = TranslationBundle_</xsl:text>
			<xsl:value-of select="$default_locale" />
			<xsl:text><![CDATA[.SINGLETON;
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
    * Activates the default translation bundle.
    */
   public static final void resetTranslationBundle() {
      TRANSLATION_BUNDLE = TranslationBundle_]]></xsl:text>
		<xsl:value-of select="$default_locale" />
		<xsl:text><![CDATA[.SINGLETON;
   }

   /**
    * Returns the statistics for all log entries.
    *
    * @return
    *    the statistics, never <code>null</code>.
    */
   public static LogStatistics getStatistics() {
      return new LogStatistics(new LogStatistics.Entry[] {]]></xsl:text>
		<xsl:apply-templates select="group/entry" mode="log_entry" />
		<xsl:text>
      });
   }</xsl:text>

		<xsl:apply-templates select="group/entry" />

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
    * Controller for this <code>Log</code> class.
    */
   private static final class Controller extends LogController {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Controller</code>.
       */
      private Controller() {
         // empty
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      protected boolean isLocaleSupported(String locale) {

         // Fetch the translation bundle
         TranslationBundle bundle = (TranslationBundle) TRANSLATION_BUNDLES_BY_NAME.get(locale);

         // Return true if the bundle exists
         return (bundle != null);
      }

      protected void setLocale(String newLocale) {
         TRANSLATION_BUNDLE = (TranslationBundle) TRANSLATION_BUNDLES_BY_NAME.get(newLocale);
      }
   }
}
]]></xsl:text>
	</xsl:template>

	<xsl:template match="group/entry">
		<xsl:variable name="category">
			<xsl:value-of select="$domain" />
			<xsl:text>.</xsl:text>
			<xsl:value-of select="../@id" />
		</xsl:variable>

		<xsl:variable name="exception">
			<xsl:choose>
				<xsl:when test="@exception = 'true'">true</xsl:when>
				<xsl:when test="@exception = 'false'">false</xsl:when>
				<xsl:when test="string-length(@exception) = 0">false</xsl:when>
				<xsl:otherwise>
					<xsl:message terminate="yes">
						<xsl:text>Element 'entry', parameter 'exception' is set to '</xsl:text>
						<xsl:value-of select="@exception" />
						<xsl:text>', which is considered invalid. It should be either 'true', 'false' or empty.</xsl:text>
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>

   /**
    * Logs the entry with ID </xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text><![CDATA[, in the log entry group <em>]]></xsl:text>
		<xsl:value-of select="../@name" />
		<xsl:text><![CDATA[</em>.
    * The description for this log entry is:
    * <blockquote><em>]]></xsl:text>
		<xsl:apply-templates select="description" />
		<xsl:text><![CDATA[</em></blockquote>
    */
   public static final void log_]]></xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>(</xsl:text>
		<xsl:if test="$exception = 'true'">
			<xsl:text>Throwable exception</xsl:text>
			<xsl:if test="count(param) &gt; 0">
				<xsl:text>, </xsl:text>
			</xsl:if>
		</xsl:if>
		<xsl:apply-templates select="param" mode="method-argument" />
		<!-- TODO: Performance improvement: Do not synchronize on
		     TODO: COUNT_xxx_LOCK if the value is not needed, since the log
		     TODO: level is not enabled. -->
		<xsl:text>) {
      int id;
      synchronized (COUNT_</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>_LOCK) {
         id = COUNT_</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>++;
      }
      final Logger LOG = Logger.getLogger("</xsl:text>
		<xsl:value-of select="$category" />
		<xsl:text>.</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>");
      if (LOG.isEnabledFor(</xsl:text>
		<xsl:value-of select="@level" />
		<xsl:text>)) {
         LOG.log(FQCN, </xsl:text>
		<xsl:value-of select="@level" />
		<xsl:text>, TRANSLATION_BUNDLE.translation_</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>(id</xsl:text>
		<xsl:for-each select="param">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="@name" />
		</xsl:for-each>
		<xsl:text>), </xsl:text>
		<xsl:choose>
			<xsl:when test="$exception = 'true' and @level = 'DEBUG'">
				<xsl:text>exception</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>null</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>);
      }</xsl:text>
		<xsl:if test="$exception = 'true' and not(@level = 'DEBUG')">
			<xsl:text>
      if (LOG.isEnabledFor(DEBUG)) {
         LOG.log(FQCN, DEBUG, null, exception);
      }</xsl:text>
		</xsl:if>
		<xsl:text>
   }</xsl:text>
	</xsl:template>

	<xsl:template match="group/entry" mode="counter">
		<xsl:text>

   /**
    * Counter for log entry </xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>.
    */
   private static int COUNT_</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>;

   /**
    * Lock object for the counter for log entry </xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>.
    */
   private static Object COUNT_</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>_LOCK = new Object();</xsl:text>
	</xsl:template>

	<xsl:template match="group/entry" mode="log_entry">
		<xsl:if test="not (position() = 1)">,</xsl:if>
		<xsl:text>
         new LogStatistics.Entry("</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>", COUNT_</xsl:text>
		<xsl:value-of select="@id" />
		<xsl:text>)</xsl:text>
	</xsl:template>
</xsl:stylesheet>
