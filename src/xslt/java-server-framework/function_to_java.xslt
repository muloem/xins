<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the abstract class as specified in the function.
 The abtract class is responsible for checking the parameters.
 It also includes the style sheets request_java.xslt and result_java.xslt.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
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
	<xsl:param name="impl_file"    />

	<!-- Perform includes -->
	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />
	<xsl:include href="../types.xslt"  />
	<xsl:include href="request_java.xslt" />
	<xsl:include href="result_java.xslt" />
	<xsl:include href="check_params.xslt" />

	<xsl:template match="function">

		<xsl:variable name="functionName" select="//function/@name" />
		<xsl:variable name="className"    select="$functionName"    />
		<xsl:variable name="fqcn">
			<xsl:value-of select="$package" />
			<xsl:text>.</xsl:text>
			<xsl:value-of select="$className" />
		</xsl:variable>
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision" select="@rcsversion" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Create the function abstract class. -->
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text><![CDATA[;

/**
 * Abstract base class for <code>]]></xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text><![CDATA[</code> function implementation.
 */
public abstract class ]]></xsl:text>
		<xsl:value-of select="$functionName" />
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
		<xsl:value-of select="$functionName" />
		<xsl:text><![CDATA[</code> instance.
    *
    * @param api
    *    the API to which this function belongs, guaranteed to be not
    *    <code>null</code>.
    */
   protected ]]></xsl:text>
		<xsl:value-of select="$functionName" />
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
		<xsl:if test="document($project_file)/project/api[@name = $api]/impl">
			<xsl:for-each select="document($impl_file)/impl/instance">
				<xsl:text>
      </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text> = api.</xsl:text>
				<xsl:value-of select="@getter" />
				<xsl:text>();</xsl:text>
			</xsl:for-each>
		</xsl:if>
		<xsl:text><![CDATA[

      // Initialize list of error codes
      _errorCodes = new java.util.HashSet();]]></xsl:text>
      <xsl:for-each select="output/resultcode-ref">
         <xsl:text>
      _errorCodes.add("</xsl:text>
         <xsl:value-of select="@name" />
         <xsl:text>");</xsl:text>
      </xsl:for-each>
		<xsl:text><![CDATA[
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The list of error codes supported by this function. This field cannot be
    * <code>null</code>.
    */
   private final java.util.HashSet _errorCodes;
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
		<xsl:if test="document($project_file)/project/api[@name = $api]/impl">
			<xsl:for-each select="document($impl_file)/impl/instance">
				<xsl:text>
   protected final </xsl:text>
				<xsl:value-of select="@class" />
				<xsl:text> </xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>;

</xsl:text>
			</xsl:for-each>
		</xsl:if>

		<xsl:text>
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected final org.xins.server.FunctionResult handleCall(org.xins.server.CallContext _context)
   throws java.lang.Throwable {</xsl:text>

		<!-- ************************************************************* -->
		<!-- Retrieve input parameters                                     -->
		<!-- ************************************************************* -->

		<xsl:if test="input/data/element">
			<xsl:text>
      org.xins.common.xml.Element _dataElement = _context.getDataElement();</xsl:text>
		</xsl:if>

		<xsl:apply-templates select="input" mode="checkParams">
			<xsl:with-param name="side" select="'server'" />
		</xsl:apply-templates>

		<!-- ************************************************************* -->
		<!-- Invoke the abstract call method                               -->
		<!-- ************************************************************* -->

		<xsl:text>
      Request _request = new Request(_context.getRemoteAddr()</xsl:text>

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
		<xsl:if test="input/data/element">
			<xsl:text>, _dataElement</xsl:text>
		</xsl:if>
		<xsl:text>);
      Result _result = call(_request);

      // The method should never return null
      if (_result == null) {
         throw org.xins.common.Utils.logProgrammingError(
            "</xsl:text>
      <xsl:value-of select="$className" />
      <xsl:text>", // detecting class
            "handleCall(org.xins.server.CallContext)", // detecting method
            getClass().getName(), // subject class
            "call(</xsl:text>
      <xsl:value-of select="$className" />
      <xsl:text>$Request)", // subject method
            "Return value is null."
         );
      }

      // Check that the Result object is really a FunctionResult instance. If
      // not, then the developer must have implemented his own class that
      // implements Result. He should never do this.
      if (! (_result instanceof org.xins.server.FunctionResult)) {
         throw org.xins.common.Utils.logProgrammingError(
            "</xsl:text>
      <xsl:value-of select="$className" />
      <xsl:text>", // detecting class
            "handleCall(org.xins.server.CallContext)", // detecting method
            getClass().getName(), // subject class
            "call(</xsl:text>
      <xsl:value-of select="$className" />
      <xsl:text>$Request)", // subject method
            "Return value is an instance of class " + _result.getClass().getName() + ", which is not derived from class org.xins.server.FunctionResult."
         );
      }

      // Convert the Result object to a proper FunctionResult instance
      org.xins.server.FunctionResult _fr = (org.xins.server.FunctionResult) _result;

      // Check that if an error code is set, it is a supported one
      java.lang.String _errorCode = _fr.getErrorCode();
      if (_errorCode != null) {
         if (! _errorCodes.contains(_errorCode)) {
            throw org.xins.common.Utils.logProgrammingError(
               "</xsl:text>
      <xsl:value-of select="$className" />
      <xsl:text>", // detecting class
               "handleCall(org.xins.server.CallContext)", // detecting method
               getClass().getName(), // subject class
               "call(</xsl:text>
      <xsl:value-of select="$className" />
      <xsl:text>$Request)", // subject method
               "The error code \"" + _errorCode + "\" is not supported by this function."
            );
         }
      }

      return _fr;</xsl:text>
		<xsl:text><![CDATA[
   }

   /**
    * Calls this function. If the function fails, it may throw any kind of
    * exception. All exceptions will be handled by the caller.
    *
    * @param request
    *    the request, never <code>null</code>.
    *
    * @return
    *    the result of the function call, should never be <code>null</code>.
    *
    * @throws java.lang.Throwable
    *    if anything went wrong.
    */
   public abstract Result call(Request request)
   throws java.lang.Throwable;


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
		</xsl:call-template>
<xsl:text>
}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
