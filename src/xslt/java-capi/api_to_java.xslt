<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the CAPI.java class.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />
	<xsl:param name="xins_version" />

	<!-- Output is text/plain -->
	<xsl:output method="text" />

	<!-- Perform includes -->
	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../java.xslt"       />
	<xsl:include href="../rcs.xslt"        />
	<xsl:include href="../types.xslt"      />

	<!-- Determine the location of the online specification docs -->
	<xsl:variable name="specdocsURL" select="document($project_file)/project/specdocs/@href" />
	<xsl:variable name="hasSpecdocsURL" select="string-length($specdocsURL) &gt; 0" />

	<!-- ***************************************************************** -->
	<!-- Match the root element: api                                       -->
	<!-- ***************************************************************** -->

	<xsl:template match="api">

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />

		<xsl:text><![CDATA[;

/**
 * Stub for the <em>]]></xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text><![CDATA[</em> API.]]></xsl:text>

		<!-- Display the specdocs URL if it is specified -->
		<xsl:if test="$hasSpecdocsURL">
			<xsl:text><![CDATA[
 *
 * <p>See the <a href="]]></xsl:text>
			<xsl:value-of select="$specdocsURL" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$api" />
			<xsl:text><![CDATA[/">API specification</a>.]]></xsl:text>
		</xsl:if>

		<xsl:text><![CDATA[
 */
public final class CAPI extends org.xins.client.AbstractCAPI {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>CAPI</code> object for the specified API from a set
    * of properties, with a specific call configuration.
    *
    * @param properties
    *    the properties to create a <code>CAPI</code> object for, cannot be
    *    <code>null</code>.
    *
    * @param apiName
    *    the name of the API to create a <code>CAPI</code> object for, cannot
    *    be <code>null</code> and must be a valid API name.
    *
    * @param callConfig
    *    configuration to be used when making calls, or <code>null</code> if a
    *    default should be applied.
    *
    * @return
    *    a new <code>CAPI</code> object, never <code>null</code>.
    *
    * @throws java.lang.IllegalArgumentException
    *    if <code>properties == null || apiName == null</code> or if
    *    <code>apiName</code> is not considered to be a valid API name.
    *
    * @throws org.xins.common.collections.MissingRequiredPropertyException
    *    if a required property is missing in the specified properties set.
    *
    * @throws org.xins.common.collections.InvalidPropertyValueException
    *    if one of the properties in the specified properties set is used to
    *    create a <code>CAPI</code> instance but its value is considered
    *    invalid.
    *
    * @since XINS 1.1.0
    *
    * @deprecated
    *    Deprecated since XINS 1.2.0.
    *    Use the
    *    {@link #CAPI(org.xins.common.collections.PropertyReader,java.lang.String) CAPI(PropertyReader,String)}
    *    constructor in combination with the
    *    {@link #setXINSCallConfig(org.xins.client.XINSCallConfig) setXINSCallConfig(XINSCallConfig)}
    *    method instead.
    */
   public static final CAPI create(org.xins.common.collections.PropertyReader properties,
                                   java.lang.String                           apiName,
                                   org.xins.client.XINSCallConfig             callConfig)
   throws java.lang.IllegalArgumentException,
          org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException {

      CAPI capi = new CAPI(properties, apiName);
      capi.setXINSCallConfig(callConfig);
      return capi;
   }

   /**
    * Creates a new <code>CAPI</code> object for the specified API from a set
    * of properties.
    *
    * @param properties
    *    the properties to create a <code>CAPI</code> object for, cannot be
    *    <code>null</code>.
    *
    * @param apiName
    *    the name of the API to create a <code>CAPI</code> object for, cannot
    *    be <code>null</code> and must be a valid API name.
    *
    * @return
    *    a new <code>CAPI</code> object, never <code>null</code>.
    *
    * @throws java.lang.IllegalArgumentException
    *    if <code>properties == null || apiName == null</code> or if
    *    <code>apiName</code> is not considered to be a valid API name.
    *
    * @throws org.xins.common.collections.MissingRequiredPropertyException
    *    if a required property is missing in the specified properties set.
    *
    * @throws org.xins.common.collections.InvalidPropertyValueException
    *    if one of the properties in the specified properties set is used to
    *    create a <code>CAPI</code> instance but its value is considered
    *    invalid.
    *
    * @since XINS 1.1.0
    *
    * @deprecated
    *    Deprecated since XINS 1.2.0.
    *    Use the
    *    {@link #CAPI(org.xins.common.collections.PropertyReader) CAPI(PropertyReader)}
    *    constructor instead. The name of the API does not need to be passed
    *    to this constructor, it will assume the name specified in the API
    *    specification.
    */
   public static final CAPI create(org.xins.common.collections.PropertyReader properties,
                                   java.lang.String                           apiName)
   throws java.lang.IllegalArgumentException,
          org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException {
      return new CAPI(properties, apiName);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Secret key used when creating <code>ProtectedPropertyReader</code>
    * instances.
    */
   private static final Object SECRET_KEY = new Object();


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------]]></xsl:text>

		<xsl:call-template name="constructor" />
		<xsl:text><![CDATA[


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------]]></xsl:text>
		<xsl:text><![CDATA[

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------]]></xsl:text>

		<!-- Loop through all <function/> elements within the <api/> element
		     and process the corresponding .fnc function definition files. -->
		<xsl:for-each select="function">
			<xsl:variable name="functionName" select="@name" />
			<xsl:variable name="functionFile" select="concat($specsdir, '/', $functionName, '.fnc')" />
			<xsl:apply-templates select="document($functionFile)/function">
				<xsl:with-param name="name" select="$functionName" />
			</xsl:apply-templates>
		</xsl:for-each>

		<xsl:text><![CDATA[

   /**
    * Returns the version of XINS used to build this CAPI class.
    *
    * @return
    *    the version as a {@link String}, e.g. <code>"]]></xsl:text>
			<xsl:value-of select="$xins_version" />
			<xsl:text><![CDATA["</code>;
    *    never <code>null</code>.
    */
   public String getXINSVersion() {
      return "]]></xsl:text>
			<xsl:value-of select="$xins_version" />
			<xsl:text><![CDATA[";
   }
}
]]></xsl:text>
	</xsl:template>


	<!-- ***************************************************************** -->
	<!-- Print the constructors                                            -->
	<!-- ***************************************************************** -->

	<xsl:template name="constructor">
		<xsl:text><![CDATA[

   /**
    * Constructs a new <code>CAPI</code> object for the specified API from a
    * set of properties.
    *
    * @param properties
    *    the properties to create a <code>CAPI</code> object for, cannot be
    *    <code>null</code>.
    *
    * @throws java.lang.IllegalArgumentException
    *    if <code>properties == null</code>.
    *
    * @throws org.xins.common.collections.MissingRequiredPropertyException
    *    if a required property is missing in the specified properties set.
    *
    * @throws org.xins.common.collections.InvalidPropertyValueException
    *    if one of the properties in the specified properties set is used to
    *    create a <code>CAPI</code> instance but its value is considered
    *    invalid.
    *
    * @since XINS 1.2.0
    */
   public CAPI(org.xins.common.collections.PropertyReader properties)
   throws java.lang.IllegalArgumentException,
          org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException {
      super(properties, "]]></xsl:text>
		<xsl:value-of select="//api/@name" />
		<xsl:text><![CDATA[");
   }

   /**
    * Constructs a new <code>CAPI</code> object for the specified API from a
    * set of properties, specifying the API name to assume.
    *
    * @param properties
    *    the properties to create a <code>CAPI</code> object for, cannot be
    *    <code>null</code>.
    *
    * @param apiName
    *    the name of the API, cannot be <code>null</code> and must be a valid
    *    API name.
    *
    * @throws java.lang.IllegalArgumentException
    *    if <code>properties == null || apiName == null</code> or if
    *    <code>apiName</code> is not considered to be a valid API name.
    *
    * @throws org.xins.common.collections.MissingRequiredPropertyException
    *    if a required property is missing in the specified properties set.
    *
    * @throws org.xins.common.collections.InvalidPropertyValueException
    *    if one of the properties in the specified properties set is used to
    *    create a <code>CAPI</code> instance but its value is considered
    *    invalid.
    *
    * @since XINS 1.2.0
    */
   public CAPI(org.xins.common.collections.PropertyReader properties,
               java.lang.String                           apiName)
   throws java.lang.IllegalArgumentException,
          org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException {
      super(properties, apiName);
   }

   /**
    * Constructs a new <code>CAPI</code> object, using the specified
    * <code>Descriptor</code>.
    *
    * @param descriptor
    *    the descriptor for the service(s), cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    *
    * @throws org.xins.common.service.UnsupportedProtocolException
    *    if any of the target descriptors specifies an unsupported protocol
    *    (<em>since XINS 1.1.0</em>).
    */
   public CAPI(org.xins.common.service.Descriptor descriptor)
   throws java.lang.IllegalArgumentException,
          org.xins.common.service.UnsupportedProtocolException {
      super(descriptor);
   }]]></xsl:text>
	</xsl:template>


	<!-- ***************************************************************** -->
	<!-- Print a method to call a single function                          -->
	<!-- ***************************************************************** -->

	<xsl:template match="function">

		<!-- Define parameters -->
		<xsl:param name="name" />

		<!-- Determine the name of the call methods -->
		<xsl:variable name="methodName" select="concat('call', $name)" />

		<!-- Always return a <FunctionName>Result object -->
		<xsl:variable name="returnType" select="concat($name, 'Result')" />

		<!-- Check name set in function definition file -->
		<xsl:if test="string-length(@name) &gt; 0 and not($name = @name)">
			<xsl:message terminate="yes">Name in function definition file differs from name defined in API definition file.</xsl:message>
		</xsl:if>

		<!-- Print method that accepts the a request object only -->
		<xsl:text><![CDATA[

   /**
    * Calls the <em>]]></xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text><![CDATA[</em>
    * function using the specified request.
    *
    * <p>Generated from function specification version ]]></xsl:text>
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision" select="@rcsversion" />
		</xsl:call-template>
		<xsl:text>.</xsl:text>
		<xsl:if test="$hasSpecdocsURL">
			<xsl:text><![CDATA[
    * See the
    * <a href="]]></xsl:text>
			<xsl:value-of select="$specdocsURL" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$api" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text><![CDATA[.html">online function specification</a>.]]></xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[
    *
    * @param request
    *    the request, cannot be <code>null</code>.
    *
    * @return
    *    the result, not <code>null</code>.
    *
    * @throws java.lang.IllegalArgumentException
    *    if <code>request == null</code>.
    *
    * @throws org.xins.client.UnacceptableRequestException
    *    if the request is considered to be unacceptable; this is determined
    *    by calling
    *    <code>request.</code>{@link org.xins.client.AbstractCAPICallRequest#validate() validate()}.
    *
    * @throws org.xins.common.service.GenericCallException
    *    if the first call attempt failed due to a generic reason and all the
    *    other call attempts (if any) failed as well.
    *
    * @throws org.xins.common.http.HTTPCallException
    *    if the first call attempt failed due to an HTTP-related reason and
    *    all the other call attempts (if any) failed as well.
    *
    * @throws org.xins.client.XINSCallException
    *    if the first call attempt failed due to a XINS-related reason and
    *    all the other call attempts (if any) failed as well.
    *
    * @since XINS 1.2.0
    */
   public ]]></xsl:text>
		<xsl:value-of select="$returnType" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text>Request request)
   throws java.lang.IllegalArgumentException,
          org.xins.client.UnacceptableRequestException,
          org.xins.common.service.GenericCallException,
          org.xins.common.http.HTTPCallException,
          org.xins.client.XINSCallException {

      // Execute the call request
      org.xins.client.XINSCallResult result = callImpl(request);

      return new </xsl:text>

<!--
TODO for Ernst: Catch all expected error codes and throw a specific error code exception
-->

		<xsl:value-of select="$returnType" />
		<xsl:text>(result);</xsl:text>
		<xsl:text>
   }</xsl:text>

		<!-- Print method that accepts the individual parameters -->
		<xsl:text><![CDATA[

   /**
    * Calls the <em>]]></xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text><![CDATA[</em>
    * function with the specified parameters.
    *
    * <p>Generated from function specification version ]]></xsl:text>
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision" select="@rcsversion" />
		</xsl:call-template>
		<xsl:text>.</xsl:text>
		<xsl:if test="$hasSpecdocsURL">
			<xsl:text><![CDATA[
    * See the
    * <a href="]]></xsl:text>
			<xsl:value-of select="$specdocsURL" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$api" />
			<xsl:text>/</xsl:text>
			<xsl:value-of select="$name" />
			<xsl:text><![CDATA[.html">online function specification</a>.]]></xsl:text>
		</xsl:if>
		<xsl:apply-templates select="input/param" mode="javadoc" />
		<xsl:if test="input/data/element">
			<xsl:text><![CDATA[
    *
    * @param _dataSection
    *    the data section for the request, or <code>null</code> if the data
    *    section should be empty.]]></xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[
    *
    * @return
    *    the result, not <code>null</code>.
    *
    * @throws org.xins.common.service.GenericCallException
    *    if the first call attempt failed due to a generic reason and all the
    *    other call attempts (if any) failed as well.
    *
    * @throws org.xins.common.http.HTTPCallException
    *    if the first call attempt failed due to an HTTP-related reason and
    *    all the other call attempts (if any) failed as well.
    *
    * @throws org.xins.client.XINSCallException
    *    if the first call attempt failed due to a XINS-related reason and
    *    all the other call attempts (if any) failed as well.
    */
   public ]]></xsl:text>
		<xsl:value-of select="$returnType" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>
		<xsl:apply-templates select="input/param" mode="methodSignature" />
		<xsl:if test="input/data/element">
			<xsl:if test="input/param">
				<xsl:text>, </xsl:text>
			</xsl:if>
			<xsl:text>org.xins.common.xml.Element _dataSection</xsl:text>
		</xsl:if>
		<xsl:text>)
   throws org.xins.common.service.GenericCallException,
          org.xins.common.http.HTTPCallException,
          org.xins.client.XINSCallException {

      // Get the XINS service caller
      org.xins.client.XINSServiceCaller caller = getCaller();</xsl:text>
		<xsl:if test="input/param">
			<xsl:text>

      // Store the input parameters in a PropertyReader
      org.xins.common.collections.ProtectedPropertyReader params = new org.xins.common.collections.ProtectedPropertyReader(SECRET_KEY);</xsl:text>
			<xsl:apply-templates select="input/param" mode="store" />
		</xsl:if>

		<xsl:text>

      // Construct a call request
      org.xins.client.XINSCallRequest request = new org.xins.client.XINSCallRequest(</xsl:text>
		<xsl:text>"</xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text>", </xsl:text>
		<xsl:choose>
			<xsl:when test="input/param">
				<xsl:text>params</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>null</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="input/data/element">
			<xsl:text>, _dataSection</xsl:text>
		</xsl:if>
		<xsl:text>);

      // Execute the call request
      org.xins.client.XINSCallResult result = caller.call(request);

      return new </xsl:text>

<!--
TODO for Ernst: Catch all expected error codes and throw a specific error code exception
-->

		<xsl:value-of select="$returnType" />
		<xsl:text>(result);</xsl:text>
		<xsl:text>
   }</xsl:text>
	</xsl:template>


	<!-- ***************************************************************** -->
	<!-- Prints an @param section for an input parameter.                  -->
	<!-- ***************************************************************** -->

	<xsl:template match="input/param" mode="javadoc">

		<!-- Determine if the input parameter is mandatory. -->
		<xsl:variable name="required">
			<xsl:choose>
				<xsl:when test="string-length(@required) &lt; 1">false</xsl:when>
				<xsl:when test="@required = 'false'">false</xsl:when>
				<xsl:when test="@required = 'true'">true</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<!-- Determine the Java primary data type or class for the input
		     parameter -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="$required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine if $javatype is a Java primary data type -->
		<xsl:variable name="typeIsPrimary">
			<xsl:call-template name="is_java_datatype">
				<xsl:with-param name="text" select="$javatype" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Determine the description for the input parameter -->
		<xsl:variable name="origDescription" select="normalize-space(description/text())" />
		<xsl:variable name="description">
			<xsl:choose>
				<xsl:when test="string-length($origDescription) = 0">
					<xsl:text><![CDATA[the value of the <em>]]></xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text><![CDATA[</em> input parameter.]]></xsl:text>
				</xsl:when>
				<xsl:when test="substring($origDescription, string-length($origDescription), 1) = '.'">
					<xsl:call-template name="hungarianLower">
						<xsl:with-param name="text" select="$origDescription" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="hungarianLower">
						<xsl:with-param name="text" select="$origDescription" />
					</xsl:call-template>
					<xsl:text>.</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- Perform the actual printing -->
		<xsl:text>
    *
    * @param </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>
    *    </xsl:text>
		<xsl:value-of select="$description" />
		<xsl:if test="$typeIsPrimary = 'false'">
			<xsl:choose>
				<xsl:when test="$required = 'true'">
					<xsl:text><![CDATA[
    *    Cannot be <code>null</code>.]]></xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[
    *    Can be <code>null</code>.]]></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>


	<!-- ***************************************************************** -->
	<!-- Prints an argument definition for a function calling method       -->
	<!-- ***************************************************************** -->

	<xsl:template match="input/param" mode="methodSignature">

		<!-- Determine if this parameter is required -->
		<xsl:variable name="required">
			<xsl:choose>
				<xsl:when test="string-length(@required) &lt; 1">false</xsl:when>
				<xsl:when test="@required = 'false'">false</xsl:when>
				<xsl:when test="@required = 'true'">true</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<!-- Determine the Java class or primary data type -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="$required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="position() &gt; 1">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:value-of select="$javatype" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
	</xsl:template>


	<!-- ***************************************************************** -->
	<!-- Print code that will store an input parameter in a variable       -->
	<!-- ***************************************************************** -->

	<xsl:template match="input/param" mode="store">
		<xsl:variable name="required">
			<xsl:choose>
				<xsl:when test="string-length(@required) &lt; 1">false</xsl:when>
				<xsl:when test="@required = 'false'">false</xsl:when>
				<xsl:when test="@required = 'true'">true</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="@required = 'false'" >
				<xsl:text>
      if (</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text> != null) {
         params.set(SECRET_KEY, "</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>", </xsl:text>
				<xsl:call-template name="javatype_to_string_for_type">
					<xsl:with-param name="api"      select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="required" select="$required" />
					<xsl:with-param name="type"     select="@type" />
					<xsl:with-param name="variable" select="@name" />
				</xsl:call-template>
				<xsl:text>);
      }</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>
      params.set(SECRET_KEY, "</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>", </xsl:text>
				<xsl:call-template name="javatype_to_string_for_type">
					<xsl:with-param name="api"      select="$api" />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="required" select="$required" />
					<xsl:with-param name="type"     select="@type" />
					<xsl:with-param name="variable" select="@name" />
				</xsl:call-template>
				<xsl:text>);</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
