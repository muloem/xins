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

	<xsl:variable name="api" select="//api/@name" />

	<xsl:output method="text" />

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

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>APIImpl</code> instance.
    */
   public APIImpl() {
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

   protected void handleCall(CallContext context)
   throws Throwable {
      String function = context.getParameter("function");
</xsl:text>
		<!-- TODO: Default functions -->
		<xsl:text>
      if (function == null || function.length() == 0) {
         context.startResponse(false, "MissingFunctionName");
</xsl:text>
		<xsl:for-each select="//api/function">
			<xsl:variable name="function_file" select="concat($specsdir, '/', $api, '/', @name, '.fnc')" />

			<xsl:text>      } else if ("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>".equals(function)) {&#10;</xsl:text>
			<xsl:for-each select="document($function_file)/function/input/param">
				<xsl:text>         String </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text> = context.getParameter("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>");&#10;</xsl:text>
			</xsl:for-each>
			<xsl:if test="document($function_file)/function/input/param[@required='true']">
				<xsl:text>&#10;         if (</xsl:text>
				<xsl:for-each select="document($function_file)/function/input/param[@required='true']">
					<xsl:if test="not(position() = 1)">
						<xsl:text> || </xsl:text>
					</xsl:if>
					<xsl:text>isMissing(</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>)</xsl:text>
				</xsl:for-each>
				<xsl:text>) {
            context.startResponse(false, MISSING_PARAMETERS);</xsl:text>
				<xsl:for-each select="document($function_file)/function/input/param-combo[@type='inclusive-or']">
					<xsl:text>
         } else if (</xsl:text>
					<xsl:for-each select="param-ref">
						<xsl:if test="position() &gt; 1"> &amp;&amp; </xsl:if>
						<xsl:text>isMissing(</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>)</xsl:text>
					</xsl:for-each>
					<xsl:text>) {
            context.startResponse(false, INVALID_PARAMETERS);</xsl:text>
				</xsl:for-each>
				<xsl:for-each select="document($function_file)/function/input/param-combo[@type='exclusive-or']">
					<xsl:for-each select="param-ref">
						<xsl:variable name="active" select="@name" />
						<xsl:text>
         } else if (!isMissing(</xsl:text>
						<xsl:value-of select="$active" />
						<xsl:text>) &amp;&amp; (</xsl:text>
						<xsl:for-each select="../param-ref[not(@name = $active)]">
							<xsl:if test="position() &gt; 1"> || </xsl:if>
							<xsl:text>!isMissing(</xsl:text>
							<xsl:value-of select="@name" />
							<xsl:text>)</xsl:text>
						</xsl:for-each>
						<xsl:text>)) {
            context.startResponse(false, INVALID_PARAMETERS);</xsl:text>
					</xsl:for-each>
				</xsl:for-each>
				<xsl:for-each select="document($function_file)/function/input/param-combo[@type='all-or-none']">
					<xsl:text>
         } else if (!(</xsl:text>
					<xsl:for-each select="param-ref">
						<xsl:if test="position() &gt; 1"> &amp;&amp; </xsl:if>
						<xsl:text>isMissing(</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>)</xsl:text>
					</xsl:for-each>
					<xsl:text>) &amp;&amp; (</xsl:text>
					<xsl:for-each select="param-ref">
						<xsl:if test="position() &gt; 1"> || </xsl:if>
						<xsl:text>isMissing(</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>)</xsl:text>
					</xsl:for-each>
					<xsl:text>)) {
            context.startResponse(false, INVALID_PARAMETERS);</xsl:text>
				</xsl:for-each>
				<xsl:text>
         } else {
   </xsl:text>
			</xsl:if>
			<xsl:text>         _function</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:if test="@name = 'context'">
				<xsl:message terminate="yes">
					<xsl:text>Name 'context' is reserved. It cannot be used as a parameter name.</xsl:text>
				</xsl:message>
			</xsl:if>
			<xsl:text>.call(context</xsl:text>
			<xsl:for-each select="document($function_file)/function/input/param">
				<xsl:text>, </xsl:text>
				<xsl:value-of select="@name" />
			</xsl:for-each>
			<xsl:text>);&#10;</xsl:text>
			<xsl:if test="document($function_file)/function/input/param[@required='true']">
				<xsl:text>         }&#10;</xsl:text>
			</xsl:if>
		</xsl:for-each>
		<xsl:text>      } else if ("_GetFunctionList".equals(function)) {
         context.startResponse(true, null);</xsl:text>
		<xsl:for-each select="//api/function">
			<xsl:variable name="function_file" select="concat($specsdir, '/', $api, '/', @name, '.fnc')" />
			<xsl:text>&#10;         context.startTag("function");</xsl:text>
			<xsl:text>&#10;         context.attribute("name", "</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>");</xsl:text>
			<xsl:text>&#10;         context.attribute("version", "</xsl:text>
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision">
					<xsl:value-of select="document($function_file)/function/@rcsversion" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text>");</xsl:text>
			<xsl:text>&#10;         context.endTag();</xsl:text>
		</xsl:for-each>
		<xsl:text>
      } else {
         context.startResponse(false, "NoSuchFunction");
      }
      context.endResponse();
   }
</xsl:text>
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
