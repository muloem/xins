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

	<xsl:include href="../hungarian.xslt" />
	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />

	<xsl:variable name="version">
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision">
				<xsl:value-of select="//function/@rcsversion" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>

	<xsl:template match="function">
		<xsl:variable name="className" select="concat(@name, 'Result')" />
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text><![CDATA[;

import org.xins.client.CallResult;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Result of a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public final class ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[ extends Object {

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
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    *
    * @param result
    *    the call result to construct a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> from, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null || result.isSuccess() == false</code>.
    */
   ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[(CallResult result)
   throws IllegalArgumentException {
      MandatoryArgumentChecker.check("result", result);
      if (!result.isSuccess()) {
         throw new IllegalArgumentException("result.isSuccess() == false");
      }

      _result = result;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The <code>CallResult</code> this object is based on. The value of this
    * field cannot be <code>null</code>.
    */
   private final CallResult _result;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------]]></xsl:text>
		<xsl:for-each select="output/param">
			<xsl:variable name="methodName">
				<xsl:choose>
					<xsl:when test="@type = 'boolean'">is</xsl:when>
					<xsl:otherwise>get</xsl:otherwise>
				</xsl:choose>
				<xsl:call-template name="hungarianUpper">
					<xsl:with-param name="text">
						<xsl:value-of select="@name" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="returnType">
				<xsl:choose>
					<xsl:when test="@type = 'boolean' and @required = 'true'">boolean</xsl:when>
					<xsl:when test="@type = 'boolean'">java.lang.Boolean</xsl:when>
					<xsl:otherwise>java.lang.String</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:text><![CDATA[

   /**
    * Gets the value of the ]]></xsl:text>
			<xsl:choose>
				<xsl:when test="@required = 'true'">
					<xsl:text>required</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>optional</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text><![CDATA[ output parameter <em>]]></xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text><![CDATA[</em>.
    *
    * @return
    *    the value of the <em>]]></xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text><![CDATA[</em> output parameter]]></xsl:text>
			<xsl:choose>
				<xsl:when test="$returnType = 'boolean'">.</xsl:when>
				<xsl:when test="@required = 'true'">
					<xsl:text><![CDATA[, never <code>null</code>.]]></xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[, or <code>null</code> if the parameter is not set.]]></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text><![CDATA[
    */
   public ]]></xsl:text>
			<xsl:value-of select="$returnType" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="$methodName" />
			<xsl:text>() {
      String value = _result.getParameter("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>");</xsl:text>
			<xsl:choose>
				<xsl:when test="$returnType = 'boolean'">
					<xsl:text>
      return "true".equals(value);
					</xsl:text>
				</xsl:when>
				<xsl:when test="$returnType = 'java.lang.Boolean'">
					<xsl:text>
      if (value == null) {
         return null;
      } else {
         return "true".equals(value) ? new Boolean(true) : new Boolean(false);
      }</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>
      return value;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>
   }</xsl:text>
		</xsl:for-each>
		<xsl:text>
}
</xsl:text>
	</xsl:template>

</xsl:stylesheet>

