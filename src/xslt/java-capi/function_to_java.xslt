<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the Result classes for the functions that return a result.

 $Id$

 Copyright 2004 Wanadoo Nederland B.V.
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

	<xsl:variable name="version">
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision">
				<xsl:value-of select="//function/@rcsversion" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>

	<xsl:variable name="functionName" select="//function/@name" />
	<xsl:variable name="className" select="concat($functionName, 'Result')" />

	<xsl:template match="function">
		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

/**
 * Result of a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public final class ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text> extends org.xins.client.AbstractCAPICallResult {

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

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------</xsl:text>
		<xsl:apply-templates select="output/param" mode="field" />
		<xsl:if test="output/data/element">
			<xsl:text><![CDATA[

   /**
    * The result data element. This field will be <code>null</code> if no data
    * element was returned.
    */
   private final org.xins.client.DataElement _dataElement;]]></xsl:text>
		</xsl:if>
		<xsl:text>

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------</xsl:text>
		<xsl:apply-templates select="output/param" mode="method" />
		<xsl:if test="output/data/element">
			<!-- TODO: FIXME: Remove getDataElement() -->
			<xsl:text><![CDATA[

   /**
    * Gets the data element, if any. If there is no data element, then
    * <code>null</code> is returned.
    *
    * @return
    *    the data element, or <code>null</code> if there is none.
    *
    * @deprecated
    *    Deprecated since XINS 1.0.0-beta9 to avoid a clash if a parameter is
    *    called <em>dataElement</em>. Use {@link #dataElement()} instead. Note
    *    that this method will be removed before XINS 1.0.0-rc1.
    */
   public org.xins.client.DataElement getDataElement() {
      return _dataElement;
   }

   /**
    * Returns the data element, if any. If no data element (or an empty data
    * element) was returned, then <code>null</code> is returned.
    *
    * @return
    *    the data element, or <code>null</code> if there is none.
    */
   public org.xins.client.DataElement dataElement() {
      return _dataElement;
   }]]></xsl:text>
		</xsl:if>
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
    *
    * @param result
    *    the call result to construct a new
    *    <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> from, not <code>null</code>.
    *
    * @throws java.lang.IllegalArgumentException
    *    if <code>result == null || result.getErrorCode() != null</code>.
    *
    * @throws org.xins.client.UnacceptableResultXINSCallException
    *    if the specified call result is considered unacceptable as a result
    *    from the <em>]]></xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text><![CDATA[</em> function.
    */
   ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>(org.xins.client.XINSCallResult result)
   throws java.lang.IllegalArgumentException,
          org.xins.client.UnacceptableResultXINSCallException {

      // Call superconstructor, which will fail if result == null
      super(result);

      // Check preconditions
      if (result.getErrorCode() != null) {
         throw new java.lang.IllegalArgumentException("result.getErrorCode() != null");
      }

      java.lang.String currentParam = "";</xsl:text>
		<xsl:if test="output/data/element">
			<xsl:text>

      _dataElement = result.getDataElement();</xsl:text>
		</xsl:if>
		<xsl:if test="output/param">
			<xsl:text>
      try {
</xsl:text>
			<xsl:apply-templates select="output/param" mode="setfield" />
			<xsl:text>
      } catch (org.xins.common.types.TypeValueException exception) {
         org.xins.common.text.FastStringBuffer buffer = new org.xins.common.text.FastStringBuffer(203, "The parameter \"");
         buffer.append(currentParam);
         buffer.append("\" has value \"");
         buffer.append(exception.getValue());
         buffer.append("\", which is invalid for the type \"");
         buffer.append(exception.getType().getName());
         buffer.append("\".");
         throw new org.xins.client.UnacceptableResultXINSCallException(result, buffer.toString(), exception);
      }</xsl:text>
		</xsl:if>
		<xsl:text>
   }</xsl:text>
	</xsl:template>

	<xsl:template match="function/output/param" mode="setfield">
		<xsl:variable name="basetype">
			<xsl:call-template name="basetype_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="type"     select="@type"     />
			</xsl:call-template>
		</xsl:variable>
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
         currentParam = "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>";
         _</xsl:text>
		<xsl:value-of select="translate(@name, '.', '_')" />
		<xsl:text> = </xsl:text>
		<xsl:call-template name="javatype_from_string_for_type">
			<xsl:with-param name="api"      select="$api"      />
			<xsl:with-param name="specsdir" select="$specsdir" />
			<xsl:with-param name="required" select="$required" />
			<xsl:with-param name="type"     select="@type"     />
			<xsl:with-param name="variable" select="'result.getParameter(currentParam)'" />
		</xsl:call-template>
		<xsl:text>;</xsl:text>
	</xsl:template>

	<xsl:template match="function/output/param" mode="field">
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="@required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<xsl:text>

   private final </xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text> _</xsl:text>
		<xsl:value-of select="translate(@name, '.', '_')" />
		<xsl:text>;</xsl:text>
	</xsl:template>

	<xsl:template match="function/output/param" mode="method">
		<xsl:variable name="basetype">
			<xsl:call-template name="basetype_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="type"     select="@type"     />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="methodName">
			<xsl:choose>
				<xsl:when test="$basetype = '_boolean'">is</xsl:when>
				<xsl:otherwise>get</xsl:otherwise>
			</xsl:choose>
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text">
					<xsl:value-of select="translate(@name, '.', '_')" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="@required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>
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

		<xsl:text><![CDATA[

   /**
    * Gets the value of the ]]></xsl:text>
		<xsl:choose>
			<xsl:when test="$required = 'true'">
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
			<xsl:when test="not($basetype = '_text')">.</xsl:when>
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
		<xsl:value-of select="$javatype" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>() {
      return _</xsl:text>
		<xsl:value-of select="translate(@name, '.', '_')" />
		<xsl:text>;
   }</xsl:text>
	</xsl:template>

</xsl:stylesheet>

