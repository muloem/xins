<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="xins_home"    />
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<!-- Determine if this API is session-based -->
	<xsl:variable name="sessionBased">
		<xsl:choose>
			<xsl:when test="boolean(//api/session-based)">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- If this API is session-based, determine if sessions are shared among
	     different instances of the API -->
	<xsl:variable name="sessionsShared">
		<xsl:choose>
			<xsl:when test="$sessionBased = 'true' and //api/session-based/@shared-sessions = 'true'">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<!-- If this API is session-based, determine the Java type for session
	     identifiers, either a Java primary data type (byte, short, int, char,
	     etc.) or a Java class (java.lang.String, etc.) -->
	<xsl:variable name="sessionIDJavaType">
		<xsl:choose>
			<xsl:when test="$sessionBased = 'true' and $sessionsShared = 'false'">
				<xsl:text>java.lang.String</xsl:text>
			</xsl:when>
			<xsl:when test="$sessionBased = 'true'">
				<xsl:call-template name="javatype_for_type">
					<xsl:with-param name="api"      select="$api"      />
					<xsl:with-param name="specsdir" select="$specsdir" />
					<xsl:with-param name="required" select="'true'"    />
					<xsl:with-param name="type"     select="_text"     />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:variable>

	<!-- For the sessionIDJavaType, determine if this is a Java primary data
	     type or a Java class. -->
	<xsl:variable name="sessionIDJavaTypeIsPrimary">
		<xsl:if test="$sessionBased = 'true'">
			<xsl:call-template name="is_java_datatype">
				<xsl:with-param name="text" select="$sessionIDJavaType" />
			</xsl:call-template>
		</xsl:if>
	</xsl:variable>

	<!-- Determine the location of the online specification docs -->
	<xsl:variable name="specdocsURL">
		<xsl:value-of select="document($project_file)/project/specdocs/@href" />
	</xsl:variable>

	<!-- Output is text/plain -->
	<xsl:output method="text" />

	<!-- Perform includes -->
	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../function.xslt"   />
	<xsl:include href="../java.xslt"       />
	<xsl:include href="../rcs.xslt"        />
	<xsl:include href="../types.xslt"      />

	<!-- Match the root element: api -->
	<xsl:template match="api">
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;</xsl:text>
		<xsl:call-template name="imports" />

<xsl:text><![CDATA[

/**
 * Stub for the <em>]]></xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text><![CDATA[</em> API.
 *
 * <p>See the <a href="]]></xsl:text>
		<xsl:value-of select="$specdocsURL" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text><![CDATA[/">API specification</a>.
 */
public final class API extends Object {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

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
   //-------------------------------------------------------------------------

   /**
    * The remote API. This field cannot be <code>null</code>.
    */
   private final FunctionCaller _functionCaller;]]></xsl:text>
		<xsl:if test="$sessionBased = 'true' and $sessionsShared = 'false'">
			<xsl:text><![CDATA[

   /**
    * The client-side session identifier splitter. Cannot be
    * <code>null</code>.
    */
   private final SessionIDSplitter _sessionIDSplitter;]]></xsl:text>
		</xsl:if>
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
				<xsl:value-of select="$api" />
				<xsl:text>/</xsl:text>
				<xsl:value-of select="$functionName" />
				<xsl:text>.fnc</xsl:text>
			</xsl:variable>
			<xsl:apply-templates select="document($functionFile)/function">
				<xsl:with-param name="name" select="$functionName" />
			</xsl:apply-templates>
		</xsl:for-each>

		<xsl:text><![CDATA[
}
]]></xsl:text>
	</xsl:template>

	<!-- Print the list of Java import statements -->
	<xsl:template name="imports">
		<xsl:text>

import java.util.HashMap;
import java.util.Map;
import org.xins.client.ActualFunctionCaller;
import org.xins.client.CallResult;
import org.xins.client.CallResultParser;
import org.xins.client.FunctionCaller;
import org.xins.client.SessionIDSplitter;
import org.xins.util.MandatoryArgumentChecker;</xsl:text>
	</xsl:template>

	<!-- Print the constructor -->
	<xsl:template name="constructor">
		<xsl:choose>
			<xsl:when test="$sessionBased = 'true' and $sessionsShared = 'false'">
				<xsl:text><![CDATA[

   /**
    * Constructs a new <code>API</code> object for the specified remote API.
    *
    * @param functionCaller
    *    the function caller, cannot be <code>null</code>.
    *
    * @param sessionIDSplitter
    *    splitter that converts a client-side session identifier to a target
    *    API checksum and a target API-specific session ID.</xsl:text>
    *
    * @throws IllegalArgumentException
    *    if <code>functionCaller == null || sessionIDSplitter == null</code>.
    */
   public API(FunctionCaller functionCaller, SessionIDSplitter sessionIDSplitter)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionCaller",    functionCaller,
                                     "sessionIDSplitter", sessionIDSplitter);

      // Store data
      _functionCaller    = functionCaller;
      _sessionIDSplitter = sessionIDSplitter;</xsl:text>
   }]]></xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text><![CDATA[

   /**
    * Constructs a new <code>API</code> object for the specified remote API.
    *
    * @param functionCaller
    *    the function caller, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionCaller == null</code>.
    */
   public API(FunctionCaller functionCaller)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionCaller", functionCaller);

      // Store data
      _functionCaller = functionCaller;
   }]]></xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Print a method to call a single function -->
	<xsl:template match="function">

		<!-- Define parameters -->
		<xsl:param name="name" />

		<!-- Determine the name of the method in the API class that will call
		     this function. -->
		<xsl:variable name="methodName">
			<xsl:text>call</xsl:text>
			<xsl:value-of select="$name" />
		</xsl:variable>

		<!-- Determine if this function is session-based. -->
		<xsl:variable name="sessionBased">
			<xsl:call-template name="is_function_session_based" />
		</xsl:variable>

		<!-- Determine the return type of the method, either a Java primary
		     data type or a Java class name. -->
		<xsl:variable name="returnType">
			<xsl:choose>
				<xsl:when test="@createsSession = 'true' and $sessionsShared = 'true'">
					<xsl:text>java.lang.String</xsl:text>
				</xsl:when>
				<xsl:when test="@createsSession = 'true'">
					<xsl:text>org.xins.client.NonSharedSession</xsl:text>
				</xsl:when>
				<xsl:when test="count(output/param) = 1 and count(output/data/element) = 0">
					<xsl:call-template name="javatype_for_type">
						<xsl:with-param name="api"      select="$api"                   />
						<xsl:with-param name="specsdir" select="$specsdir"              />
						<xsl:with-param name="required" select="output/param/@required" />
						<xsl:with-param name="type"     select="output/param/@type"     />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="output/param or output/data/element">
					<xsl:value-of select="$name" />
					<xsl:text>Result</xsl:text>
				</xsl:when>
				<xsl:otherwise>void</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- Check name set in function definition file -->
		<xsl:if test="string-length(@name) &gt; 0 and not($name = @name)">
			<xsl:message terminate="yes">Name in function definition file differs from name defined in API definition file. Removing the attribute in the function definition file should solve this problem.</xsl:message>
		</xsl:if>

		<xsl:text><![CDATA[

   /**
    * Calls the <em>]]></xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text><![CDATA[</em> function.
    *
    * <p>See the <a href="]]></xsl:text>
		<xsl:value-of select="$specdocsURL" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text>.html</xsl:text>
		<xsl:text><![CDATA["><em>]]></xsl:text>
		<xsl:value-of select="$name" />
		<xsl:text><![CDATA[</em> function specification</a>.]]></xsl:text>
		<xsl:choose>
			<xsl:when test="$sessionBased = 'true' and $sessionsShared = 'false'">
				<xsl:text><![CDATA[
    *
    * @param session
    *    the client-side session, cannot be <code>null</code>.]]></xsl:text>
			</xsl:when>
			<xsl:when test="$sessionBased = 'true'">
				<xsl:text>
    *
    * @param session
    *    the session identifier</xsl:text>
				<xsl:if test="$sessionIDJavaTypeIsPrimary = 'false'">
					<xsl:text><![CDATA[, cannot be <code>null</code>]]></xsl:text>
				</xsl:if>
				<xsl:text>.</xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:apply-templates select="input/param" mode="javadoc" />
		<xsl:choose>
			<xsl:when test="@createsSession = 'true' and $sessionsShared = 'true'">
				<xsl:text><![CDATA[
    *
    * @return
    *    the shared session identifier, not <code>null</code>.]]></xsl:text>
			</xsl:when>
			<xsl:when test="@createsSession = 'true'">
				<xsl:text><![CDATA[
    *
    * @return
    *    the non-shared session (not <code>null</code>), a combination of the
    *    identifier of the created session and a link to the function caller
    *    that actually created the session.]]></xsl:text>
			</xsl:when>
			<xsl:when test="count(output/param) = 1 and count(output/data/element) = 0">
				<xsl:text><![CDATA[
    *
    * @return
    *    the value of the <em>]]></xsl:text>
				<xsl:value-of select="output/param/@name" />
				<!-- TODO: Can it not be null? And what if it is a Java basic data type such as boolean, char, short or int? -->
				<xsl:text><![CDATA[</em> parameter, not <code>null</code>.]]></xsl:text>
			</xsl:when>
			<xsl:when test="output/param or output/data/element">
				<xsl:text><![CDATA[
    *
    * @return
    *    the result, not <code>null</code>.]]></xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:text><![CDATA[
    *
    * @throws java.io.IOException
    *    if there was an I/O error.
    *
    * @throws org.xins.client.InvalidCallResultException
    *    if the call to the API resulted in an invalid response, either
    *    invalid XML or invalid as a XINS result document.
    *
    * @throws org.xins.client.UnsuccessfulCallException
    *    if the call was unsuccessful; in some cases this may be determined
    *    locally already.
    */
   public ]]></xsl:text>
		<xsl:value-of select="$returnType" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>
			<xsl:if test="$sessionBased = 'true'">
				<xsl:value-of select="$sessionIDJavaType" />
				<xsl:text> session</xsl:text>
			</xsl:if>

			<xsl:for-each select="input/param">
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
				<xsl:variable name="javatype">
					<xsl:call-template name="javatype_for_type">
						<xsl:with-param name="api"      select="$api"      />
						<xsl:with-param name="specsdir" select="$specsdir" />
						<xsl:with-param name="required" select="$required" />
						<xsl:with-param name="type"     select="@type" />
					</xsl:call-template>
				</xsl:variable>

				<xsl:if test="$sessionBased = 'true' or position() &gt; 1">
					<xsl:text>, </xsl:text>
				</xsl:if>
				<xsl:value-of select="$javatype" />
				<xsl:text> </xsl:text>
				<xsl:value-of select="@name" />
			</xsl:for-each>
		<xsl:text>)
   throws </xsl:text>
		<xsl:if test="$sessionBased = 'true' and $sessionsShared = 'false'">
			<xsl:text>org.xins.types.TypeValueException, </xsl:text>
		</xsl:if>
		<xsl:text>java.io.IOException,
          org.xins.client.InvalidCallResultException,
          org.xins.client.UnsuccessfulCallException {</xsl:text>
		<xsl:if test="$sessionBased = 'true' and $sessionsShared = 'false'">
 			<!-- TODO: Improve performance by caching the result array -->
			<xsl:text>

      // Split the client-side session ID
      String[] arr = new String[2];
      _sessionIDSplitter.splitSessionID(session, arr);
      ActualFunctionCaller afc = _functionCaller.getActualFunctionCallerByCRC32(arr[0]);
      session = arr[1];</xsl:text>
		</xsl:if>
		<xsl:if test="input/param">
			<xsl:text>

      // Store the input parameters in a map
      Map params = new HashMap();</xsl:text>
			<xsl:for-each select="input/param">
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
			</xsl:for-each>
		</xsl:if>
		<xsl:text>
      CallResult result = </xsl:text>
		<xsl:choose>
			<xsl:when test="$sessionBased = 'true' and $sessionsShared = 'false'">
				<xsl:text>afc</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>_functionCaller</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>.call(</xsl:text>
		<xsl:choose>
			<xsl:when test="$sessionBased = 'true' and $sessionsShared = 'false'">
				<!-- TODO: Split the session ID -->
				<xsl:text>session, </xsl:text>
			</xsl:when>
			<xsl:when test="$sessionBased = 'true'">
				<!-- TODO: Convert the session ID correctly to a String -->
				<xsl:text>session, </xsl:text>
			</xsl:when>
		</xsl:choose>
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
			<xsl:when test="@createsSession = 'true'">
				<xsl:text>
      if (result.isSuccess()) {
         String session = result.getParameter("_session");
         if (session == null) {
            throw new org.xins.client.InvalidCallResultException("The call to function \"</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>\" returned no session ID.");
         }</xsl:text>
				<xsl:choose>
					<xsl:when test="$sessionsShared = 'true'">
						<xsl:text>
         return session;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>
         return new org.xins.client.NonSharedSession(result.getFunctionCaller(), session);</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>
      } else {
         throw new org.xins.client.UnsuccessfulCallException(result);
      }</xsl:text>
			</xsl:when>
			<xsl:when test="count(output/param) = 1 and count(output/data/element) = 0">
				<!-- TODO: Return type-specific result, not always String! -->
				<xsl:text>
      if (result.isSuccess()) {
         return result.getParameter("</xsl:text>
				<xsl:value-of select="output/param/@name" />
				<xsl:text>");
      } else {
         throw new org.xins.client.UnsuccessfulCallException(result);
      }</xsl:text>
			</xsl:when>
			<xsl:when test="output/param or output/data/element">
				<xsl:text>
      if (result.isSuccess()) {
         return new </xsl:text>
				<xsl:value-of select="$returnType" />
				<xsl:text>(result);
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

	<!-- Prints an @param section for an input parameter. -->
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
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="required" select="$required" />
				<xsl:with-param name="type"     select="@type" />
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
</xsl:stylesheet>
