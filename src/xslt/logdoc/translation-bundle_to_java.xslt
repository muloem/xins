<?xml version="1.0" encoding="US-ASCII" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
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

	<!-- Match the root element 'translation-bundle' -->
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
			<xsl:variable name="exception" select="document($log_file)/log/group/entry[@id = $entry]/@exception" />

			<xsl:text>

   public String translation_</xsl:text>
			<xsl:value-of select="$entry" />
			<xsl:text>(</xsl:text>
			<xsl:if test="$exception = 'true'">
				<xsl:text>java.lang.Throwable __exception__</xsl:text>
			</xsl:if>
			<xsl:apply-templates select="document($log_file)/log/group/entry[@id = $entry]/param" mode="method-argument">
				<xsl:with-param name="exception" select="$exception" />
			</xsl:apply-templates>
			<xsl:text>) {
      LogdocStringBuffer buffer = new LogdocStringBuffer(255);</xsl:text>
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

	<!-- Match <exception-property/> elements -->
	<xsl:template match="translation/exception-property">
		<xsl:variable name="entry" select="../@entry" />
		<xsl:variable name="exception" select="document($log_file)/log/group/entry[@id = $entry]/@exception" />

		<xsl:if test="not ($exception = 'true')">
			<xsl:message terminate="yes">
				<xsl:text>Translation for entry </xsl:text>
				<xsl:value-of select="$entry" />
				<xsl:text> contains an &lt;exception-property/&gt; element although the log entry does not declare an exception.</xsl:text>
			</xsl:message>
		</xsl:if>

		<xsl:choose>
			<xsl:when test="@name = 'class'">
				<xsl:text>
         buffer.append(__exception__.getClass().getName());</xsl:text>
			</xsl:when>
			<xsl:when test="@name = 'message'">
				<xsl:text>
         if (__exception__.getMessage() == null) {
            buffer.append("(null)");
         } else {</xsl:text>
				<xsl:choose>
					<xsl:when test="@format = 'quoted'">
						<xsl:text>
            buffer.append('"');
            buffer.append(__exception__.getMessage());
            buffer.append('"');
         }</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>
            buffer.append(__exception__.getMessage());</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>Invalid &lt;exception-property/&gt; element. There is no exception property named "</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>".</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Match <value-of-param/> elements -->
	<xsl:template match="translation/value-of-param">
		<xsl:variable name="entry" select="../@entry" />
		<xsl:variable name="param-name" select="@name" />
		<xsl:variable name="param-type" select="document($log_file)/log/group/entry[@id = $entry]/param[@name=$param-name]/@type" />
		<xsl:variable name="param-nullable" select="document($log_file)/log/group/entry[@id = $entry]/param[@name=$param-name]/@nullable" />

		<xsl:text>
      </xsl:text>
		<xsl:if test="not($param-nullable = 'false')">
			<xsl:text>if (</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> == null) {
         buffer.append("(null)");
      } else {
         </xsl:text>
		</xsl:if>
		<xsl:if test="@format = 'quoted'">
			<xsl:text>buffer.append('"');
         </xsl:text>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="$param-type = 'serializable'">
				<xsl:value-of select="@name" />
				<xsl:text>.serialize(buffer);</xsl:text>
			</xsl:when>
			<xsl:when test="$param-type = 'object'">
				<xsl:text>buffer.append(</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>.toString()</xsl:text>
				<xsl:text>);</xsl:text>
			</xsl:when>
			<xsl:when test="@format = 'hex' and (not($param-nullable = 'false') or (not($param-type = 'int32') and not($param-type = 'int64')))">
				<xsl:message terminate="yes">
					<xsl:text>The parameter "</xsl:text>
					<xsl:value-of select="@name"/>
					<xsl:text>" can be formatted as hex because its type is not int32 or int64 or its value is nullable.</xsl:text>
				</xsl:message>
			</xsl:when>
			<xsl:when test="@format = 'hex' and $param-nullable = 'false' and ($param-type = 'int32' or $param-type = 'int64')">
				<xsl:text>org.xins.logdoc.LogdocHexConverter.toHexString(buffer, </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>);</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>buffer.append(</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:if test="not($param-nullable = 'false') and string-length($param-type) &gt; 0 and not($param-type = 'text')">
					<xsl:text>.toString()</xsl:text>
				</xsl:if>
				<xsl:text>);</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="@format = 'quoted'">
			<xsl:text>
         buffer.append('"');</xsl:text>
		</xsl:if>
		<xsl:if test="not($param-nullable = 'false')">
			<xsl:text>
      }</xsl:text>
		</xsl:if>
	</xsl:template>

	<!-- Match character data -->
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
				<xsl:variable name="normalized-text">
					<xsl:call-template name="normalize">
						<xsl:with-param name="text" select="." />
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="xml_to_java_string">
					<xsl:with-param name="text" select="$normalized-text" />
				</xsl:call-template>
				<xsl:text>");</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
