<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../escapepattern.xslt" />

	<xsl:variable name="type" select="//type/@name" />
	<xsl:variable name="classname">
		<xsl:call-template name="hungarianUpper">
			<xsl:with-param name="text">
				<xsl:value-of select="$type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="superclass">
		<xsl:choose>
			<xsl:when test="type/enum">EnumType</xsl:when>
			<xsl:when test="type/pattern">PatternType</xsl:when>
			<xsl:otherwise>Type</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:include href="../hungarian.xslt" />
	<xsl:include href="../java.xslt"      />

	<xsl:template match="type">
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

import org.xins.types.*;

/**
 * Enumeration type <em>]]></xsl:text>
		<xsl:value-of select="$type" />
		<xsl:text><![CDATA[</em>.
 */
public final class ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text> extends </xsl:text>
		<xsl:value-of select="$superclass" />
		<xsl:text><![CDATA[ {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text> SINGLETON = new </xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text><![CDATA[();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text><![CDATA[</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text>()</xsl:text>
		<xsl:text> {
      super("</xsl:text>
		<xsl:value-of select="$type" />
		<xsl:text>", </xsl:text>
		<xsl:choose>
			<xsl:when test="enum">
				<xsl:text>new EnumItem[] {</xsl:text>
				<xsl:for-each select="enum/item">
					<xsl:if test="position() &gt; 1">,</xsl:if>
					<xsl:text>
         new EnumItem("</xsl:text>
					<xsl:choose>
						<xsl:when test="@name">
							<xsl:value-of select="@name" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@value" />
						</xsl:otherwise>
					</xsl:choose>
					<xsl:text>", "</xsl:text>
					<xsl:value-of select="@value" />
					<xsl:text>")</xsl:text>
				</xsl:for-each>
				<xsl:text>}</xsl:text>
			</xsl:when>
			<xsl:when test="pattern">
				<xsl:text>"</xsl:text>
				<xsl:call-template name="escapepattern">
					<xsl:with-param name="text">
						<xsl:value-of select="pattern/text()" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:text>"</xsl:text>
			</xsl:when>
			<xsl:otherwise>String.class</xsl:otherwise>
		</xsl:choose>
		<xsl:text>);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
</xsl:text>
		<xsl:if test="not(pattern or enum)">
			<xsl:text>
   public Object fromStringImpl(String string) {
      return string;
   }</xsl:text>
		</xsl:if>
		<xsl:text>
}</xsl:text>
	</xsl:template>
</xsl:stylesheet>
