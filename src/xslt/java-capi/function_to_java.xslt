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

	<xsl:include href="../function.xslt" />
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
		<!-- TODO: Link to online specdocs ? -->
		<xsl:text><![CDATA[;

import java.util.Collections;
import java.util.List;
import org.jdom.Element;
import org.xins.client.CallResult;
import org.xins.client.InvalidCallResultException;
import org.xins.types.TypeValueException;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.collections.CollectionUtils;

/**
 * Result of a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public final class ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text> extends Object {

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
			<xsl:text>

   private final List _dataElements;</xsl:text>
		</xsl:if>
		<xsl:text>

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------</xsl:text>
		<xsl:apply-templates select="output/param" mode="method" />
		<xsl:if test="output/data/element">
			<xsl:text><![CDATA[

   /**
    * Gets the elements within the data section. If there are none, then an
    * empty {@link List} is returned. All elements in the list are instances
    * of class {@link Element}.
    *
    * @return
    *    the {@link List} of elements within the data section, never
    *    <code>null</code>.
    */
   public List getDataElements() {
      return _dataElements;
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
    * @throws IllegalArgumentException
    *    if <code>result == null || result.isSuccess() == false</code>.
    *
    * @throws InvalidCallResultException
    *    if the specified call result is not valid as a result from the
    *    <em>]]></xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text><![CDATA[</em> function.
    */
   ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>(CallResult result)
   throws IllegalArgumentException, InvalidCallResultException {
      if (result == null) {
         throw new IllegalArgumentException("result == null");
      } else if (!result.isSuccess()) {
         throw new IllegalArgumentException("result.isSuccess() == false");
      }
      String currentParam = "";</xsl:text>
		<xsl:if test="output/data/element">
			<xsl:text>

      Element data = result.getDataElement();
      if (data == null) {
         _dataElements = CollectionUtils.EMPTY_LIST;
      } else {
         List children = data.getChildren();
         if (children == null || children.size() &lt; 1) {
            _dataElements = CollectionUtils.EMPTY_LIST;
         } else {
            _dataElements = Collections.unmodifiableList(children);
         }
      }</xsl:text>
		</xsl:if>
		<xsl:if test="output/param">
			<xsl:text>
      try {
</xsl:text>
			<xsl:apply-templates select="output/param" mode="setfield" />
			<xsl:text> 
      } catch (TypeValueException exception) {
         throw new InvalidCallResultException("The parameter \"" + currentParam + "\" has value \"" + exception.getValue() + "\", which is invalid for the type \"" + exception.getType().getName() + "\".");
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
		<xsl:value-of select="@name" />
		<xsl:text> = </xsl:text>
		<xsl:call-template name="javatype_from_string_for_type">
			<xsl:with-param name="api"      select="$api" />
			<xsl:with-param name="specsdir" select="$api" />
			<xsl:with-param name="required" select="$required" />
			<xsl:with-param name="type"     select="$basetype" />
			<xsl:with-param name="variable" select="'result.getParameter(currentParam)'" />
		</xsl:call-template>
		<xsl:text>;</xsl:text>
	</xsl:template>

	<xsl:template match="function/output/param" mode="field">
		<xsl:variable name="basetype">
			<xsl:call-template name="basetype_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="type"     select="@type"     />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="required" select="@required" />
				<xsl:with-param name="type"     select="$basetype" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:text>

   private final </xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text> _</xsl:text>
		<xsl:value-of select="@name" />
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
					<xsl:value-of select="@name" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="required" select="@required" />
				<xsl:with-param name="type"     select="$basetype" />
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
		<xsl:value-of select="@name" />
		<xsl:text>;
   }</xsl:text>
	</xsl:template>

</xsl:stylesheet>

