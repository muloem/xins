<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generated the APIImpl.java class.

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="xins_home"    />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="api_file"     />
	<xsl:param name="package"      />
	<xsl:param name="enable_statistics">true</xsl:param>

	<!-- Determine name of API -->
	<xsl:variable name="api" select="//api/@name" />

	<!-- Perform includes -->
	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../hungarian.xslt" />
	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />

	<xsl:output method="text" />

	<xsl:template match="api">
		<xsl:apply-templates select="impl-java" />
		<xsl:if test="document($project_file)/project/api[@name = $api]/impl">
			<xsl:variable name="impl_file" select="concat($project_home, '/apis/', $api, '/impl/impl.xml')"/>
			<xsl:apply-templates select="document($impl_file)/impl" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="impl-java | impl">

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
		<xsl:for-each select="document($api_file)/api/resultcode">
			<xsl:variable name="name"    select="@name" />
			<xsl:variable name="file"    select="concat($specsdir, '/', $name, '.rcd')" />
			<xsl:variable name="value">
				<xsl:choose>
					<xsl:when test="document($file)/resultcode/@value">
						<xsl:value-of select="document($file)/resultcode/@value" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="document($file)/resultcode/@name" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
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
   public final static org.xins.server.ResultCode ]]></xsl:text>
			<xsl:value-of select="$fieldname" />
			<xsl:text> = new org.xins.server.ResultCode(SINGLETON, "</xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text>", "</xsl:text>
			<xsl:value-of select="$value" />
			<xsl:text>");</xsl:text>
		</xsl:for-each>

		<xsl:for-each select="document($api_file)/api/function">
			<xsl:variable name="name"    select="@name" />
			<xsl:variable name="file"    select="concat($specsdir, '/', $name, '.fnc')" />
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
   private APIImpl() {
      super("]]></xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text>");</xsl:text>
		<xsl:choose>
			<xsl:when test="instance">
				<xsl:for-each select="instance">
					<xsl:text>
      </xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text> = new </xsl:text>
					<xsl:value-of select="@class" />
					<xsl:text>(this);</xsl:text>
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
   protected void bootstrapImpl2(org.xins.common.collections.PropertyReader properties)
   throws org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException,
          org.xins.common.manageable.BootstrapException {</xsl:text>
		<xsl:for-each select="instance">
			<xsl:text>
      add(</xsl:text>
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
