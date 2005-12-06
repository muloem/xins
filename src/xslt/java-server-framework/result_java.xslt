<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the different classes used to specify the result of a function.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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
		<xsl:apply-templates select="output/data/element" mode="addElementClass" />
	</xsl:template>

	<xsl:template match="output">
		<xsl:text>
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------</xsl:text>
		<xsl:apply-templates select="param">
			<xsl:with-param name="methodImpl" select="'param'" />
		</xsl:apply-templates>

		<xsl:if test="data/@contains">
			<xsl:variable name="elementName" select="data/@contains" />
			<xsl:apply-templates select="data/element[@name=$elementName]" mode="addMethod" />
		</xsl:if>
		<xsl:for-each select="data/contains/contained">
			<xsl:variable name="elementName" select="@element" />
			<xsl:apply-templates select="../../element[@name=$elementName]" mode="addMethod" />
		</xsl:for-each>

		<xsl:text>

   protected org.xins.server.InvalidResponseResult checkOutputParameters() {
</xsl:text>
		<xsl:apply-templates select="." mode="checkParams">
			<xsl:with-param name="side" select="'server'" />
		</xsl:apply-templates>
		<xsl:text>
   }
</xsl:text>
	</xsl:template>

	<xsl:template match="output/param | output/data/element/attribute | input/data/element/attribute">

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
			<xsl:call-template name="hungarianPropertyUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="javasimpletype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_node" select="$project_node" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="'true'"        />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="javaVariable">
			<xsl:call-template name="hungarianPropertyLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="typeToString">
			<xsl:call-template name="javatype_to_string_for_type">
				<xsl:with-param name="api"      select="$api" />
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="required" select="@required" />
				<xsl:with-param name="type"     select="@type" />
				<xsl:with-param name="variable" select="$javaVariable" />
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
		<xsl:value-of select="$javaVariable" />
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
		<xsl:value-of select="$javaVariable" />
		<xsl:text>) {
      </xsl:text>
		<xsl:if test="$typeIsPrimary = 'false'" >
			<xsl:text>if (</xsl:text>
			<xsl:value-of select="$javaVariable" />
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
		<xsl:if test="$typeIsPrimary = 'false'" >
			<xsl:text>
      }</xsl:text>
		</xsl:if>
		<xsl:text>
   }</xsl:text>
	</xsl:template>

	<!-- ************************************************************* -->
	<!-- Generate the add data/element methods                         -->
	<!-- ************************************************************* -->

	<xsl:template match="output/data/element | input/data/element" mode="addMethod">
		<xsl:variable name="javaVariable">
			<xsl:call-template name="hungarianPropertyLower">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="objectName">
			<xsl:call-template name="hungarianPropertyUpper">
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
		<xsl:value-of select="../../../@name" />
		<xsl:if test="ancestor::input">
			<xsl:text>Request</xsl:text>
		</xsl:if>
		<xsl:text>.</xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$javaVariable" />
		<xsl:text>) {
      add((org.xins.common.xml.Element)</xsl:text>
		<xsl:value-of select="$javaVariable" />
		<xsl:text>.getElement().clone());
   }
</xsl:text>
	</xsl:template>

	<!-- ************************************************************* -->
	<!-- Generate the data/element classes.                            -->
	<!-- ************************************************************* -->

	<xsl:template match="output/data/element | input/data/element" mode="addElementClass">
		<xsl:variable name="objectName">
			<xsl:call-template name="hungarianPropertyUpper">
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
   </xsl:text>
		<xsl:if test="ancestor::input">
			<xsl:text>public static final </xsl:text>
		</xsl:if>
		<xsl:text>class </xsl:text>
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
		<xsl:if test="ancestor::input">
			<xsl:text>public </xsl:text>
		</xsl:if>
		<xsl:value-of select="$objectName" />
		<xsl:text>() {
      }

      //-------------------------------------------------------------------------
      // Fields
      //-------------------------------------------------------------------------

      /**
       * Element containing the values of this object.
       */
      private final org.xins.common.xml.ElementBuilder _elementBuilder = new org.xins.common.xml.ElementBuilder("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[");


      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

      /**
       * Returns the element containing the values of the data element.
       *
       * @return
       *    the element created by invoking the different set methods
       *    of this object, never <code>null</code>.
       */
      final org.xins.common.xml.Element getElement() {
         return _elementBuilder.createElement();
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
      ]]></xsl:text>
		<xsl:if test="ancestor::input">
			<xsl:text>public </xsl:text>
		</xsl:if>
		<xsl:text>final void pcdata(String data) {
         _elementBuilder.setText(data);
      }

</xsl:text>
			</xsl:if>

			<xsl:apply-templates select="attribute">
				<xsl:with-param name="methodImpl" select="'_elementBuilder.setAttribute'" />
			</xsl:apply-templates>
			<xsl:apply-templates select="contains/contained" />
			<xsl:text>
   }
</xsl:text>
	</xsl:template>

	<xsl:template match="output/data/element/contains/contained">
		<!-- Define the variables used in the set methods -->
		<xsl:variable name="javaVariable">
			<xsl:call-template name="hungarianPropertyLower">
				<xsl:with-param name="text" select="@element" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="methodName">
			<xsl:call-template name="hungarianPropertyUpper">
				<xsl:with-param name="text" select="@element" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:text>

   /**
    * Adds a sub-element to this element.
    *
    * @param </xsl:text>
		<xsl:value-of select="$javaVariable" />
		<xsl:text><![CDATA[
    *    the value of the sub-element to add, cannot be <code>null</code>.]]></xsl:text>
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
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="$javaVariable" />
		<xsl:text>) {
      _elementBuilder.addChild((org.xins.common.xml.Element)</xsl:text>
		<xsl:value-of select="$javaVariable" />
		<xsl:text>.getElement().clone());
   }
</xsl:text>
	</xsl:template>

</xsl:stylesheet>
