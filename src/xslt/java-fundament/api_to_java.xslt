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
   public static final APIImpl SINGLETON = new APIImpl();


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>APIImpl</code> instance.
    */
   private APIImpl() {
]]></xsl:text>
		<xsl:for-each select="instance">
			<xsl:text>      </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> = new </xsl:text>
			<xsl:value-of select="@class" />
			<xsl:text>()</xsl:text>
			<xsl:text>;&#10;</xsl:text>
		</xsl:for-each>
		<xsl:for-each select="property">
			<xsl:text>      _</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> = </xsl:text>
			<xsl:if test="@class = 'java.lang.String'">"</xsl:if>
			<xsl:value-of select="text()" />
			<xsl:if test="@class = 'java.lang.String'">"</xsl:if>
			<xsl:text>;&#10;</xsl:text>
		</xsl:for-each>
		<xsl:for-each select="//api/function">
			<xsl:text>      _function</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> = new </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>Impl(this);&#10;</xsl:text>
		</xsl:for-each>
		<xsl:text>   }
		

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

</xsl:text>
		<xsl:for-each select="instance">
			<xsl:text>   private final </xsl:text>
			<xsl:value-of select="@class" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;&#10;</xsl:text>
		</xsl:for-each>
		<xsl:for-each select="property">
			<xsl:text>   private final </xsl:text>
			<xsl:value-of select="@class" />
			<xsl:text> _</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;&#10;</xsl:text>
		</xsl:for-each>
		<xsl:for-each select="//api/function">
			<xsl:text>   private final </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> _function</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;&#10;</xsl:text>
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
      </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>.init(properties);</xsl:text>
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
