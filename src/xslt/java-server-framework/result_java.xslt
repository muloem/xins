<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 $Id$
-->
<!-- This stylesheet generated the different classes used to
     specify the result of a function.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:variable name="version">
		<xsl:call-template name="revision2string">
			<xsl:with-param name="revision">
				<xsl:value-of select="//function/@rcsversion" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>

	<xsl:variable name="functionName" select="//function/@name" />
	<xsl:variable name="className" select="'SuccessfulResult'" />

	<xsl:template name="result">
		<!-- Generate the Result interface -->
<xsl:text><![CDATA[/**
 * Result of a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public interface Result {
}
]]></xsl:text>

		<!-- Generate the UnsuccessResult interface -->
		<xsl:text><![CDATA[
/**
 * Unsuccessful result of a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public interface UnsuccessfulResult extends Result {
}
]]></xsl:text>

		<!-- Generate the SuccessResult class -->
		<xsl:text><![CDATA[
/**
 * Successful result of a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public final static class SuccessfulResult implements Result {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

	 final static org.xins.server.CallResultBuilder _builder = new org.xins.server.CallResultBuilder();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

	 /**
	  * Creates a new SuccessfulResult.
		*/
	 public SuccessfulResult() {

	    // Reports the success
	    _builder.startResponse(true, null);
	 }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------]]></xsl:text>
		<xsl:apply-templates select="output/param" mode="method" />
		<xsl:if test="output/data/element">
			<xsl:text><![CDATA[

   /**
    * Gets the data element, if any. If there is no data element, then
    * <code>null</code> is returned.
    *
    * <p>This method will always {@link org.jdom.Element#clone() clone}
    * the underlying element and return the clone.
    *
    * @return
    *    the data element, or <code>null</code> if there is none.
    */
   public org.jdom.Element getDataElement() {
      if (_dataElement != null) {
         return (org.jdom.Element) _dataElement.clone();
      } else {
         return null;
      }
   }]]></xsl:text>
		</xsl:if>
		<xsl:text>
   org.xins.server.CallResult getCallResult() {
      return _builder;
   }
}
</xsl:text>
	</xsl:template>

<!--
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

   private </xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text> _</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>;</xsl:text>
	</xsl:template>
-->

	<xsl:template match="function/output/param" mode="method">
		<xsl:variable name="basetype">
			<xsl:call-template name="basetype_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="type"     select="@type"     />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="methodName">
			<xsl:text>set</xsl:text>
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text">
					<xsl:value-of select="@name" />
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
    * Sets the value of the ]]></xsl:text>
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
    * @param
    *    the value of the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> output parameter]]></xsl:text>
		<xsl:choose>
			<xsl:when test="not($basetype = '_text')">.</xsl:when>
			<xsl:when test="@required = 'true'">
				<xsl:text><![CDATA[, cannot be <code>null</code>.]]></xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text><![CDATA[, or can be <code>null</code>.]]></xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text><![CDATA[
    */
   public void ]]></xsl:text>
		<xsl:text> </xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>) {
      _builder.param("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>",  </xsl:text>
		<xsl:call-template name="javatype_to_string_for_type">
			<xsl:with-param name="api"      select="$api" />
			<xsl:with-param name="specsdir" select="$specsdir" />
			<xsl:with-param name="required" select="$required" />
			<xsl:with-param name="type"     select="@type" />
			<xsl:with-param name="variable" select="@name" />
		</xsl:call-template>
		<xsl:text>);
   }</xsl:text>
	</xsl:template>

</xsl:stylesheet>