<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the Request classes.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />
	<xsl:include href="../types.xslt"  />

	<xsl:template match="function">
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision">
					<xsl:value-of select="//function/@rcsversion" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="functionName" select="@name" />
		<xsl:variable name="className" select="concat($functionName,'Request')" />

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

/**
 * Request for a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public final class ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[
extends org.xins.client.AbstractCAPICallRequest {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    */
   public static final ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[ create() {
      return new ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------
		
   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    */
   private ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>() {
      _request = new org.xins.client.XINSCallRequest("</xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text><![CDATA[");
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The underlying XINS call request. Initialized to a non-<code>null</code>
    * value in the constructor.
    */
   private final org.xins.client.XINSCallRequest _request;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Assigns the specified call configuration to this request.
    *
    * @param config
    *    the call configuration to apply when executing this request, or
    *    <code>null</code> if no specific call configuration should be
    *    associated with this request.
    */
   public void configure(org.xins.client.XINSCallConfig config) {
      _request.setXINSCallConfig(config);
   }

   /**
    * Retrieves the call configuration currently associated with this request.
    *
    * @return
    *    the call configuration currently associated with this request, which
    *    will be applied when executing this request, or <code>null</code> if
    *    no specific call configuration is associated with this request.
    */
   public org.xins.client.XINSCallConfig configuration() {
      return _request.getXINSCallConfig();
   }

   /**
    * Validates whether this request is considered acceptable (implementation
    * method). If required parameters are missing or if certain parameter
    * values are out of bounds, then an exception is thrown.
    *
    * <p>This method is called by {@link #validate()}. It should not be called
    * from anywhere else.
    *
    * @throws org.xins.client.UnacceptableRequestException
    *    if this request is considered unacceptable.
    */
   public void validateImpl()
   throws org.xins.client.UnacceptableRequestException {
      // TODO
   }
}
]]></xsl:text>
	</xsl:template>
</xsl:stylesheet>
