<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->
<!-- This stylesheet generates the result code used in the
     functions of the api.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../hungarian.xslt" />
	<xsl:include href="../java.xslt" />

	<xsl:variable name="resultcode" select="//resultcode/@name" />
	<xsl:variable name="className">
		<xsl:call-template name="hungarianUpper">
			<xsl:with-param name="text">
				<xsl:value-of select="$resultcode" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>

	<xsl:template match="resultcode">
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text><![CDATA[;

/**
 */
final class ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text> implements </xsl:text>
		<xsl:for-each select="//api/function">
			<xsl:variable name="functionName"    select="@name" />
			<xsl:variable name="functionFile"    select="concat($specsdir, '/', $api, '/', $functionName, '.fnc')" />
			<xsl:for-each select="document($functionFile)/resultcode-ref">
				<xsl:if test="@name = $resultcode">
					<xsl:if test="not(position() = 1)">, </xsl:if>
					<xsl:value-of select="@name" />
					<xsl:text>.UnsuccessfulResult</xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:text> {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------</xsl:text>
		<xsl:call-template name="constructor" />
		<xsl:text>

}
</xsl:text>
	</xsl:template>

	<xsl:template name="constructor">
		<xsl:text><![CDATA[
   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    */
   ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>() {
   }</xsl:text>
	</xsl:template>

</xsl:stylesheet>