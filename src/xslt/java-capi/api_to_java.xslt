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
	<xsl:variable name="specdocsURL">
		<xsl:value-of select="document($project_file)/project/specdocs/@href" />
	</xsl:variable>

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
		<xsl:if test="string-length($specdocsURL) &gt; 0">
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
    */
   public static final CAPI create(org.xins.common.collections.PropertyReader properties,
                                   java.lang.String                           apiName,
                                   org.xins.client.XINSCallConfig             callConfig)
   throws java.lang.IllegalArgumentException,
          org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException {

      // Check arguments
      org.xins.common.MandatoryArgumentChecker.check("properties", properties,
                                                     "apiName",    apiName);

      // TODO: Check validity of API name

      // Determine property name
      java.lang.String propertyName = "capis." + apiName;

      // Build a descriptor from the properties
      org.xins.common.service.Descriptor descriptor = org.xins.common.service.DescriptorBuilder.build(properties, propertyName);

      // Create and return a CAPI instance
      try {
         return new CAPI(descriptor, callConfig);

      // Invalid property value due to unsupported protocol
      } catch (org.xins.common.service.UnsupportedProtocolException e) {
         // TODO: Use correct property name for specific target descriptor
         // TODO: Use correct property value for specific target descriptor
         org.xins.common.service.TargetDescriptor target = e.getTargetDescriptor();
         throw new org.xins.common.collections.InvalidPropertyValueException(propertyName, properties.get(propertyName), "Protocol in URL \"" + target.getURL() + "\" is not supported.");
      }
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
    */
   public static final CAPI create(org.xins.common.collections.PropertyReader properties,
                                   java.lang.String                           apiName)
   throws java.lang.IllegalArgumentException,
          org.xins.common.collections.MissingRequiredPropertyException,
          org.xins.common.collections.InvalidPropertyValueException {
      return create(properties, apiName, null);
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
			<xsl:variable name="functionFile">
				<xsl:value-of select="$specsdir" />
				<xsl:text>/</xsl:text>
				<xsl:value-of select="$functionName" />
				<xsl:text>.fnc</xsl:text>
			</xsl:variable>
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
	<!-- Print the constructor                                             -->
	<!-- ***************************************************************** -->

	<xsl:template name="constructor">
		<xsl:text><![CDATA[

   /**
    * Constructs a new <code>CAPI</code> object, using the specified
    * <code>Descriptor</code>, with the specified call configuration.
    *
    * @param descriptor
    *    the descriptor for the service(s), cannot be <code>null</code>.
    *
    * @param callConfig
    *    the call configuration object, or <code>null</code> if a default
    *    should be used.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    *
    * @throws org.xins.common.service.UnsupportedProtocolException
    *    if any of the target descriptors specifies an unsupported protocol.
    *
    * @since XINS 1.1.0
    */
   private CAPI(org.xins.common.service.Descriptor descriptor,
                org.xins.client.XINSCallConfig     callConfig)
   throws java.lang.IllegalArgumentException,
          org.xins.common.service.UnsupportedProtocolException {

      // Call the superclass constructor
      super(descriptor, callConfig);
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
      this(descriptor, null);
   }]]></xsl:text>
	</xsl:template>


	<!-- ***************************************************************** -->
	<!-- Print a method to call a single function                          -->
	<!-- ***************************************************************** -->

	<xsl:template match="function">

		<!-- Define parameters -->
		<xsl:param name="name" />

		<!-- Determine the name of the method in the API class that will call
		     this function. -->
		<xsl:variable name="methodName">
			<xsl:text>call</xsl:text>
			<xsl:value-of select="$name" />
		</xsl:variable>

		<!-- Determine if this methods returns a <FunctionName>Result
		     object or just void -->
		<xsl:variable name="returnType">
			<xsl:value-of select="$name" />
			<xsl:text>Result</xsl:text>
		</xsl:variable>

		<!-- Check name set in function definition file -->
		<xsl:if test="string-length(@name) &gt; 0 and not($name = @name)">
			<xsl:message terminate="yes">Name in function definition file differs from name defined in API definition file.</xsl:message>
		</xsl:if>

		<xsl:text><![CDATA[

   /**
    * Calls the <em>]]></xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text><![CDATA[</em> function.
    *
    * <p>Generated from function specification version ]]></xsl:text>
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision" select="@rcsversion" />
		</xsl:call-template>
		<xsl:text><![CDATA[.
    * See the
    * <a href="]]></xsl:text>
		<xsl:value-of select="$specdocsURL" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text><![CDATA[.html">online function specification</a>.]]></xsl:text>
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
FIXME for Ernst: Catch all expected error codes and throw a specific error code exception
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
		<xsl:variable name="origDescription">
			<xsl:value-of select="normalize-space(description/text())" />
		</xsl:variable>
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
