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
	<xsl:param name="accesslevel"  />

	<!-- Perform includes -->
	<xsl:include href="shared.xslt"         />
	<xsl:include href="../xml_to_java.xslt" />

	<!-- Set output method -->
	<xsl:output method="text" />

	<xsl:template match="translation-bundle">
		<xsl:variable name="classname">
			<xsl:text>TranslationBundle_</xsl:text>
			<xsl:value-of select="$locale" />
		</xsl:variable>

		<xsl:variable name="accessmodifier">
			<xsl:choose>
				<xsl:when test="(string-length($accesslevel) = 0) or $accesslevel = 'package'" />
				<xsl:when test="$accesslevel = 'public'">public </xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package_name" />
		<xsl:text><![CDATA[;

import org.xins.logdoc.LogdocStringBuffer;

/**
 * Translation bundle for the <em>]]></xsl:text>

		<xsl:value-of select="$locale" />

		<xsl:text><![CDATA[</em> locale.
 *
 * @see Log
 */
]]></xsl:text>
		<xsl:value-of select="$accessmodifier" />
		<xsl:text>final class </xsl:text>
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

		<xsl:for-each select="translation">
			<xsl:variable name="entry" select="@entry" />

			<xsl:text>

   public String translation_</xsl:text>
			<xsl:value-of select="$entry" />
			<xsl:text>(int id</xsl:text>
			<xsl:apply-templates select="document($log_file)/log/group/entry[@id = $entry]/param" mode="method-argument">
				<xsl:with-param name="had-argument" select="'true'" />
			</xsl:apply-templates>
			<xsl:text>) {
      LogdocStringBuffer buffer = new LogdocStringBuffer(255);
      buffer.append(id);
      buffer.append(' ');</xsl:text>
			<xsl:apply-templates />
			<xsl:text>
      return buffer.toString();</xsl:text>
			<xsl:text>
   }</xsl:text>
		</xsl:for-each>

		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template match="translation/value-of-param">
		<xsl:variable name="entry" select="../@entry" />
		<xsl:variable name="param-name" select="@name" />
		<xsl:variable name="param-type" select="document($log_file)/log/group/entry[@id = $entry]/param[@name=$param-name]/@type" />
		<xsl:variable name="param-nullable" select="document($log_file)/log/group/entry[@id = $entry]/param[@name=$param-name]/@nullable" />

		<xsl:text>
      buffer.append(</xsl:text>
		<xsl:choose>
			<xsl:when test="($param-type = 'object') and ($param-nullable = 'false')">
				<xsl:value-of select="@name" />
				<xsl:text>.toString()</xsl:text>
			</xsl:when>
			<xsl:when test="$param-type = 'object'">
				<xsl:text>(</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>== null) ? "(null)" : </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>.toString()</xsl:text>
			</xsl:when>
			<xsl:when test="$param-nullable = 'false'">
				<xsl:value-of select="@name" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>(</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>== null) ? "(null)" : </xsl:text>
				<xsl:value-of select="@name" />
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>);</xsl:text>
	</xsl:template>

	<xsl:template match="translation/value-of-param[@format='quoted']">
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

	<xsl:template match="translation/value-of-param[@format='hex']">
		<xsl:text>
      org.xins.logdoc.LogdocHexConverter.toHexString(buffer, </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>);</xsl:text>
	</xsl:template>

	<xsl:template match="translation/text()">
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
