<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="xins_home"    />
	<xsl:param name="project_home" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="enable_statistics">true</xsl:param>

	<xsl:variable name="api" select="//api/@name" />

	<xsl:output method="text" />

	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../hungarian.xslt" />
	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />

	<xsl:template match="api">
		<xsl:apply-templates select="impl-java" />
	</xsl:template>

	<xsl:template match="impl-java">

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;</xsl:text>
		<xsl:text><![CDATA[

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.xins.server.API;
import org.xins.server.CallContext;
import org.xins.server.ResultCode;

/**
 * Implementation of <code>]]></xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text><![CDATA[</code> API.
 */
public class APIImpl extends API {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final APIImpl SINGLETON = new APIImpl();]]></xsl:text>
		<xsl:for-each select="//api/resultcode">
			<xsl:variable name="name"    select="@name" />
			<xsl:variable name="file"    select="concat($specsdir, '/', $api, '/', $name, '.rcd')" />
			<xsl:variable name="successAttr" select="document($file)/resultcode/@success" />
			<xsl:variable name="success">
				<xsl:choose>
					<xsl:when test="string-length($successAttr) = 0">false</xsl:when>
					<xsl:when test="$successAttr = 'false'">false</xsl:when>
					<xsl:when test="$successAttr = 'true'">true</xsl:when>
					<xsl:otherwise>
						<xsl:message terminate="yes">
							<xsl:text>Invalid value for success attribute: "</xsl:text>
							<xsl:value-of select="$successAttr" />
							<xsl:text>".</xsl:text>
						</xsl:message>
					</xsl:otherwise>
				</xsl:choose>
			<xsl:variable name="value"   select="document($file)/resultcode/@value" />
			<xsl:variable name="fieldname">
				<xsl:call-template name="toupper">
					<xsl:with-param name="text">
						<xsl:call-template name="hungarianWordSplit">
							<xsl:with-param name="text" select="$name" />
							<xsl:with-param name="separator" select="'_'" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:variable>

			<xsl:text><![CDATA[

   /**
    * The <em>]]></xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text><![CDATA[</em> result code.
    */
   public final static ResultCode ]]></xsl:text>
			<xsl:value-of select="$fieldname" />
			<xsl:text> = new ResultCode(SINGLETON, </xsl:text>
			<xsl:value-of select="$success" />
			<xsl:text>, "</xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text>", "</xsl:text>
			<xsl:value-of select="$value" />
			<xsl:text>");</xsl:text>
		</xsl:for-each>

		<xsl:for-each select="//api/function">
			<xsl:variable name="name"    select="@name" />
			<xsl:variable name="file"    select="concat($specsdir, '/', $api, '/', $name, '.fnc')" />
			<xsl:variable name="fieldname">
				<xsl:call-template name="toupper">
					<xsl:with-param name="text">
						<xsl:call-template name="hungarianWordSplit">
							<xsl:with-param name="text" select="$name" />
							<xsl:with-param name="separator" select="'_'" />
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:variable>

			<xsl:text><![CDATA[

   /**
    * The <em>]]></xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text><![CDATA[</em> function.
    */
   public final static ]]></xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="$fieldname" />
			<xsl:text> = new </xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text>Impl(SINGLETON);</xsl:text>
		</xsl:for-each>

		<xsl:text><![CDATA[


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>APIImpl</code> instance.
    */
   private APIImpl() {]]></xsl:text>
		<xsl:choose>
			<xsl:when test="instance or property">
				<xsl:for-each select="instance">
					<xsl:text>
      </xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text> = new </xsl:text>
					<xsl:value-of select="@class" />
					<xsl:text>()</xsl:text>
					<xsl:text>;</xsl:text>
				</xsl:for-each>
				<xsl:for-each select="property">
					<xsl:text>
      _</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text> = </xsl:text>
					<xsl:if test="@class = 'java.lang.String'">"</xsl:if>
					<xsl:value-of select="text()" />
					<xsl:if test="@class = 'java.lang.String'">"</xsl:if>
					<xsl:text>;</xsl:text>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>
      // empty</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------</xsl:text>
		<xsl:for-each select="instance">
			<xsl:text>

   private final </xsl:text>
			<xsl:value-of select="@class" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;</xsl:text>
		</xsl:for-each>
		<xsl:for-each select="property">
			<xsl:text>

   private final </xsl:text>
			<xsl:value-of select="@class" />
			<xsl:text> _</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;</xsl:text>
		</xsl:for-each>
		<xsl:text>

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
</xsl:text>
	<xsl:if test="instance">
		<xsl:text>
   protected void initImpl(Properties properties)
   throws Throwable {</xsl:text>
		<xsl:for-each select="instance">
			<xsl:text>
      addInstance(</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>);</xsl:text>
		</xsl:for-each>
		<xsl:text>
   }
</xsl:text>
	</xsl:if>

		<xsl:for-each select="instance">
			<xsl:text>   public </xsl:text>
			<xsl:value-of select="@class" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@getter" />
			<xsl:text>() {&#10;      return </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;&#10;   }</xsl:text>
		</xsl:for-each>
		<xsl:for-each select="property">
			<xsl:text>   public </xsl:text>
			<xsl:value-of select="@class" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@getter" />
			<xsl:text>() {&#10;      return _</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;&#10;   }</xsl:text>
		</xsl:for-each>
		<xsl:text>&#10;}</xsl:text>
	</xsl:template>
</xsl:stylesheet>
