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
	<xsl:param name="log_file"     />

	<xsl:include href="../xml_to_java.xslt" />

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
import org.xins.util.text.FastStringBuffer;

/**
 * Translation bundle for the <em>]]></xsl:text>

		<xsl:value-of select="$locale" />

		<xsl:text><![CDATA[</em> locale.
 *
 * @see Log
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
   //-------------------------------------------------------------------------</xsl:text>

		<xsl:for-each select="message">
			<xsl:variable name="entry" select="@entry" />

			<xsl:text>

   public String translation_</xsl:text>
			<xsl:value-of select="$entry" />
			<xsl:text>(</xsl:text>
			<xsl:for-each select="document($log_file)/log/entry[@id = $entry]/param">
				<xsl:if test="position() &gt; 1">, </xsl:if>
				<xsl:text>String </xsl:text>
				<xsl:value-of select="@name" />
			</xsl:for-each>
			<xsl:text>) {</xsl:text>
			<xsl:choose>
				<xsl:when test="count(value-of-param) &lt; 1">
					<xsl:text>
      return "</xsl:text>
					<xsl:call-template name="xml_to_java_string">
						<xsl:with-param name="text" select="text()" />
					</xsl:call-template>
					<xsl:text>";</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>
      FastStringBuffer buffer = new FastStringBuffer(205);</xsl:text>
					<xsl:apply-templates />
					<xsl:text>
      return buffer.toString();</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>
   }</xsl:text>
		</xsl:for-each>

		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template match="message/value-of-param">
		<xsl:text>
      buffer.append(</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>);</xsl:text>
	</xsl:template>

	<xsl:template match="message/value-of-param[@format='quoted']">
		<xsl:text>
      if (</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text> == null) {
         buffer.append("(null)");
      } else {
         buffer.append('"');
         buffer.append(</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>);
         buffer.append('"');
      }</xsl:text>
	</xsl:template>

	<xsl:template match="message/text()">
		<xsl:choose>
			<xsl:when test="string-length(.) &lt; 1"></xsl:when>
			<xsl:when test="string-length(.) = 1">
				<xsl:text>
      buffer.append('</xsl:text>
				<xsl:call-template name="xml_to_java_string"> <!-- TODO: xml_to_java_char -->
					<xsl:with-param name="text" select="." />
				</xsl:call-template>
				<xsl:text>');</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>
      buffer.append("</xsl:text>
				<xsl:call-template name="xml_to_java_string">
					<xsl:with-param name="text" select="." />
				</xsl:call-template>
				<xsl:text>");</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
