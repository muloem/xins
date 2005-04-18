<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that converts the definition of an error code to a Java file that
 represents this error code within the CAPI code.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<!-- Define parameters -->
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<!-- Perform includes -->
	<xsl:include href="../casechange.xslt" />
	<xsl:include href="../java.xslt"  />
	<xsl:include href="../types.xslt"  />
	<xsl:include href="check_params.xslt" />
	<xsl:include href="result_java.xslt" />

	<xsl:template match="resultcode">

		<xsl:variable name="resultcode" select="@name" />
		<xsl:variable name="className" select="concat($resultcode, 'Exception')" />

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text><![CDATA[;

/**
 * Exception that represents the <em>]]></xsl:text>
		   <xsl:value-of select="$resultcode" />
		<xsl:text><![CDATA[</em> error code.
 */
public final class ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[ extends org.xins.client.AbstractCAPIErrorCodeException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    *
    * <p><em>This constructor is internal to XINS. Please do not use it
    * directly. It should only be used by the generated classes. Note that
    * this constructor may be removed at any point in time.</em>
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration in milliseconds, must be &gt;= 0.
    *
    * @param resultData
    *    the result data, cannot be <code>null</code>.
    *
    * @throws java.lang.IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0
    *          || resultData  == null
    *          || resultData.{@link org.xins.client.XINSCallResult#getErrorCode() getErrorCode()} == null</code>.
    */
   public ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>(org.xins.client.XINSCallRequest            request,
                 org.xins.common.service.TargetDescriptor   target,
                 long                                       duration,
                 org.xins.client.XINSCallResultData         resultData)
   throws java.lang.IllegalArgumentException {
      super(request, target, duration, resultData);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
</xsl:text>

	</xsl:template>
</xsl:stylesheet>
