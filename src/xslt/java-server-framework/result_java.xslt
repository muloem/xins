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
			<xsl:with-param name="revision" select="//function/@rcsversion" />
		</xsl:call-template>
	</xsl:variable>

	<xsl:variable name="functionName" select="//function/@name" />
	<xsl:variable name="className" select="'SuccessfulResult'" />

	<xsl:template name="result">
		<xsl:param name="createsSession" />

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
		<!-- Generate the UnsuccessResult interface                        -->
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
public final static class SuccessfulResult extends org.xins.server.FunctionResult implements Result {

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
    * Creates a new SuccessfulResult.
    */
   public SuccessfulResult(]]></xsl:text>
	  <xsl:if test="$createsSession = 'true'">
			<xsl:text>org.xins.server.Session session</xsl:text>
		</xsl:if>
		<xsl:text>) {

      // Report the success
      super(true, null);</xsl:text>
	  <xsl:if test="$createsSession = 'true'">
			<xsl:text>
      param("_session", session.getIDString());</xsl:text>
		</xsl:if>
		<xsl:text>
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------</xsl:text>
		<xsl:apply-templates select="output/param">
			<xsl:with-param name="methodImpl" select="'param'" />
		</xsl:apply-templates>

		<xsl:apply-templates select="output/data/element" mode="addMethod">
		</xsl:apply-templates>

		<xsl:text>

}
</xsl:text>
		<xsl:apply-templates select="output/data/element" mode="elementClass">
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="function/output/param | function/output/data/element/attribute">

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
				<xsl:with-param name="required"     select="'true'"     />
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
				<xsl:text>doesn't need</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text> to be called before returning the SuccessfulResult.
    *
    * @param </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[
    *    the value of the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> output parameter, can be <code>null</code>.
    *      The value is not added to the result if the value is <code>null</code> or
    *      it's <code>String</code> representation is an empty <code>String</code>.]]></xsl:text>
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

	<xsl:template match="function/output/data/element" mode="addMethod">
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
		<xsl:text><![CDATA[</code> to the result.
    */
   public void add]]></xsl:text>
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

	<xsl:template match="function/output/data/element" mode="elementClass">
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
		<xsl:text>");


      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

      /**
       * Returns the element containing the informations about the
       */
      final org.jdom.Element getDOMElement() {
         return _jdomElement;
      }

			</xsl:text>
			<xsl:apply-templates select="attribute">
				<xsl:with-param name="methodImpl" select="'_jdomElement.setAttribute'" />
			</xsl:apply-templates>
			<xsl:apply-templates select="contains/contained">
			</xsl:apply-templates>
			<xsl:text>
   }
</xsl:text>
	</xsl:template>

	<xsl:template match="function/output/data/element/contains/contained">
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