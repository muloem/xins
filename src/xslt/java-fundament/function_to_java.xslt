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

	<xsl:include href="../function.xslt" />
	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />
	<xsl:include href="../types.xslt"  />

	<xsl:variable name="project_file" select="concat($project_home, '/xins-project.xml')" />
	<xsl:variable name="version">
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision">
				<xsl:value-of select="//function/@rcsversion" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>

	<xsl:template match="function">
		<xsl:variable name="sessionBased">
			<xsl:call-template name="is_function_session_based" />
		</xsl:variable>

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text><![CDATA[;

import org.xins.server.CallContext;
import org.xins.server.Function;
import org.xins.server.Responder;
import org.xins.server.Session;

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
		<xsl:text>", </xsl:text>
		<xsl:value-of select="$sessionBased" />
		<xsl:text>);</xsl:text>
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
		<xsl:text>
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected final void handleCall(CallContext context)
   throws Throwable {
      boolean debugEnabled = context.isDebugEnabled();</xsl:text>


		<!-- ************************************************************* -->
		<!-- Retrieve session                                              -->
		<!-- ************************************************************* -->

		<xsl:if test="$sessionBased = 'true'">
			<xsl:text>

      // Get the session
      Session session = context.getSession();</xsl:text>
		</xsl:if>


		<!-- ************************************************************* -->
		<!-- Retrieve input parameters                                     -->
		<!-- ************************************************************* -->

		<xsl:if test="input/param">
			<xsl:text>

      // Get the input parameters</xsl:text>

			<xsl:for-each select="input/param">
				<xsl:text>
      String </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text> = context.getParameter("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>");</xsl:text>
			</xsl:for-each>
		</xsl:if>


		<!-- ************************************************************* -->
		<!-- Check required parameters                                     -->
		<!-- ************************************************************* -->

		<xsl:if test="input/param[@required='true']">
			<xsl:text>

      // Check that required parameters are indeed given
      if (</xsl:text>
			<xsl:for-each select="input/param[@required='true']">
				<xsl:if test="not(position() = 1)"> || </xsl:if>
				<xsl:value-of select="@name" />
				<xsl:text> == null</xsl:text>
			</xsl:for-each>
			<xsl:text>) {
         context.startResponse(MISSING_PARAMETERS);</xsl:text>
			<xsl:choose>
				<xsl:when test="count(input/param[@required='true']) &gt; 1">
					<xsl:for-each select="input/param[@required='true']">
						<xsl:text>
         if (</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text> == null) {
            context.startTag("missing-param");
            context.attribute("name", "</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>");
            context.endTag();
         }</xsl:text>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>
         context.startTag("missing-param");
         context.attribute("name", "</xsl:text>
					<xsl:value-of select="input/param[@required='true']/@name" />
						<xsl:text>");
         context.endTag();</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>
         return;
      }</xsl:text>
		</xsl:if>


		<!-- ************************************************************* -->
		<!-- Check 'inclusive-or' combos                                   -->
		<!-- ************************************************************* -->

		<xsl:if test="input/param-combo[@type='inclusive-or']">
			<xsl:text>

      // Check inclusive-or parameter combinations</xsl:text>
			<xsl:for-each select="input/param-combo[@type='inclusive-or']">
				<xsl:text>
      if (</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1"> &amp;&amp; </xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text> == null</xsl:text>
				</xsl:for-each>
				<xsl:text>) {
         context.startResponse(INVALID_PARAMETERS);
         context.startTag("param-combo");
         context.attribute("type", "inclusive-or");</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:text>
         context.startTag("param");
         context.attribute("name", "</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>");
         context.endTag();</xsl:text>
				</xsl:for-each>
				<xsl:text>
         context.endTag();
         return;
      }</xsl:text>
			</xsl:for-each>
		</xsl:if>


		<!-- ************************************************************* -->
		<!-- Check 'exclusive-or' combos                                   -->
		<!-- ************************************************************* -->

		<xsl:if test="input/param-combo[@type='exclusive-or']">
			<xsl:text>

      // Check exclusive-or parameter combinations</xsl:text>
			<xsl:for-each select="input/param-combo[@type='exclusive-or']">
				<xsl:for-each select="param-ref">
					<xsl:variable name="active" select="@name" />
					<xsl:text>
      if (</xsl:text>
					<xsl:value-of select="$active" />
					<xsl:text>!= null &amp;&amp; (</xsl:text>
					<xsl:for-each select="../param-ref[not(@name = $active)]">
						<xsl:if test="position() &gt; 1"> || </xsl:if>
						<xsl:text></xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text> != null</xsl:text>
					</xsl:for-each>
					<xsl:text>)) {
         context.startResponse(INVALID_PARAMETERS);
         context.startTag("param-combo");
         context.attribute("type", "exclusive-or");</xsl:text>
					<xsl:for-each select="param-ref">
						<xsl:text>
         context.startTag("param");
         context.attribute("name", "</xsl:text>
						<xsl:value-of select="$active" />
						<xsl:text>");</xsl:text>
					</xsl:for-each>
					<xsl:text>
         context.endTag();
      }</xsl:text>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:if>


		<!-- ************************************************************* -->
		<!-- Check 'all-or-none' combos                                    -->
		<!-- ************************************************************* -->

		<xsl:if test="input/param-combo[@type='all-or-none']">
			<xsl:text>

      // Check all-or-none parameter combinations</xsl:text>
			<xsl:for-each select="input/param-combo[@type='all-or-none']">
				<xsl:text>
      if (!(</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1"> &amp;&amp; </xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text> == null</xsl:text>
				</xsl:for-each>
				<xsl:text>) &amp;&amp; (</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:if test="position() &gt; 1"> || </xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text> == null</xsl:text>
				</xsl:for-each>
				<xsl:text>)) {
         context.startResponse(INVALID_PARAMETERS);
         context.startTag("param-combo");
         context.attribute("type", "all-or-none");</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:text>
         context.startTag("param");
         context.attribute("name", "</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>");</xsl:text>
				</xsl:for-each>
				<xsl:text>
         }</xsl:text>

			</xsl:for-each>
		</xsl:if>


		<!-- ************************************************************* -->
		<!-- Check values for types                                        -->
		<!-- ************************************************************* -->

		<xsl:if test="input/param[not(@type='_text' or string-length(@type) = 0)]">
			<xsl:text>

      // Check values are valid for the associated types</xsl:text>
			<xsl:for-each select="input/param[not(@type='_text' or string-length(@type) = 0)]">
				<xsl:text>
      if (!</xsl:text>
				<xsl:call-template name="javatypeclass_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="specsdir"     select="$specsdir"     />
					<xsl:with-param name="type"         select="@type"         />
				</xsl:call-template>
				<xsl:text>.SINGLETON.isValidValue(</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>)) {
         context.startResponse(INVALID_PARAMETERS);
         context.startTag("invalid-value-for-type");
         context.attribute("param", "</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>");
         context.attribute("type", "</xsl:text>
				<xsl:value-of select="@type" />
				<xsl:text>");
         context.endTag();
         return;
      }</xsl:text>
			</xsl:for-each>
		</xsl:if>

		<xsl:choose>
			<xsl:when test="$sessionBased = 'true'">
				<xsl:text>

      // Lock on the session and then call the subclass
      synchronized (session) {
         call(context, session</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>

      // Call the subclass
      call(context</xsl:text>
			</xsl:otherwise>
		</xsl:choose>

		<xsl:for-each select="input/param">
			<xsl:text>, </xsl:text>
			<xsl:call-template name="javatype_from_string_for_type">
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="required" select="@required" />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="type"     select="@type"     />
				<xsl:with-param name="variable" select="@name"     />
			</xsl:call-template>
		</xsl:for-each>
		<xsl:text>);</xsl:text>
		<xsl:if test="@createsSession = 'true'">
			<xsl:if test="input/param">
				<xsl:message terminate="yes">No input parameters allowed for functions that create sessions.</xsl:message>
			</xsl:if>
			<xsl:if test="output/param">
				<xsl:message terminate="yes">No output parameters allowed for functions that create sessions.</xsl:message>
			</xsl:if>
			<xsl:text>

      // Create the session
      context.createSession();</xsl:text>
		</xsl:if>
		<xsl:if test="$sessionBased = 'true'">
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
		<xsl:if test="$sessionBased = 'true'">
			<xsl:text><![CDATA[
    *
    * @param session
    *    the current session, never <code>null</code>.]]></xsl:text>
		</xsl:if>
		<xsl:for-each select="input/param">
			<xsl:text>
    *
    * @param </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text><![CDATA[
    *    ]]></xsl:text>
			<!-- TODO: Use an intelligent template -->
			<xsl:value-of select="description/text()" />
		</xsl:for-each>
		<xsl:text><![CDATA[
    */
   public abstract void call(Responder responder]]></xsl:text>
		<xsl:if test="$sessionBased = 'true'">
			<xsl:text>, Session session</xsl:text>
		</xsl:if>
		<xsl:for-each select="input/param">
			<xsl:text>, </xsl:text>
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="type"     select="@type"     />
				<xsl:with-param name="required" select="@required" />
			</xsl:call-template>
			<xsl:text> </xsl:text>
			<xsl:value-of select="@name" />
		</xsl:for-each>
		<xsl:text><![CDATA[)
   throws Throwable;
}
]]></xsl:text>
	</xsl:template>

</xsl:stylesheet>
