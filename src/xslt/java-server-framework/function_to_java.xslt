<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the abstract class as specified in the function.
 The abtract class is responsible for checking the parameters.
 It also includes the style sheets request_java.xslt and result_java.xslt.

 $Id$
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<!-- Define parameters -->
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<!-- Perform includes -->
	<xsl:include href="../function.xslt" />
	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />
	<xsl:include href="../types.xslt"  />
	<xsl:include href="request_java.xslt"       />
	<xsl:include href="result_java.xslt"  />

	<xsl:variable name="version">
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision">
				<xsl:value-of select="//function/@rcsversion" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>

	<xsl:template match="function">
		<!-- Initialize the session related variables. -->
		<xsl:variable name="sessionBased">
			<xsl:call-template name="is_function_session_based" />
		</xsl:variable>
		<xsl:variable name="createsSession">
			<xsl:call-template name="does_function_creates_session" />
		</xsl:variable>

		<!-- Create the function abstract class. -->
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text><![CDATA[;

/**
 * Abstract base class for <code>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</code> function implementation.
 */
public abstract class ]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[ extends org.xins.server.Function {

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

   protected final org.xins.server.FunctionResult handleCall(org.xins.server.CallContext context)
   throws Throwable {</xsl:text>


		<!-- ************************************************************* -->
		<!-- Retrieve session                                              -->
		<!-- ************************************************************* -->

		<xsl:if test="$sessionBased = 'true'">
			<xsl:text>

      // Get the session
      org.xins.server.Session _session = context.getSession();</xsl:text>
		</xsl:if>


		<!-- ************************************************************* -->
		<!-- Retrieve input parameters                                     -->
		<!-- ************************************************************* -->

		<xsl:if test="input/param">
			<xsl:text>

      // Get the input parameters</xsl:text>

			<xsl:for-each select="input/param">
				<xsl:text>
      java.lang.String </xsl:text>
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
          org.xins.server.MissingParametersResult _errorResult = new org.xins.server.MissingParametersResult();</xsl:text>
			<xsl:choose>
				<xsl:when test="count(input/param[@required='true']) &gt; 1">
					<xsl:for-each select="input/param[@required='true']">
						<xsl:text>
         if (</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text> == null) {
            _errorResult.addMissingParameter("</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>");
         }</xsl:text>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>
         _errorResult.addMissingParameter("</xsl:text>
					<xsl:value-of select="input/param[@required='true']/@name" />
						<xsl:text>");</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>
         return _errorResult;
      }</xsl:text>
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
         org.xins.server.InvalidParametersResult _errorResult = new org.xins.server.InvalidParametersResult();
         _errorResult.setInvalidTypeForValue("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>", "</xsl:text>
				<xsl:value-of select="@type" />
				<xsl:text>");
         return _errorResult;
      }</xsl:text>
			</xsl:for-each>
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
         org.xins.server.InvalidParametersResult _errorResult = new org.xins.server.InvalidParametersResult();
         java.util.List _invalidComboElements = new java.util.ArrayList();</xsl:text>
				<xsl:for-each select="param-ref">
				<xsl:text>
         _invalidComboElements.add("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>");</xsl:text>
				</xsl:for-each>
				<xsl:text>
         _errorResult.setParamCombo("inclusive-or", _invalidComboElements);
         return _errorResult;
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
         org.xins.server.InvalidParametersResult _errorResult = new org.xins.server.InvalidParametersResult();
         java.util.List _invalidComboElements = new java.util.ArrayList();</xsl:text>
					<xsl:for-each select="param-ref">
						<xsl:text>
         _invalidComboElements.add("</xsl:text>
						<xsl:value-of select="$active" />
						<xsl:text>");</xsl:text>
					</xsl:for-each>
					<xsl:text>
         _errorResult.setParamCombo("exclusive-or", _invalidComboElements);
         return _errorResult;
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
         org.xins.server.InvalidParametersResult _errorResult = new org.xins.server.InvalidParametersResult();
         java.util.List _invalidComboElements = new java.util.ArrayList();</xsl:text>
				<xsl:for-each select="param-ref">
					<xsl:text>
         _invalidComboElements.add("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>");</xsl:text>
				</xsl:for-each>
				<xsl:text>
         _errorResult.setParamCombo("all-or-none", _invalidComboElements);
         return _errorResult;
         }</xsl:text>

			</xsl:for-each>
		</xsl:if>

		<!-- ************************************************************* -->
		<!-- Invoke the abstract call method                               -->
		<!-- ************************************************************* -->

		<xsl:choose>
			<xsl:when test="$createsSession = 'true'">
				<xsl:text>
      // Create the session
      org.xins.server.Session _session = context.createSession();
</xsl:text>
			</xsl:when>
			<xsl:when test="$sessionBased = 'true'">
				<xsl:text>

      // Lock on the session and then call the subclass
      synchronized (_session) {
         </xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:text>
      Request _callRequest = new Request(</xsl:text>

		<xsl:for-each select="input/param">
			<xsl:if test="not(position() = 1)">, </xsl:if>
			<xsl:call-template name="javatype_from_string_for_type">
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="required" select="@required" />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="type"     select="@type"     />
				<xsl:with-param name="variable" select="@name"     />
			</xsl:call-template>
		</xsl:for-each>
		<xsl:if test="$sessionBased = 'true' or $createsSession = 'true'">
			<xsl:if test="input/param">
				<xsl:text>, </xsl:text>
			</xsl:if>
			<xsl:text>_session</xsl:text>
		</xsl:if>
		<xsl:text>);
      Result _result = call(_callRequest);
      return (org.xins.server.FunctionResult) _result;</xsl:text>
		<!-- TODO: Dispose the session if appropriate -->
		<xsl:if test="$sessionBased = 'true'">
			<xsl:text>
      }</xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[
   }

   /**
    * Calls this function. If the function fails, it may throw any kind of
    * exception. All exceptions will be handled by the caller.
    *
    * @param request
    *    the container that contains the input value and the session if needed, never <code>null</code>.
    *
    * @return Result
    *    the result of your function, cannot be <code>null</code>.
    *
    * @throws Throwable
    *    if anything went wrong.
    */
   public abstract Result call(Request request) throws Throwable;

   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------
]]></xsl:text>

		<!-- Generates the Request object used to get the input data. -->
		<xsl:call-template name="request">
			<xsl:with-param name="project_home"   select="$project_home"   />
			<xsl:with-param name="project_file"   select="$project_file"   />
			<xsl:with-param name="api"            select="$api"            />
			<xsl:with-param name="api_file"       select="$api_file"       />
			<xsl:with-param name="specsdir"       select="$specsdir"       />
			<xsl:with-param name="sessionBased"   select="$sessionBased"   />
			<xsl:with-param name="createsSession" select="$createsSession" />
		</xsl:call-template>
<xsl:text>
</xsl:text>
		<!-- Generates the Result interfaces and object used to set the output data. -->
		<xsl:call-template name="result">
			<xsl:with-param name="project_home"   select="$project_home"   />
			<xsl:with-param name="project_file"   select="$project_file"   />
			<xsl:with-param name="api"            select="$api"            />
			<xsl:with-param name="api_file"       select="$api_file"       />
			<xsl:with-param name="specsdir"       select="$specsdir"       />
			<xsl:with-param name="createsSession" select="$createsSession" />
		</xsl:call-template>
<xsl:text>
}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
