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
	<xsl:variable name="sessionIDJavaType">
		<xsl:if test="$sessionBased = 'true'">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="required" select="'true'"    />
				<xsl:with-param name="type"     select="_text"     />
			</xsl:call-template>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="sessionIDJavaTypeIsPrimary">
		<xsl:if test="$sessionBased = 'true'">
			<xsl:call-template name="is_java_datatype">
				<xsl:with-param name="text" select="$sessionIDJavaType" />
			</xsl:call-template>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="sessionsShared">
		<xsl:if test="$sessionBased = 'true'">
			<xsl:choose>
				<xsl:when test="//api/session-based/@shared-sessions = 'true'">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.xins.client.CallResult;
import org.xins.client.CallResultParser;
import org.xins.client.FunctionCaller;
import org.xins.client.InvalidCallResultException;
import org.xins.client.UnsuccessfulCallException;
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
    *    the function caller, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionCaller == null</code>.
    */
   public API(FunctionCaller functionCaller) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionCaller", functionCaller);

      // Store data
      _functionCaller = functionCaller;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The remote API. This field cannot be <code>null</code>.
    */
   private final FunctionCaller _functionCaller;


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
						<xsl:when test="output/param">
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
				<xsl:if test="$sessionBased = 'true'">
					<xsl:text>
    *
    * @param session
    *    the session identifier</xsl:text>
					<xsl:if test="$sessionIDJavaTypeIsPrimary = 'false'">
						<xsl:text><![CDATA[, cannot be <code>null</code>]]></xsl:text>
					</xsl:if>
					<xsl:text>.</xsl:text>
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
					<xsl:when test="output/param">
						<xsl:text><![CDATA[
    *
    * @return
    *    the result, not <code>null</code>.]]></xsl:text>
					</xsl:when>
				</xsl:choose>
				<xsl:text><![CDATA[
    *
    * @throws IOException
    *    if there was an I/O error.
    *
    * @throws InvalidCallResultException
    *    if the call to the API resulted in an invalid response, either
    *    invalid XML or invalid as a XINS result document.
    *
    * @throws UnsuccessfulCallException
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
   throws IOException, InvalidCallResultException, UnsuccessfulCallException {</xsl:text>
				<xsl:if test="input/param">
					<xsl:text>
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
      </xsl:text>
				<xsl:if test="output/param or @createsSession = 'true'">
					<xsl:text>CallResult result = </xsl:text>
				</xsl:if>
				<xsl:text>_functionCaller.call(</xsl:text>
				<xsl:if test="$sessionBased = 'true'">
					<!-- TODO: Convert the session ID correctly to a String -->
					<xsl:text>session, </xsl:text>
				</xsl:if>
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
      String session = result.getParameter("_session");
      if (session == null) {
         throw new InvalidCallResultException("The call to function \"</xsl:text>
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

					</xsl:when>
					<xsl:when test="output/param">
						<xsl:text>
      if (result.isSuccess()) {
         return new </xsl:text>
						<xsl:value-of select="$returnType" />
						<xsl:text>(result);
      } else {
         throw new UnsuccessfulCallException(result);
      }</xsl:text>
					</xsl:when>
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
