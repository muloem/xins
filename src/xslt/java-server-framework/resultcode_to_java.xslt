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
	<xsl:variable name="className" select="concat($resultcode, 'Result')" />

	<xsl:template match="resultcode">
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text>;

/**
 * UnsuccessfulResult due to a </xsl:text>
		<xsl:value-of select="$resultcode" />
		<xsl:text>.
 */
final class </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text> implements </xsl:text>
		<!-- This class should implements the UnsuccessfulResult from all the functions
		     that reference to this result code. -->
		<xsl:variable name="resultcodeIncludes">
			<xsl:for-each select="document($api_file)/api/function">
				<xsl:call-template name="search-matching-resultcode">
					<xsl:with-param name="functionName" select="@name" />
				</xsl:call-template>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="resultcodeIncludes2"    select="substring($resultcodeIncludes, 2)" />
		<xsl:value-of select="$resultcodeIncludes2" />

		<xsl:text> {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------
</xsl:text>
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

	<xsl:template name="search-matching-resultcode">
		<!-- Define parameters -->
		<xsl:param name="functionName" />

		<!-- Determine file that defines type -->
		<xsl:variable name="functionFile"    select="concat($specsdir, '/', $api, '/', $functionName, '.fnc')" />

		<xsl:for-each select="document($functionFile)/function/output/resultcode-ref">
			<xsl:if test="@name = $resultcode">
				<xsl:text>, </xsl:text>
				<xsl:value-of select="$functionName" />
				<xsl:text>.UnsuccessfulResult</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>