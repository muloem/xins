<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the CAPI.java class.

 $Id$
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

	<!-- Determine the location of the online specification docs -->
	<xsl:variable name="specdocsURL">
		<xsl:value-of select="document($project_file)/project/specdocs/@href" />
	</xsl:variable>

	<!-- Output is text/plain -->
	<xsl:output method="text" />

	<!-- Perform includes -->
	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../java.xslt"       />
	<xsl:include href="../rcs.xslt"        />
	<xsl:include href="../types.xslt"      />


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
    * Checks the arguments for the constructor and returns the
    * <code>XINSServiceCaller</code>.
    *
    * @param caller
    *    the {@link org.xins.client.XINSServiceCaller}, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>caller == null</code>.
    */
   private static final org.xins.client.XINSServiceCaller checkArguments(
      org.xins.client.XINSServiceCaller caller
   )
   throws IllegalArgumentException {

      // Check preconditions
      org.xins.common.MandatoryArgumentChecker.check("caller", caller);

      return caller;
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

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

   public String getXINSVersion() {
      return "]]></xsl:text>
			<xsl:value-of select="$xins_version" />
			<xsl:text><![CDATA[";
   }]]></xsl:text>

		<xsl:text><![CDATA[
}
]]></xsl:text>
	</xsl:template>


	<!-- ***************************************************************** -->
	<!-- Print the constructor                                             -->
	<!-- ***************************************************************** -->

	<xsl:template name="constructor">
		<xsl:text><![CDATA[

   /**
    * Constructs a new <code>CAPI</code> object for the specified XINS service
    * caller.
    *
    * @param caller
    *    the XINS service caller, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>caller == null</code>.
    */
   public CAPI(org.xins.client.XINSServiceCaller caller)
   throws IllegalArgumentException {

      // Call the superclass constructor
      super(caller);
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

		<!-- Determine the return type of the method, either a Java primary
		     data type or a Java class name. -->
		<xsl:variable name="returnType">
			<xsl:choose>
				<xsl:when test="(output/param and output/data/element) or count(output/param) &gt; 1">
					<xsl:value-of select="$name" />
					<xsl:text>Result</xsl:text>
				</xsl:when>
				<xsl:when test="output/param">
					<xsl:call-template name="javatype_for_type">
						<xsl:with-param name="project_file" select="$project_file"          />
						<xsl:with-param name="api"          select="$api"                   />
						<xsl:with-param name="specsdir"     select="$specsdir"              />
						<xsl:with-param name="required"     select="output/param/@required" />
						<xsl:with-param name="type"         select="output/param/@type"     />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="output/data/element">
					<xsl:text>org.jdom.Element</xsl:text>
				</xsl:when>
				<xsl:otherwise>void</xsl:otherwise>
			</xsl:choose>
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
		<xsl:choose>
			<xsl:when test="output/param and output/data/element">
				<xsl:text><![CDATA[
    *
    * @return
    *    the result, not <code>null</code>.]]></xsl:text>
			</xsl:when>
			<xsl:when test="output/param">
				<!-- Determine if this parameter is required -->
				<xsl:variable name="required">
					<xsl:choose>
						<xsl:when test="string-length(output/param/@required) &lt; 1">false</xsl:when>
						<xsl:when test="output/param/@required = 'false'">false</xsl:when>
						<xsl:when test="output/param/@required = 'true'">true</xsl:when>
						<xsl:otherwise>
							<xsl:message terminate="yes">
								<xsl:text>The attribute 'required' has an illegal value: '</xsl:text>
								<xsl:value-of select="output/param/@required" />
								<xsl:text>'.</xsl:text>
							</xsl:message>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<!-- Determine the Java primary data type or class for the
		     		 input parameter -->
				<xsl:variable name="javatype">
					<xsl:call-template name="javatype_for_type">
						<xsl:with-param name="project_file" select="$project_file"      />
						<xsl:with-param name="api"          select="$api"               />
						<xsl:with-param name="specsdir"     select="$specsdir"          />
						<xsl:with-param name="required"     select="$required"          />
						<xsl:with-param name="type"         select="output/param/@type" />
					</xsl:call-template>
				</xsl:variable>

				<!-- Determine if $javatype is a Java primary data type -->
				<xsl:variable name="typeIsPrimary">
					<xsl:call-template name="is_java_datatype">
						<xsl:with-param name="text" select="$javatype" />
					</xsl:call-template>
				</xsl:variable>

				<xsl:text><![CDATA[
    *
    * @return
    *    the value of the <em>]]></xsl:text>
				<xsl:value-of select="output/param/@name" />
				<xsl:text><![CDATA[</em> parameter]]></xsl:text>
				<xsl:choose>
					<xsl:when test="$typeIsPrimary = 'false' and $required = 'true'">
						<xsl:text><![CDATA[, not <code>null</code>.]]></xsl:text>
					</xsl:when>
					<xsl:when test="$typeIsPrimary = 'false'">
						<xsl:text><![CDATA[, can be <code>null</code>.]]></xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>.</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="output/data/element">
				<xsl:text><![CDATA[
    *
    * @return
    *    a {@link org.jdom.Element#clone() clone} of the returned data
    *    element, or <code>null</code> if no data element is returned.]]></xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:text><![CDATA[
    *
    * @throws org.xins.client.CallException
    *    if the call failed for any reason.
    */
   public ]]></xsl:text>
		<xsl:value-of select="$returnType" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>

			<xsl:apply-templates select="input/param" mode="methodSignature" />

		<xsl:text>)
   throws org.xins.client.CallException {

   // Get the XINS service caller
   org.xins.client.XINSServiceCaller caller = getCaller();
</xsl:text>
		<xsl:if test="input/param">
			<xsl:text>

      // Store the input parameters in a map
      java.util.Map params = new java.util.HashMap();</xsl:text>
			<xsl:apply-templates select="input/param" mode="store" />

		</xsl:if>
		<xsl:text>
      org.xins.client.XINSServiceCaller.Result result = caller.call(</xsl:text>
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
		<xsl:text>);</xsl:text>
		<xsl:choose>
			<xsl:when test="(output/param and output/data/element) or count(output/param) &gt; 1">
				<xsl:text>
      if (result.isSuccess()) {
         return new </xsl:text>
				<xsl:value-of select="$returnType" />
				<xsl:text>(result);
      } else {
         throw new org.xins.client.UnsuccessfulCallException(result);
      }</xsl:text>
			</xsl:when>
			<xsl:when test="output/param">
				<!-- Determine if this parameter is required -->
				<xsl:variable name="required">
					<xsl:choose>
						<xsl:when test="string-length(output/param/@required) &lt; 1">false</xsl:when>
						<xsl:when test="output/param/@required = 'false'">false</xsl:when>
						<xsl:when test="output/param/@required = 'true'">true</xsl:when>
						<xsl:otherwise>
							<xsl:message terminate="yes">
								<xsl:text>The attribute 'required' has an illegal value: '</xsl:text>
								<xsl:value-of select="output/param/@required" />
								<xsl:text>'.</xsl:text>
							</xsl:message>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<xsl:text>
      if (result.isSuccess()) {
         try {
            return </xsl:text>
				<xsl:call-template name="javatype_from_string_for_type">
					<xsl:with-param name="specsdir" select="$specsdir"          />
					<xsl:with-param name="api"      select="$api"               />
					<xsl:with-param name="type"     select="output/param/@type" />
					<xsl:with-param name="required" select="$required"          />
					<xsl:with-param name="variable">
						<xsl:text>result.getParameter("</xsl:text>
						<xsl:value-of select="output/param/@name" />
						<xsl:text>")</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:text>;
         } catch (org.xins.common.types.TypeValueException exception) {
            // fall through
         }
      }
      throw new org.xins.client.UnsuccessfulCallException(result);</xsl:text>
			</xsl:when>
			<xsl:when test="output/data/element">
				<xsl:text>
      if (result.isSuccess()) {
         org.jdom.Element element = result.getDataElement();
         if (element != null) {
            return (org.jdom.Element) element.clone();
         } else {
            return null;
         }
      } else {
         throw new org.xins.client.UnsuccessfulCallException(result);
      }</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>
      if (! result.isSuccess()) {
         throw new org.xins.client.UnsuccessfulCallException(result);
      }</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
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
				<xsl:otherwise>
					<xsl:message terminate="yes">
						<xsl:text>The attribute 'required' has an illegal value: '</xsl:text>
						<xsl:value-of select="@required" />
						<xsl:text>'.</xsl:text>
					</xsl:message>
				</xsl:otherwise>
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
				<xsl:otherwise>
					<xsl:message terminate="yes">
						<xsl:text>The attribute 'required' has an illegal value: '</xsl:text>
						<xsl:value-of select="@required" />
						<xsl:text>'.</xsl:text>
					</xsl:message>
				</xsl:otherwise>
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
	<!-- Print code that will store an input parameter in a map            -->
	<!-- ***************************************************************** -->

	<xsl:template match="input/param" mode="store">
		<xsl:variable name="required">
			<xsl:choose>
				<xsl:when test="string-length(@required) &lt; 1">false</xsl:when>
				<xsl:when test="@required = 'false'">false</xsl:when>
				<xsl:when test="@required = 'true'">true</xsl:when>
				<xsl:otherwise>
					<xsl:message terminate="yes">
						<xsl:text>The attribute 'required' has an illegal value: '</xsl:text>
						<xsl:value-of select="@required" />
						<xsl:text>'.</xsl:text>
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="@required = 'false'" >
				<xsl:text>
      if (</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text> != null) {
         params.put("</xsl:text>
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
      params.put("</xsl:text>
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
