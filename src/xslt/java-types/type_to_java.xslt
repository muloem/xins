<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the java representation of the type.

 $Id$

 Copyright 2004 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- Define parameters -->
	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<!-- Perform includes -->
	<xsl:include href="../casechange.xslt"    />
	<xsl:include href="../xml_to_java.xslt" />
	<xsl:include href="../java.xslt"          />
	<xsl:include href="../types.xslt"         />

	<xsl:output method="text" />

	<xsl:template match="type">

		<xsl:variable name="type" select="@name" />
		<xsl:variable name="classname">
			<xsl:call-template name="hungarianUpper">
				<xsl:with-param name="text">
					<xsl:value-of select="$type" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="kind">
			<xsl:choose>
				<xsl:when test="enum">enum</xsl:when>
				<xsl:when test="pattern">pattern</xsl:when>
				<xsl:when test="properties">properties</xsl:when>
				<xsl:when test="int8">int8</xsl:when>
				<xsl:when test="int16">int16</xsl:when>
				<xsl:when test="int32">int32</xsl:when>
				<xsl:when test="int64">int64</xsl:when>
				<xsl:when test="float32">float32</xsl:when>
				<xsl:when test="float64">float64</xsl:when>
				<xsl:when test="list">list</xsl:when>
				<xsl:when test="set">set</xsl:when>
				<xsl:otherwise>
					<xsl:message terminate="yes">
						<xsl:text>Unable to determine kind of type.</xsl:text>
					</xsl:message>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="superclass">
			<xsl:choose>
				<xsl:when test="$kind = 'enum'">org.xins.common.types.EnumType</xsl:when>
				<xsl:when test="$kind = 'pattern'">org.xins.common.types.PatternType</xsl:when>
				<xsl:when test="$kind = 'properties'">org.xins.common.types.standard.Properties</xsl:when>
				<xsl:when test="$kind = 'int8'">org.xins.common.types.standard.Int8</xsl:when>
				<xsl:when test="$kind = 'int16'">org.xins.common.types.standard.Int16</xsl:when>
				<xsl:when test="$kind = 'int32'">org.xins.common.types.standard.Int32</xsl:when>
				<xsl:when test="$kind = 'int64'">org.xins.common.types.standard.Int64</xsl:when>
				<xsl:when test="$kind = 'float32'">org.xins.common.types.standard.Float32</xsl:when>
				<xsl:when test="$kind = 'float64'">org.xins.common.types.standard.Float64</xsl:when>
				<xsl:when test="$kind = 'list'">org.xins.common.types.List</xsl:when>
				<xsl:when test="$kind = 'set'">org.xins.common.types.List</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;

import org.xins.common.types.EnumItem;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * </xsl:text>
		<xsl:call-template name="hungarianUpper">
			<xsl:with-param name="text">
				<xsl:value-of select="$kind" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ type <em>]]></xsl:text>
		<xsl:value-of select="$type" />
		<xsl:text><![CDATA[</em>.
 */
public final class ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text> extends </xsl:text>
		<xsl:value-of select="$superclass" />
		<xsl:text><![CDATA[ {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text> SINGLETON = new </xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text>();</xsl:text>
		<xsl:if test="$kind = 'enum'">
			<xsl:apply-templates select="enum/item" mode="field" />
		</xsl:if>
		<xsl:text><![CDATA[


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------]]></xsl:text>

		<xsl:if test="$kind = 'pattern'">
			<xsl:text>
   public static String fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return (String) SINGLETON.fromString(string);
   }

   public static String fromStringForOptional(String string)
   throws TypeValueException {
      return (String) SINGLETON.fromString(string);
   }
</xsl:text>
		</xsl:if>

		<xsl:if test="$kind = 'int8' or $kind = 'int16' or $kind = 'int32' or $kind = 'int64' or $kind = 'float32' or $kind = 'float64'">
			<xsl:variable name="optional_object">
				<xsl:call-template name="javatype_for_type">
					<xsl:with-param name="project_file"     select="$project_file" />
					<xsl:with-param name="specsdir"     select="$specsdir" />
					<xsl:with-param name="api"     select="$api" />
					<xsl:with-param name="type"     select="concat('_', $kind)" />
					<xsl:with-param name="required" select="'false'" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:text>

   public static </xsl:text>
			<xsl:value-of select="$optional_object"/>
			<xsl:text> fromStringForOptional(String string)
   throws TypeValueException {
      return (</xsl:text>
			<xsl:value-of select="$optional_object"/>
			<xsl:text>) SINGLETON.fromString(string);
   }</xsl:text>

			<xsl:variable name="required_object">
				<xsl:call-template name="javatype_for_type">
					<xsl:with-param name="project_file"     select="$project_file" />
					<xsl:with-param name="specsdir"     select="$specsdir" />
					<xsl:with-param name="api"     select="$api" />
					<xsl:with-param name="type"     select="concat('_', $kind)" />
					<xsl:with-param name="required" select="'true'" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:text>

   public static </xsl:text>
			<xsl:value-of select="$required_object"/>
			<xsl:text> fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return ((</xsl:text>
			<xsl:value-of select="$optional_object"/>
			<xsl:text>)SINGLETON.fromString(string)).</xsl:text>
			<xsl:value-of select="$required_object"/>
			<xsl:text>Value();
   }
</xsl:text>
		</xsl:if>

		<xsl:text><![CDATA[

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text><![CDATA[</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private ]]></xsl:text>
		<xsl:value-of select="$classname" />
		<xsl:text>()</xsl:text>
		<xsl:text> {
      super("</xsl:text>
		<xsl:value-of select="$type" />
		<xsl:text>", </xsl:text>
		<xsl:choose>
			<xsl:when test="$kind = 'enum'">
				<xsl:text>new EnumItem[] {</xsl:text>
				<xsl:for-each select="enum/item">
					<xsl:if test="position() &gt; 1">,</xsl:if>
					<xsl:text>
         new Item("</xsl:text>
					<xsl:choose>
						<xsl:when test="@name">
							<xsl:value-of select="@name" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@value" />
						</xsl:otherwise>
					</xsl:choose>
					<xsl:text>", "</xsl:text>
					<xsl:value-of select="@value" />
					<xsl:text>")</xsl:text>
				</xsl:for-each>
				<xsl:text>}</xsl:text>
			</xsl:when>
			<xsl:when test="$kind = 'pattern'">
				<xsl:text>"</xsl:text>
				<xsl:call-template name="xml_to_java_string">
					<xsl:with-param name="text">
						<xsl:value-of select="pattern/text()" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:text>"</xsl:text>
			</xsl:when>
			<xsl:when test="$kind = 'properties'">
				<xsl:call-template name="javatypeclass_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"      />
					<xsl:with-param name="specsdir"     select="$specsdir" />
					<xsl:with-param name="type"         select="properties/@nameType" />
				</xsl:call-template>
				<xsl:text>.SINGLETON, </xsl:text>
				<xsl:call-template name="javatypeclass_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"      />
					<xsl:with-param name="specsdir"     select="$specsdir" />
					<xsl:with-param name="type"         select="properties/@valueType" />
				</xsl:call-template>
				<xsl:text>.SINGLETON</xsl:text>
			</xsl:when>
			<xsl:when test="$kind = 'int8'">
				<xsl:choose>
					<xsl:when test="int8/@min">
						<xsl:text>(byte)</xsl:text>
						<xsl:value-of select="int8/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Byte.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="int8/@max">
						<xsl:text>(byte)</xsl:text>
						<xsl:value-of select="int8/@max" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Byte.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'int16'">
				<xsl:choose>
					<xsl:when test="int16/@min">
						<xsl:text>(short)</xsl:text>
						<xsl:value-of select="int16/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Short.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="int16/@max">
						<xsl:text>(short)</xsl:text>
						<xsl:value-of select="int16/@max" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Short.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'int32'">
				<xsl:choose>
					<xsl:when test="int32/@min">
						<xsl:value-of select="int32/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Integer.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="int32/@max">
						<xsl:value-of select="int32/@max" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Integer.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'int64'">
				<xsl:choose>
					<xsl:when test="int64/@min">
						<xsl:value-of select="int64/@min" />
						<xsl:text>L</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Long.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="int64/@max">
						<xsl:value-of select="int64/@max" />
						<xsl:text>L</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Long.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'float32'">
				<xsl:choose>
					<xsl:when test="float32/@min">
						<xsl:value-of select="float32/@min" />
						<xsl:text>F</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Float.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="float32/@max">
						<xsl:value-of select="float32/@max" />
						<xsl:text>F</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Float.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'float64'">
				<xsl:choose>
					<xsl:when test="float64/@min">
						<xsl:value-of select="float64/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Double.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="float64/@max">
						<xsl:value-of select="float64/@max" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Double.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$kind = 'list'">
				<xsl:call-template name="javatypeclass_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"      />
					<xsl:with-param name="specsdir"     select="$specsdir" />
					<xsl:with-param name="type"         select="list/@type" />
				</xsl:call-template>
				<xsl:text>.SINGLETON</xsl:text>
			</xsl:when>
			<xsl:when test="$kind = 'set'">
				<xsl:call-template name="javatypeclass_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"      />
					<xsl:with-param name="specsdir"     select="$specsdir" />
					<xsl:with-param name="type"         select="set/@type" />
				</xsl:call-template>
				<xsl:text>.SINGLETON</xsl:text>
			</xsl:when>
		</xsl:choose>
		<xsl:text>);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------</xsl:text>

		<xsl:if test="$kind = 'list' or $kind = 'set'">
			<xsl:variable name="innertype">
				<xsl:choose>
					<xsl:when test="$kind = 'list'">
						<xsl:value-of select="list/@type" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="set/@type" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="javasimpletype">
				<xsl:call-template name="javatype_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="specsdir"     select="$specsdir"     />
					<xsl:with-param name="required"     select="'true'"        />
					<xsl:with-param name="type"         select="$innertype"    />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="javaoptionaltype">
				<xsl:call-template name="javatype_for_type">
					<xsl:with-param name="project_file" select="$project_file" />
					<xsl:with-param name="api"          select="$api"          />
					<xsl:with-param name="specsdir"     select="$specsdir"     />
					<xsl:with-param name="required"     select="'false'"       />
					<xsl:with-param name="type"         select="$innertype"    />
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="typeIsPrimary">
				<xsl:call-template name="is_java_datatype">
					<xsl:with-param name="text" select="$javasimpletype" />
				</xsl:call-template>
			</xsl:variable>
			<xsl:text><![CDATA[

   public org.xins.common.types.ItemList createList() {
      return new Value();
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

	/**
	 * Inner class that represents a ]]></xsl:text>
			<xsl:value-of select="$kind" />
	 		<xsl:text> of </xsl:text>
			<xsl:value-of select="$javasimpletype" />
			<xsl:text>.
	 */
   public static final class Value extends org.xins.common.types.ItemList {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------</xsl:text>
			<xsl:if test="$kind = 'set'">
	      <xsl:text>
      /**
       * Creates a new set.
       */
      public Value() {
         super(true);
      }
</xsl:text>
			</xsl:if>
			<xsl:text>

      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Add a new element in the </xsl:text>
			<xsl:value-of select="$kind" />
			<xsl:text>.
       *
       * @param value
			 *    the new value to add</xsl:text>
			<xsl:if test="not($typeIsPrimary = 'true')">
	      <xsl:text><![CDATA[, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>value == null</code>]]></xsl:text>
			</xsl:if>
			<xsl:text>.
       */
      public void add(</xsl:text>
			<xsl:value-of select="$javasimpletype" />
			<xsl:text> value) {

			</xsl:text>
			<xsl:if test="not($typeIsPrimary = 'true')">
         MandatoryArgumentChecker.check("value", value);
			</xsl:if>
			<xsl:variable name="valueasobject">
				<xsl:choose>
					<xsl:when test="$typeIsPrimary = 'true'">
						<xsl:text>new </xsl:text>
						<xsl:value-of select="$javaoptionaltype" />
						<xsl:text>(value)</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>value</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:text>
         addItem(</xsl:text>
			<xsl:value-of select="$valueasobject" />
      <xsl:text>);
      }

      /**
       * Get an element from the </xsl:text>
			<xsl:value-of select="$kind" />
			<xsl:text>.
       *
       * @param index
       *    The position of the required element.
       *
       * @return
       *    The element at the specified position</xsl:text>
			<xsl:if test="not($typeIsPrimary = 'true')">
	      <xsl:text><![CDATA[, cannot be <code>null</code>]]></xsl:text>
			</xsl:if>
      <xsl:text>.
       */
      public </xsl:text>
			<xsl:value-of select="$javasimpletype" />
			<xsl:text> get(int index) {

         return </xsl:text>
			<xsl:choose>
				<xsl:when test="$typeIsPrimary = 'true'">
					<xsl:text>((</xsl:text>
					<xsl:value-of select="$javaoptionaltype" />
					<xsl:text>) getItem(index)).</xsl:text>
					<xsl:value-of select="$javasimpletype" />
					<xsl:text>Value()</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>(</xsl:text>
					<xsl:value-of select="$javasimpletype" />
					<xsl:text>) </xsl:text>
					<xsl:text>getItem(index)</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="not($typeIsPrimary = 'true')">
			</xsl:if>
			<xsl:text>;

      }
   }</xsl:text>
		</xsl:if>

		<xsl:if test="$kind = 'enum'">
			<xsl:text><![CDATA[

   /**
    * Gets the <code>Item</code> for the specified string value.
    *
    * @param value
    *    the value for which to lookup the matching {@link Item} instance,
    *    can be <code>null</code>, in which case <code>null</code> is also
    *    returned.
    *
    * @return
    *    the matching {@link Item} instance, or <code>null</code> if and only
    *    if <code>value == null</code>.
    *
    * @throws TypeValueException
    *    if the specified value does not denote an existing item.
    */
   public static Item getItemByValue(String value)
   throws TypeValueException {

      if (value != null) {
         Object o = SINGLETON._valuesToItems.get(value);
         if (o != null) {
            return (Item) o;
         } else {
            throw new TypeValueException(SINGLETON, value);
         }
      } else {
         return null;
      }
   }

   /**
    * Gets the <code>Item</code> for the specified string name.
    *
    * @param name
    *    the name for which to lookup the matching {@link Item} instance,
    *    can be <code>null</code>, in which case <code>null</code> is also
    *    returned.
    *
    * @return
    *    the matching {@link Item} instance, or <code>null</code> if and only
    *    if <code>name == null</code>.
    *
    * @throws TypeValueException
    *    if the specified name does not denote an existing item.
    */
   public static Item getItemByName(String name)
   throws TypeValueException {

      if (name != null) {
         Object o = SINGLETON._namesToItems.get(name);
         if (o != null) {
            return (Item) o;
         } else {
            throw new TypeValueException(SINGLETON, name);
         }
      } else {
         return null;
      }
   }

   public Object fromStringImpl(String value)
   throws TypeValueException {
      return getItemByValue(value);
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Item of the <em>]]></xsl:text>
		<xsl:value-of select="$type" />
		<xsl:text><![CDATA[</em> enumeration type.
    * The following items are defined in this type:
    *
    * <ul>]]></xsl:text>
		<xsl:for-each select="enum/item">
			<xsl:variable name="itemName">
				<xsl:choose>
					<xsl:when test="@name">
						<xsl:value-of select="@name" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@value" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="fieldName">
				<xsl:call-template name="name_for_itemfield">
					<xsl:with-param name="itemName" select="$itemName" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:text><![CDATA[
    *    <li>{@link #]]></xsl:text>
			<xsl:value-of select="$fieldName" />
			<xsl:text> </xsl:text>
			<xsl:value-of select="$fieldName" />
			<xsl:text>}</xsl:text>
		</xsl:for-each>
		<xsl:text><![CDATA[
    * </ul>
    */
   public static final class Item
   extends EnumItem {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Item</code> with the specified name and value.
       *
       * @param name
       *    the symbolic (friendly) name for the enumeration value, not
       *    <code>null</code>.
       *
       * @param value
       *    the actual value of the enumeration item, not <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>name == null || value == null</code>.
       */
      private Item(String name, String value)
      throws IllegalArgumentException {
         super(name, value);
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }]]></xsl:text>
		</xsl:if>

		<xsl:text>
}
</xsl:text>
	</xsl:template>

	<xsl:template match="enum/item" mode="field">
		<xsl:variable name="itemName">
			<xsl:choose>
				<xsl:when test="@name">
					<xsl:value-of select="@name" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@value" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text><![CDATA[

   /**
    * The <em>]]></xsl:text>
		<xsl:value-of select="$itemName" />
		<xsl:text><![CDATA[</em> item.
    */
   public static final Item ]]></xsl:text>
		<xsl:call-template name="name_for_itemfield">
			<xsl:with-param name="itemName" select="$itemName" />
		</xsl:call-template>
		<xsl:text> = new Item("</xsl:text>
		<xsl:value-of select="$itemName" />
		<xsl:text>", "</xsl:text>
		<xsl:value-of select="@value" />
		<xsl:text>");
</xsl:text>
	</xsl:template>

	<xsl:template name="name_for_itemfield">
		<xsl:param name="itemName" />
		<xsl:call-template name="toupper">
			<xsl:with-param name="text" select="translate($itemName, ' .-/', '____')" />
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
