<?xml version="1.0" encoding="US-ASCII"?>
<!--
 XSLT that generates the Result classes.

 $Id$

 Copyright 2003-2008 Online Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="generics"     />

	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />
	<xsl:include href="../types.xslt"  />
	<xsl:include href="../xml_to_java.xslt"  />
	<xsl:include href="../java-server-framework/check_params.xslt"  />
	<xsl:include href="../java-server-framework/request_java.xslt"  />

	<xsl:variable name="project_node" select="document($project_file)/project" />

	<xsl:template match="function">
		<xsl:variable name="project_node" select="document($project_file)/project" />
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision" select="//function/@rcsversion" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="functionName" select="@name" />
		<xsl:variable name="className" select="concat($functionName, 'Result')" />

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

/**
 * Result of a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 *
 * @see CAPI
 * @see ]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>Request
 */
public final class </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>
extends org.xins.client.AbstractCAPICallResult {</xsl:text>

		<xsl:apply-templates select="output/param" mode="field" />
		<xsl:if test="output/data/element">
			<xsl:text><![CDATA[

   /**
    * The result data element. This field will be <code>null</code> if no data
    * element was returned.
    */
   private final org.xins.common.xml.Element _dataElement;]]></xsl:text>
		</xsl:if>

		<xsl:call-template name="constructor">
			<xsl:with-param name="className" select="$className" />
		</xsl:call-template>

		<xsl:apply-templates select="output/param" mode="method" />

		<xsl:if test="output/data/element">
			<xsl:text><![CDATA[

   /**
    * Returns the data element, if any. If no data element (or an empty data
    * element) was returned, then <code>null</code> is returned.
    *
    * @return
    *    the data element, or <code>null</code> if there is none.
    */
   public org.xins.common.xml.Element dataElement() {
      return _dataElement;
   }]]></xsl:text>
		</xsl:if>

		<xsl:if test="output/data/@contains">
			<xsl:variable name="elementName" select="output/data/@contains" />
			<xsl:apply-templates select="output/data/element[@name=$elementName]" mode="listMethod" />
		</xsl:if>
		<xsl:for-each select="output/data/contains/contained">
			<xsl:variable name="elementName" select="@element" />
			<xsl:apply-templates select="../../element[@name=$elementName]" mode="listMethod" />
		</xsl:for-each>

		<xsl:text><![CDATA[

   /**
    * Validates whether this result is considered acceptable. If any
    * constraints are violated, then an
    * {@link org.xins.client.UnacceptableRequestException UnacceptableRequestException}
    * is returned.
    *
    * @param result
    *    the returned result from the server, never <code>null</code>.
    *
    * @return
    *    an
    *    {@link org.xins.client.UnacceptableRequestException UnacceptableRequestException}
    *    instance if this result is considered unacceptable, otherwise
    *    <code>null</code>.
    */
   private org.xins.client.UnacceptableMessageException checkParameters(org.xins.client.XINSCallResult _result) {
]]></xsl:text>
		<xsl:apply-templates select="output" mode="checkParams">
			<xsl:with-param name="side" select="'client'" />
		</xsl:apply-templates>
		<xsl:if test="not(output)">
			<xsl:text>
      return null;</xsl:text>
		</xsl:if>
		<xsl:text>
   }
</xsl:text>
		<xsl:apply-templates select="output/data/element" mode="listElementClass" />
		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template name="constructor">
		<xsl:param name="className" />

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
    * @throws IllegalArgumentException
    *    if <code>result == null || result.getErrorCode() != null</code>.
    *
    * @throws org.xins.client.UnacceptableResultXINSCallException
    *    if the specified call result is considered unacceptable as a result
    *    from the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
    */
   ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>(org.xins.client.XINSCallResult result)
   throws IllegalArgumentException,
          org.xins.client.UnacceptableResultXINSCallException {

      // Call superconstructor, which will fail if result == null
      super(result);

      org.xins.client.UnacceptableMessageException invalidResult = checkParameters(result);
      if (invalidResult != null) {
          throw new org.xins.client.UnacceptableResultXINSCallException(result, invalidResult.getMessage(), null);
      }</xsl:text>

		<xsl:if test="output/param">
			<xsl:text>

      String currentParam = "";
      String paramValue = "";</xsl:text>
		</xsl:if>
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
      } catch (IllegalArgumentException exception) {
         String details = "The parameter \"" + currentParam + "\" is not set although it is required.";
         throw new org.xins.client.UnacceptableResultXINSCallException(result, details, exception);

      } catch (org.xins.common.types.TypeValueException exception) {
         String details = "The parameter \"" + currentParam + "\" has value \""
               + exception.getValue() + "\", which is invalid for the type \""
               + exception.getType().getName() + "\".";
         throw new org.xins.client.UnacceptableResultXINSCallException(result, details, exception);
      }</xsl:text>
        </xsl:if>
		<xsl:text>
   }</xsl:text>
	</xsl:template>

	<xsl:template match="function/output/param" mode="setfield">
		<xsl:variable name="requiredOrDefault">
			<xsl:choose>
				<xsl:when test="@required = 'true' or @default">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- The name of the variable used in code for this parameter -->
		<xsl:variable name="javaVariable">
			<xsl:call-template name="hungarianLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<!-- The text passed to the string to type method -->
		<xsl:variable name="paramValue">
			<xsl:choose>
				<xsl:when test="@default">
					<xsl:text>paramValue == null ? "</xsl:text>
					<xsl:call-template name="xml_to_java_string">
						<xsl:with-param name="text" select="@default" />
					</xsl:call-template>
					<xsl:text>" : paramValue</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>paramValue</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>
         currentParam = "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>";
         paramValue = result.getParameter(currentParam);</xsl:text>
			<xsl:if test="@default">
				<xsl:text>
         if (paramValue == null) {
            paramValue = &quot;</xsl:text>
				<xsl:call-template name="xml_to_java_string">
					<xsl:with-param name="text" select="@default" />
				</xsl:call-template>
				<xsl:text>&quot;;
         }</xsl:text>
			</xsl:if>
			<xsl:text>
         _</xsl:text>
		<xsl:value-of select="$javaVariable" />
		<xsl:text> = </xsl:text>
		<xsl:call-template name="javatype_from_string_for_type">
			<xsl:with-param name="project_node" select="$project_node" />
			<xsl:with-param name="api"          select="$api"      />
			<xsl:with-param name="specsdir"     select="$specsdir" />
			<xsl:with-param name="required"     select="$requiredOrDefault" />
			<xsl:with-param name="type"         select="@type"     />
			<xsl:with-param name="variable"     select="$paramValue" />
		</xsl:call-template>
		<xsl:text>;</xsl:text>
	</xsl:template>

	<xsl:template match="function/output/param" mode="field">
		<xsl:variable name="requiredOrDefault">
			<xsl:choose>
				<xsl:when test="@required = 'true' or @default">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="$requiredOrDefault" />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>

		<!-- The name of the variable used in code for this parameter -->
		<xsl:variable name="javaVariable">
			<xsl:call-template name="hungarianLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:text>

   private </xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text> _</xsl:text>
		<xsl:value-of select="$javaVariable" />
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
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="requiredOrDefault">
			<xsl:choose>
				<xsl:when test="@required = 'true' or @default">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="$requiredOrDefault" />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>
		<!-- The name of the variable used in code for this parameter -->
		<xsl:variable name="javaVariable">
			<xsl:call-template name="hungarianLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
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
			<xsl:when test="not($basetype = '_text')">.</xsl:when>
			<xsl:when test="$requiredOrDefault = 'true'">
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
		<xsl:value-of select="$javaVariable" />
		<xsl:text>;
   }</xsl:text>
	</xsl:template>

</xsl:stylesheet>

