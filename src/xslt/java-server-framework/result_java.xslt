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
		<xsl:apply-templates select="output/param">
			<xsl:with-param name="methodImpl" select="'_builder.param'" />
		</xsl:apply-templates>

		<xsl:apply-templates select="output/data/element" mode="addMethod">
		</xsl:apply-templates>

		<xsl:if test="output/data/element">
			<xsl:text><![CDATA[

   /**
    * Add a new JDOM element.
    */
   private void addJDOMElement(org.jdom.Element element) {
      _builder.startTag(element.getName());
      java.util.Iterator itAttributes = element.getAttributes().iterator();
      while (itAttributes.hasNext()) {
         org.jdom.Attribute nextAttribute = (org.jdom.Attribute) itAttribute.next();
         _builder.attribute(nextAttribute.getName(), nextAttribute.getValue());
      }
      java.util.Iterator itSubElements = element.getChildren().iterator();
      while (itSubElements.hasNext()) {
         org.jdom.Attribute nextChild = (org.jdom.Element) itSubElements.next();
         addJDOMElement(nextChild);
      }
      _builder.endTag();
   }]]></xsl:text>
		</xsl:if>
		<xsl:text>

   /**
    * Returns the XML structure which is creating by invoking the different
    * 'set' and 'add' methods
    *
    * @return
    *    the CallResult with the XML structure.
    */
   org.xins.server.CallResult getCallResult() {
      return _builder;
   }

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

		<!-- Write the set methods -->
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
		<xsl:value-of select="$methodName" />
		<xsl:text>(</xsl:text>
		<xsl:value-of select="$javatype" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>) {
      </xsl:text>
		<xsl:value-of select="$methodImpl" />
		<xsl:text>("</xsl:text>
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

	<!-- ************************************************************* -->
	<!-- Generate the add data/element methods                         -->
	<!-- ************************************************************* -->

	<xsl:template match="function/output/data/element" mode="addMethod">
		<xsl:variable name="objectName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text">
					<xsl:value-of select="@name" />
				</xsl:with-param>
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
				<xsl:with-param name="text">
					<xsl:value-of select="@name" />
				</xsl:with-param>
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
      public final org.jdom.Element getDOMElement() {
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
		<!-- Define the varibles used in the set methods -->
		<xsl:variable name="methodName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text">
					<xsl:value-of select="@element" />
				</xsl:with-param>
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