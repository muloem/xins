<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the different classes used to specify the result of a function.

 $Id$
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
			<xsl:with-param name="revision" select="//function/@rcsversion" />
		</xsl:call-template>
	</xsl:variable>

	<xsl:variable name="functionName" select="//function/@name" />
	<xsl:variable name="className" select="'SuccessfulResult'" />

	<xsl:template name="result">

		<!-- ************************************************************* -->
		<!-- Generate the Result interface                                 -->
		<!-- ************************************************************* -->

<xsl:text><![CDATA[/**
 * Result of a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public interface Result {
}
]]></xsl:text>

		<!-- ************************************************************* -->
		<!-- Generate the UnsuccessfulResult interface                     -->
		<!-- ************************************************************* -->

		<xsl:text><![CDATA[
/**
 * Unsuccessful result of a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public interface UnsuccessfulResult extends Result {
}
]]></xsl:text>

		<!-- ************************************************************* -->
		<!-- Generate the SuccessResult class                              -->
		<!-- ************************************************************* -->

		<xsl:text><![CDATA[
/**
 * Successful result of a call to the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public final static class SuccessfulResult
extends org.xins.server.FunctionResult
implements Result {

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
    * Creates a new <code>SuccessfulResult</code> object.
    */
   public SuccessfulResult() {

      // Report the success
      super(null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
]]></xsl:text>
		<!-- Generate the set methods, the inner classes and the add methods -->
		<xsl:apply-templates select="output" />
		<xsl:text>
}
</xsl:text>
		<xsl:apply-templates select="output/data/element" mode="elementClass" />
	</xsl:template>

	<xsl:template match="output">
		<xsl:text>
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------</xsl:text>
		<xsl:apply-templates select="param">
			<xsl:with-param name="methodImpl" select="'param'" />
		</xsl:apply-templates>

		<xsl:apply-templates select="data/element" mode="addMethod" />

		<xsl:text>

   protected org.xins.server.InvalidResponseResult checkOutputParameters() {

      // Check the mandatory output parameters
      org.xins.server.InvalidResponseResult _errorOutputResult = null;</xsl:text>

		<!-- ************************************************************* -->
		<!-- Check required output parameters                              -->
		<!-- ************************************************************* -->

		<xsl:for-each select="param[@required='true']">
			<xsl:text>
      if (getParameter("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>") == null) {
         if (_errorOutputResult == null) {
            _errorOutputResult = new org.xins.server.InvalidResponseResult();
         }
         _errorOutputResult.addMissingParameter("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>");
      }</xsl:text>
		</xsl:for-each>

		<!-- ************************************************************* -->
		<!-- Check values for types for the output parameters               -->
		<!-- ************************************************************* -->

		<xsl:if test="param[not(@type='_text' or string-length(@type) = 0)]">
			<xsl:text>

      // Check values are valid for the associated types</xsl:text>
			<xsl:for-each select="param[not(@type='_text' or string-length(@type) = 0)]">
				<xsl:text>
      if (!</xsl:text>
				<xsl:call-template name="javatypeclass_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="specsdir"     select="$specsdir"     />
					<xsl:with-param name="type"         select="@type"         />
				</xsl:call-template>
				<xsl:text>.SINGLETON.isValidValue(getParameter("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>"))) {
         if (_errorOutputResult == null) {
            _errorOutputResult = new org.xins.server.InvalidResponseResult();
         }
         _errorOutputResult.addInvalidValueForType("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>", "</xsl:text>
				<xsl:value-of select="@type" />
				<xsl:text>");
      }</xsl:text>
			</xsl:for-each>
		</xsl:if>

		<xsl:text>
      return _errorOutputResult;
   }
</xsl:text>
	</xsl:template>

	<xsl:template match="output/param | output/data/element/attribute">

		<!-- Define the variables used in the set methods -->

		<xsl:param name="methodImpl" />

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
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="javasimpletype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="'true'"        />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="typeToString">
			<xsl:call-template name="javatype_to_string_for_type">
				<xsl:with-param name="api"      select="$api" />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="required" select="@required" />
				<xsl:with-param name="type"     select="@type" />
				<xsl:with-param name="variable" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="typeIsPrimary">
			<xsl:call-template name="is_java_datatype">
				<xsl:with-param name="text" select="$javasimpletype" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Write the set methods -->
		<xsl:text><![CDATA[

   /**
    * Sets the value of the ]]></xsl:text>
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
    * This method ]]></xsl:text>
		<xsl:choose>
			<xsl:when test="@required = 'true'">
				<xsl:text>has</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>does not need</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text> to be called before returning the
    * SuccessfulResult.
    *
    * @param </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[
    *    the value of the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> output parameter,
    *      can be <code>null</code>.
    *      The value is not added to the result if the value is <code>null</code>
    *      or its <code>String</code> representation is an empty
    *      <code>String</code>.]]></xsl:text>
		<xsl:if test="deprecated">
			<xsl:text>
    *
    * @deprecated
    *    </xsl:text>
			<xsl:value-of select="deprecated/text()" />
		</xsl:if>
		<xsl:text>
    */
   public void </xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$javasimpletype" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>) {
      </xsl:text>
		<xsl:if test="not($typeIsPrimary = 'true')" >
		<xsl:text>if (</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text> != null &amp;&amp; !</xsl:text>
		<xsl:value-of select="$typeToString" />
		<xsl:text>.equals("")) {
         </xsl:text>
			</xsl:if>
		<xsl:value-of select="$methodImpl" />
		<xsl:text>("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>",  </xsl:text>
		<xsl:value-of select="$typeToString" />
		<xsl:text>);</xsl:text>
		<xsl:if test="not($typeIsPrimary = 'true')" >
			<xsl:text>
      }</xsl:text>
		</xsl:if>
		<xsl:text>
   }</xsl:text>
	</xsl:template>

	<!-- ************************************************************* -->
	<!-- Generate the add data/element methods                         -->
	<!-- ************************************************************* -->

	<xsl:template match="output/data/element" mode="addMethod">
		<xsl:variable name="objectName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<!-- First, write in the SuccessfulResult the add(Element) method -->
		<xsl:text><![CDATA[

   /**
    * Adds a new <code>]]></xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text><![CDATA[</code> to the result.]]></xsl:text>
		<xsl:if test="deprecated">
			<xsl:text>
    *
    * @deprecated
    *    </xsl:text>
			<xsl:value-of select="deprecated/text()" />
		</xsl:if>
		<xsl:text>
    */
   public void add</xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>) {
      addJDOMElement(</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>.getDOMElement());
   }
</xsl:text>
</xsl:template>

	<!-- ************************************************************* -->
	<!-- Generate the data/element classes.                            -->
	<!-- ************************************************************* -->

	<xsl:template match="output/data/element" mode="elementClass">
		<xsl:variable name="objectName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<!-- Create the class that contains the data of the element. -->
		<xsl:text>
   /**
    * Class that contains the data for the </xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text> element.
    */
   class </xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text><![CDATA[ {
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
       * Creates a new <code>]]></xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text><![CDATA[</code> instance.
       */
      ]]></xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text>() {
      }

      //-------------------------------------------------------------------------
      // Fields
      //-------------------------------------------------------------------------

      /**
       * Element containing the values of this object.
       */
      private final org.jdom.Element _jdomElement = new org.jdom.Element("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[");


      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

      /**
       * Returns the element containing the values of the data element.
       *
       * @return
       *    the JDOM element created by invoking the different set methods
       *    of this object, never <code>null</code>.
       */
      final org.jdom.Element getDOMElement() {
         return _jdomElement;
      }

]]></xsl:text>

			<xsl:if test="contains/pcdata">
				<xsl:text><![CDATA[
      /**
       * Sets a <code>PCDATA</code> to the element. This method erases previous
       * <code>PCDATA</code> set by invoking this method.
       *
       * @param data
       *    the PCDATA for this element, cannot be <code>null</code>.
       */
      final void pcdata(String data) {
         _jdomElement.setText(data);
      }

]]></xsl:text>
			</xsl:if>

			<xsl:apply-templates select="attribute">
				<xsl:with-param name="methodImpl" select="'_jdomElement.setAttribute'" />
			</xsl:apply-templates>
			<xsl:apply-templates select="contains/contained" />
			<xsl:text>
   }
</xsl:text>
	</xsl:template>

	<xsl:template match="output/data/element/contains/contained">
		<!-- Define the variables used in the set methods -->
		<xsl:variable name="methodName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@element" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:text><![CDATA[

   /**
    * Adds a sub-element to this element.
    *
    * @param
    *    the value of the sub-element to add, cannot be <code>null</code>.
    */
   public void add]]></xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@element" />
		<xsl:text>) {
      _jdomElement.addContent(</xsl:text>
		<xsl:value-of select="@element" />
		<xsl:text>.getDOMElement().getContent());
   }
</xsl:text>
	</xsl:template>

</xsl:stylesheet>
