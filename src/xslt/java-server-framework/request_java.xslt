<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 This stylesheet transform the input parameters of a function
 to a Request object with get method that will be used by the user
 in the call method of the implementation.

 $Id$

 Copyright 2004 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="request">

<xsl:text><![CDATA[
/**
 * Container for the input parameters of the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> function.
 */
public final static class Request {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------
]]></xsl:text>
		<xsl:call-template name="constructor_request" />
		<xsl:text>

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The IP address from where the call came from.
    */
   private final String __ip;</xsl:text>
		<xsl:apply-templates select="input/param" mode="field" />

		<xsl:if test="input/data/element">
			<xsl:text>

   /**
    * The data section for the request.
    */
   private final org.xins.common.xml.Element __dataSection;</xsl:text>
		</xsl:if>

		<xsl:text>

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the IP address of the host that requested the function.
    *
    * @return
    *    IP address (e.g. 192.168.0.1), cannot be &lt;code&gt;null&lt;/code&gt;.
    */
   public final String remoteIP() {
      return __ip;
   }</xsl:text>

		<xsl:apply-templates select="input/param" mode="method" />

		<xsl:apply-templates select="input/data/element" mode="listMethod" />
		<xsl:apply-templates select="input/data/element" mode="elementClass" />
		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<!-- Generated the constructor for the request
	     The contructor sets the input values.
	-->
	<xsl:template name="constructor_request">

		<xsl:text><![CDATA[
   /**
    * Constructs a new <code>Request</code> instance.
    */
   public Request(String _ip]]></xsl:text>
		<xsl:for-each select="input/param">
			<xsl:variable name="javatype">
				<xsl:call-template name="javatype_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="specsdir"     select="$specsdir"     />
					<xsl:with-param name="required"     select="@required"     />
					<xsl:with-param name="type"         select="@type"         />
				</xsl:call-template>
			</xsl:variable>
			<xsl:text>, </xsl:text>
			<xsl:value-of select="$javatype" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="@name" />
		</xsl:for-each>
		<xsl:if test="input/data/element">
			<xsl:text>, org.xins.common.xml.Element _dataSection</xsl:text>
		</xsl:if>
		<xsl:text>) {
      __ip = _ip;
</xsl:text>
		<xsl:for-each select="input/param">
			<xsl:text>      _</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text> = </xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>;
</xsl:text>
		</xsl:for-each>
		<xsl:if test="input/data/element">
			<xsl:text>
      __dataSection = _dataSection;</xsl:text>
		</xsl:if>
		<xsl:text>
   }</xsl:text>
	</xsl:template>

	<!-- Generates the fields. -->
	<xsl:template match="function/input/param | input/data/element/attribute" mode="field">
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
		<xsl:value-of select="@name" />
		<xsl:text>;</xsl:text>
	</xsl:template>

	<!-- Generates the get methods. -->
	<xsl:template match="function/input/param | function/input/data/element/attribute" mode="method">
		<xsl:variable name="basetype">
			<xsl:call-template name="basetype_for_type">
				<xsl:with-param name="specsdir" select="$specsdir" />
				<xsl:with-param name="api"      select="$api"      />
				<xsl:with-param name="type"     select="@type"     />
			</xsl:call-template>
		</xsl:variable>
		<!-- Get the name of the get method. -->
		<xsl:variable name="hungarianName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>
		<!-- Get the return type of the variable. -->
		<xsl:variable name="javatype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="@required"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>
		<!-- Get the return type of the get method. -->
		<xsl:variable name="javasimpletype">
			<xsl:call-template name="javatype_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="required"     select="'true'"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="typeIsPrimary">
			<xsl:call-template name="is_java_datatype">
				<xsl:with-param name="text" select="$javasimpletype" />
			</xsl:call-template>
		</xsl:variable>

		<!-- If the object is not required, write a isSetType() method -->
		<xsl:if test="not(@required = 'true')">
			<xsl:text>
   /**
    * As the parameter is optional, this method checks whether this parameter
    * has been sent.
    *
    * @return
    *    true is the parameter has been sent, false otherwise.</xsl:text>
		<xsl:if test="deprecated">
			<xsl:text>
    *
    * @deprecated
    *    </xsl:text>
			<xsl:value-of select="deprecated/text()" />
		</xsl:if>
		<xsl:text>
    */
   boolean isSet</xsl:text>
			<xsl:value-of select="$hungarianName" />
			<xsl:text>() {
      return _</xsl:text>
			<xsl:choose>
				<xsl:when test="name()='attribute'">
					<xsl:text>element.getAttribute("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>")</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@name" />
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text> != null;
   }
			</xsl:text>
		</xsl:if>
		<!-- Generates the get method. -->
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
		<xsl:text><![CDATA[ input parameter <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em>,
    * which is ]]></xsl:text>
		<xsl:value-of select="description/text()" />
		<xsl:text><![CDATA[
    *
    * @return
    *    the value of the <em>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</em> input parameter]]></xsl:text>
		<xsl:choose>
			<xsl:when test="not($basetype = '_text')">.</xsl:when>
			<xsl:otherwise>
				<xsl:text><![CDATA[, never <code>null</code>.]]></xsl:text>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="not(@required = 'true')">
			<xsl:text>
    *
    * @throws ParameterNotInitializedException
    *    if the value has not been set.</xsl:text>
		</xsl:if>
		<xsl:if test="deprecated">
			<xsl:text>
    *
    * @deprecated
    *    </xsl:text>
			<xsl:value-of select="deprecated/text()" />
		</xsl:if>
		<xsl:text>
    */
   </xsl:text>
		<xsl:value-of select="$javasimpletype" />
		<xsl:text> get</xsl:text>
		<xsl:value-of select="$hungarianName" />
		<xsl:text>() </xsl:text>
		<xsl:if test="not(@required = 'true')">
			<xsl:text>throws org.xins.server.ParameterNotInitializedException </xsl:text>
		</xsl:if>
		<xsl:text>{
      </xsl:text>
		<xsl:choose>
			<xsl:when test="@required = 'true'">
				<xsl:text>return _</xsl:text>
				<xsl:choose>
					<xsl:when test="name()='attribute'">
						<xsl:text>element.getAttribute("</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>")</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@name" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>if (!isSet</xsl:text>
				<xsl:value-of select="$hungarianName" />
				<xsl:text>()) {
         throw new org.xins.server.ParameterNotInitializedException("</xsl:text>
				<xsl:value-of select="@name" />
				<xsl:text>");
      }
      return _</xsl:text>
				<xsl:choose>
					<xsl:when test="name()='attribute'">
						<xsl:text>element.getAttribute("</xsl:text>
						<xsl:value-of select="@name" />
						<xsl:text>")</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@name" />
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="$typeIsPrimary = 'true'">
					<xsl:text>.</xsl:text>
					<xsl:value-of select="$javasimpletype" />
					<xsl:text>Value()</xsl:text>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>;
   }</xsl:text>
	</xsl:template>

	<!-- ************************************************************* -->
	<!-- Generate the list data/element methods                         -->
	<!-- ************************************************************* -->

	<xsl:template match="input/data/element" mode="listMethod">
		<xsl:variable name="objectName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@name" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:text><![CDATA[

   /**
    * Gets the list of <code>Request.]]></xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text><![CDATA[</code> included the data section.
    *
    * @return 
    *    A list of <code>Request.]]></xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text><![CDATA[</code>, cannot be <code>null</code>.]]></xsl:text>
		<xsl:if test="deprecated">
			<xsl:text>
    *
    * @deprecated
    *    </xsl:text>
			<xsl:value-of select="deprecated/text()" />
		</xsl:if>
		<xsl:text>
    */
   public java.util.List list</xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text>() {
      java.util.List elements = __dataSection.getChildElements("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>");
      java.util.List resultList = new java.util.ArrayList(elements.size());
      java.util.Iterator itElements = elements.listIterator();
      while (itElements.hasNext()) {
         org.xins.common.xml.Element nextElement = (org.xins.common.xml.Element)itElements.next();
         </xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text> elem = new </xsl:text>
		<xsl:value-of select="$objectName" />
		<xsl:text>(nextElement);
         resultList.add(elem);
      }
      return resultList;
   }
</xsl:text>
	</xsl:template>

	<!-- ************************************************************* -->
	<!-- Generate the data/element classes.                            -->
	<!-- ************************************************************* -->

	<xsl:template match="input/data/element" mode="elementClass">
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
   public final static class </xsl:text>
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
		<xsl:text>(org.xins.common.xml.Element element) {
         _element = element;
      }

      //-------------------------------------------------------------------------
      // Fields
      //-------------------------------------------------------------------------

      /**
       * Element containing the values of this object.
       */
      private final org.xins.common.xml.Element _element;

      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

</xsl:text>

			<xsl:if test="contains/pcdata">
				<xsl:text><![CDATA[
      /**
       * Gets a <code>PCDATA</code> of the element.
       *
       * @return
       *    the PCDATA for this element, cannot be <code>null</code>.
       */
      final String pcdata() {
         _element.getText();
      }

]]></xsl:text>
			</xsl:if>

			<xsl:apply-templates select="attribute" mode="method" />
			<xsl:apply-templates select="contains/contained" />
			<xsl:text>
   }
</xsl:text>
	</xsl:template>

	<xsl:template match="input/data/element/contains/contained">
		<!-- Define the variables used in the set methods -->
		<xsl:variable name="methodName">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text" select="@element" />
			</xsl:call-template>
		</xsl:variable>
		<xsl:text><![CDATA[

   /**
    * Gets the list of <code>Request.]]></xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text><![CDATA[</code> included in this element.
    *
    * @return 
    *    A list of <code>Request.]]></xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text><![CDATA[</code>, cannot be <code>null</code>.]]></xsl:text>
		<xsl:if test="deprecated">
			<xsl:text>
    *
    * @deprecated
    *    </xsl:text>
			<xsl:value-of select="deprecated/text()" />
		</xsl:if>
		<xsl:text>
    */
   public java.util.List list</xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>() {
      java.util.List elements = _element.getChildElements("</xsl:text>
		<xsl:value-of select="@element" />
		<xsl:text>");
      java.util.List resultList = new java.util.ArrayList(elements.getSize());
      java.util.Iterator itElements = elements.listIterator();
      while (itElements.hasNext()) {
         org.xins.common.xml.Element nextElement = (org.xins.common.xml.Element)itElements.next();
         </xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text> elem = new </xsl:text>
		<xsl:value-of select="$methodName" />
		<xsl:text>(nextElement);
         resultList.add(elem);
      }
      return resultList;
   }
</xsl:text>
	</xsl:template>

</xsl:stylesheet>

