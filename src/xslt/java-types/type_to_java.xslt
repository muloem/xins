<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the java representation of the type.

 $Id$
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

	<xsl:variable name="type" select="//type/@name" />
	<xsl:variable name="classname">
		<xsl:call-template name="hungarianUpper">
			<xsl:with-param name="text">
				<xsl:value-of select="$type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="kind">
		<xsl:choose>
			<xsl:when test="type/enum">enum</xsl:when>
			<xsl:when test="type/pattern">pattern</xsl:when>
			<xsl:when test="type/properties">properties</xsl:when>
			<xsl:when test="type/int8">int8</xsl:when>
			<xsl:when test="type/int16">int16</xsl:when>
			<xsl:when test="type/int32">int32</xsl:when>
			<xsl:when test="type/int64">int64</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>Unable to determine kind of type. Seems to be neither enum nor pattern type.</xsl:text>
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
		</xsl:choose>
	</xsl:variable>

	<xsl:output method="text" />

	<xsl:template match="type">
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
   //-------------------------------------------------------------------------

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
						<xsl:value-of select="int8/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Byte.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="int8/@max">
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
						<xsl:value-of select="int16/@min" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Short.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="int16/@max">
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
						<xsl:text>l</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Long.MIN_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>, </xsl:text>
				<xsl:choose>
					<xsl:when test="int64/@max">
						<xsl:value-of select="int64/@max" />
						<xsl:text>l</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Long.MAX_VALUE</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
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
   public Item getItemByValue(String value)
   throws TypeValueException {

      if (value != null) {
         Object o = _valuesToItems.get(value);
         if (o != null) {
            return (Item) o;
         } else {
            throw new TypeValueException(this, value);
         }
      } else {
         return null;
      }
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
