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

	<xsl:output method="text" />

	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../hungarian.xslt" />
	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />

	<xsl:template match="api">
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;</xsl:text>
		<xsl:text><![CDATA[

import java.io.IOException;
import org.xins.client.CallResult;
import org.xins.client.CallResultParser;
import org.xins.client.FunctionCaller;
import org.xins.client.InvalidCallResultException;
import org.xins.client.UnsuccessfulCallException;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Stub for <code>]]></xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text><![CDATA[</code> API.
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
				<xsl:call-template name="hungarianLower">
					<xsl:with-param name="text">
						<xsl:value-of select="$functionName" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:variable>
			<xsl:for-each select="document($functionFile)/function">
				<xsl:text><![CDATA[

   /**
    * Calls the <em>]]></xsl:text>
				<xsl:value-of select="$functionName" />
				<xsl:text><![CDATA[</em> function.
    *]]></xsl:text>
				<xsl:for-each select="input/param">
					<xsl:text>
    * @param </xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>
    *    </xsl:text>
					<xsl:call-template name="hungarianLower">
						<xsl:with-param name="text">
							<xsl:value-of select="description/text()" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:text>
    *</xsl:text>
				</xsl:for-each>
				<xsl:text><![CDATA[
    * @return
    *    the result of the call, not <code>null</code>.
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
   public CallResult ]]></xsl:text>
				<xsl:value-of select="$methodName" />
				<xsl:text>(</xsl:text>
					<xsl:for-each select="input/param">
						<xsl:if test="position() &gt; 1">
							<xsl:text>, </xsl:text>
						</xsl:if>
						<xsl:call-template name="class_for_type">
							<xsl:with-param name="type">
								<xsl:value-of select="@type" />
							</xsl:with-param>
						</xsl:call-template>
						<xsl:text> </xsl:text>
						<xsl:value-of select="@name" />
					</xsl:for-each>
				<xsl:text>)
   throws IOException, InvalidCallResultException, UnsuccessfulCallException {
      CallResult result = _functionCaller.call("</xsl:text>
				<xsl:value-of select="$functionName" />
				<xsl:text>", null); // TODO: Pass parameters if any
      return result;
   }</xsl:text>
			</xsl:for-each>
		</xsl:for-each>

		<xsl:text><![CDATA[
}
]]></xsl:text>
	</xsl:template>

	<xsl:template name="class_for_type">
		<xsl:param name="type" />
		<xsl:choose> <!-- XXX: This is a bit dirty -->
			<xsl:when test="$type = 'boolean'">
				<xsl:text>boolean</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>java.lang.String</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
