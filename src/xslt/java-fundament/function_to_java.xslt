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

	<xsl:include href="../hungarian.xslt" />
	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />

	<xsl:variable name="version">
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision">
				<xsl:value-of select="//function/@rcsversion" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>

	<xsl:template match="function">
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text><![CDATA[;

import org.xins.server.CallContext;
import org.xins.server.Function;
import org.xins.server.Responder;

/**
 * Abstract base class for <code>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</code> function implementation.
 */
public abstract class ]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[ extends Function {

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
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   protected ]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>(APIImpl api) {
      super(api, "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>", "</xsl:text>
		<xsl:value-of select="$version" />
		<xsl:text>");</xsl:text>
		<xsl:for-each select="document($api_file)/api/impl-java/instance">
			<xsl:text>
      </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> = api.</xsl:text>
			<xsl:value-of select="@getter" />
			<xsl:text>();</xsl:text>
		</xsl:for-each>
		<xsl:text><![CDATA[
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
]]></xsl:text>
		<xsl:for-each select="document($api_file)/api/impl-java/instance">
			<xsl:text>
   protected final </xsl:text>
			<xsl:value-of select="@class" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;

</xsl:text>
		</xsl:for-each>
		<xsl:text><![CDATA[
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected final void handleCall(CallContext context)
   throws Throwable {
      boolean debugEnabled = context.isDebugEnabled();]]></xsl:text>
		<xsl:if test="input/param">
			<xsl:text>

      // Get the input parameters</xsl:text>
		</xsl:if>

		<xsl:for-each select="input/param">
			<xsl:if test="@name = 'context'">
				<xsl:message terminate="yes">Name 'context' is reserved. It cannot be used as a parameter name.</xsl:message>
			</xsl:if>
			<xsl:text>
      String </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> = context.getParameter("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>");</xsl:text>
		</xsl:for-each>
		<xsl:if test="input/param[@required='true']">
			<xsl:text>

      // Check that required parameters are indeed given
      if (</xsl:text>
			<xsl:for-each select="input/param[@required='true']">
				<xsl:if test="not(position() = 1)"> || </xsl:if>
				<xsl:text>isMissing(</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>)</xsl:text>
			</xsl:for-each>
			<xsl:text>) {
         context.startResponse(MISSING_PARAMETERS);
         if (debugEnabled) {</xsl:text>
			<xsl:for-each select="input/param[@required='true']">
				<xsl:text>
            if (isMissing(</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>)) {
               context.debug("Missing parameter \"</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>\".");
            }</xsl:text>
			</xsl:for-each>
			<xsl:text>
         }</xsl:text>
			<xsl:if test="input/param-combo[@type='inclusive-or']">
				<xsl:text>

      // Check inclusive-or parameter combinations</xsl:text>
			</xsl:if>
			<xsl:for-each select="input/param-combo[@type='inclusive-or']">
				<xsl:text>
      } else if (</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1"> &amp;&amp; </xsl:if>
					<xsl:text>isMissing(</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>)</xsl:text>
				</xsl:for-each>
				<xsl:text>) {
         context.startResponse(INVALID_PARAMETERS);
         if (debugEnabled) {
            context.debug("Either </xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1"> or </xsl:if>
					<xsl:text>\"</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>\"</xsl:text>
				</xsl:for-each>
				<xsl:text> should be set.");
         }</xsl:text>
			</xsl:for-each>
			<xsl:if test="input/param-combo[@type='exclusive-or']">
				<xsl:text>

      // Check exclusive-or parameter combinations</xsl:text>
			</xsl:if>
			<xsl:for-each select="input/param-combo[@type='exclusive-or']">
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
         context.startResponse(INVALID_PARAMETERS);
         if (debugEnabled) {
            context.debug("Exactly one of </xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1"> and </xsl:if>
					<xsl:text>\"</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>\"</xsl:text>
				</xsl:for-each>
				<xsl:text> must be set.");
         }</xsl:text>
				</xsl:for-each>
			</xsl:for-each>
			<xsl:if test="input/param-combo[@type='exclusive-or']">
				<xsl:text>

      // Check all-or-none parameter combinations</xsl:text>
			</xsl:if>
			<xsl:for-each select="input/param-combo[@type='all-or-none']">
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
         context.startResponse(INVALID_PARAMETERS);
         if (debugEnabled) {
            context.debug("Either </xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1"> and </xsl:if>
					<xsl:text>\"</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>\"</xsl:text>
				</xsl:for-each>
				<xsl:text> should all be set, or none these should be set.");
         }</xsl:text>

			</xsl:for-each>
			<xsl:if test="input/param[not(@type='text' or string-length(@type) = 0)]">
				<xsl:text>

      // Check values are valid for the associated types</xsl:text>
			</xsl:if>
			<xsl:for-each select="input/param[not(@type='text' or string-length(@type) = 0)]">
				<xsl:text>
      } else if (!</xsl:text>
				<xsl:call-template name="hungarianUpper">
					<xsl:with-param name="text">
						<xsl:value-of select="@type" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:text>.SINGLETON.isValidValue(</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>)) {
         context.startResponse(INVALID_PARAMETERS);</xsl:text>
			</xsl:for-each>
			<xsl:text>

      // Otherwise everything is okay, let the subclass do the thing
      } else {
         call(context</xsl:text>
		</xsl:if>
		<xsl:if test="not(input/param[@required='true'])">
			<xsl:text>
      // Nothing to check, just let the subclass do the thing
      call(context</xsl:text>
		</xsl:if>
		<xsl:for-each select="input/param">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="@name" />
		</xsl:for-each>
		<xsl:text>);</xsl:text>
		<xsl:if test="input/param[@required='true']">
			<xsl:text>
      }</xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[
   }

   /**
    * Calls this function.
    *
    * @param responder
    *    the responder to be used, never <code>null</code>.]]></xsl:text>
		<xsl:for-each select="input/param">
			<xsl:text><![CDATA[
    *
    * @param ]]></xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text><![CDATA[
    *    ]]></xsl:text>
			<!-- TODO: Use an intelligent template -->
			<xsl:value-of select="description/text()" />
		</xsl:for-each>
		<xsl:text><![CDATA[
    */
   public abstract void call(Responder responder]]></xsl:text>
		<xsl:for-each select="input/param">
			<xsl:text>, </xsl:text>
			<!-- TODO: Decide what the class of the parameter is -->
			<xsl:text>String </xsl:text>
			<xsl:value-of select="@name" />
		</xsl:for-each>
		<xsl:text><![CDATA[)
   throws Throwable;
}
]]></xsl:text>
	</xsl:template>

</xsl:stylesheet>
