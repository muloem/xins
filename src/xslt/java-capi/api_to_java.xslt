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
	<xsl:variable name="sessionBased">
		<xsl:choose>
			<xsl:when test="boolean(//api/session-based)">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="sessionsShared">
		<xsl:if test="$sessionBased = 'true'">
			<xsl:choose>
				<xsl:when test="//api/session-based/@shared-sessions = 'true'">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:variable>
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
	<xsl:variable name="sessionIDJavaTypeIsPrimary">
		<xsl:if test="$sessionBased = 'true'">
			<xsl:call-template name="is_java_datatype">
				<xsl:with-param name="text" select="$sessionIDJavaType" />
			</xsl:call-template>
		</xsl:if>
	</xsl:variable>

	<xsl:output method="text" />

	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../function.xslt"   />
	<xsl:include href="../java.xslt"       />
	<xsl:include href="../rcs.xslt"        />
	<xsl:include href="../types.xslt"      />

	<xsl:template match="api">
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;</xsl:text>
		<xsl:text><![CDATA[

import java.util.HashMap;
import java.util.Map;
import org.xins.client.ActualFunctionCaller;
import org.xins.client.CallResult;
import org.xins.client.CallResultParser;
import org.xins.client.FunctionCaller;
import org.xins.client.SessionIDSplitter;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Stub for the <em>]]></xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text><![CDATA[</em> API.
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
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>API</code> object for the specified remote API.
    *
    * @param functionCaller
    *    the function caller, cannot be <code>null</code>.]]></xsl:text>
		<xsl:if test="$sessionBased = 'true' and $sessionsShared = 'false'">
			<xsl:text>
    *
    * @param sessionIDSplitter
    *    splitter that converts a client-side session identifier to a target
    *    API checksum and a target API-specific session ID.</xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[
    *
    * @throws IllegalArgumentException
    *    if <code>functionCaller == null]]></xsl:text>
		<xsl:if test="$sessionBased = 'true' and $sessionsShared = 'false'">
			<xsl:text> || sessionIDSplitter == null</xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[</code>.
    */
   public API(FunctionCaller functionCaller]]></xsl:text>
		<xsl:if test="$sessionBased = 'true' and $sessionsShared = 'false'">
			<xsl:text>, SessionIDSplitter sessionIDSplitter</xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionCaller", functionCaller]]></xsl:text>
		<xsl:if test="$sessionBased = 'true' and $sessionsShared = 'false'">
			<xsl:text>, "sessionIDSplitter", sessionIDSplitter</xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[);

      // Store data
      _functionCaller = functionCaller;]]></xsl:text>
		<xsl:if test="$sessionBased = 'true' and $sessionsShared = 'false'">
			<xsl:text>
      _sessionIDSplitter = sessionIDSplitter;</xsl:text>
		</xsl:if>
		<xsl:text><![CDATA[
   }


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

		<xsl:for-each select="function">
			<xsl:variable name="functionName" select="@name" />
			<xsl:variable name="functionFile" select="concat($specsdir, '/', $api, '/', $functionName, '.fnc')" />
			<xsl:variable name="methodName">
				<xsl:text>call</xsl:text>
				<xsl:value-of select="$functionName" />
			</xsl:variable>
			<xsl:for-each select="document($functionFile)/function">
				<xsl:variable name="sessionBased">
					<xsl:call-template name="is_function_session_based" />
				</xsl:variable>
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
							<xsl:value-of select="$functionName" />
							<xsl:text>Result</xsl:text>
						</xsl:when>
						<xsl:otherwise>void</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:text><![CDATA[

   /**
    * Calls the <em>]]></xsl:text>
				<xsl:value-of select="$functionName" />
				<xsl:text><![CDATA[</em> function.]]></xsl:text>
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
					<xsl:variable name="typeIsPrimary">
						<xsl:call-template name="is_java_datatype">
							<xsl:with-param name="text" select="$javatype" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:text>
    *
    * @param </xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>
    *    </xsl:text>
					<xsl:call-template name="hungarianLower">
						<xsl:with-param name="text">
							<!-- TODO: Improve this -->
							<xsl:value-of select="description/text()" />
						</xsl:with-param>
					</xsl:call-template>
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
				</xsl:for-each>
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
				<xsl:value-of select="$functionName" />
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
			</xsl:for-each>
		</xsl:for-each>

		<xsl:text><![CDATA[
}
]]></xsl:text>
	</xsl:template>
</xsl:stylesheet>
