<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

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

	<xsl:include href="../casechange.xslt"    />
	<xsl:include href="../escapepattern.xslt" />
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
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>Unable to determine kind of type. Seems to be neither enum nor pattern type.</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="superclass">
		<xsl:choose>
			<xsl:when test="$kind = 'enum'">org.xins.types.EnumType</xsl:when>
			<xsl:when test="$kind = 'pattern'">org.xins.types.PatternType</xsl:when>
			<xsl:when test="$kind = 'properties'">org.xins.types.standard.Properties</xsl:when>
		</xsl:choose>
	</xsl:variable>

	<xsl:template match="type">
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;

import org.xins.types.EnumItem;
import org.xins.util.MandatoryArgumentChecker;

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
         new EnumItem("</xsl:text>
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
				<xsl:call-template name="escapepattern">
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
    *    cannot be <code>null</code>.
    *
    * @return
    *    the matching {@link Item} instance, or <code>null</code> if there is
    *    none.
    *
    * @throws IllegalArgumentException
    *    if <code>value == null</code>.
    */
   public Item getItemByValue(String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      return (Item) _valuesToItems.get(value);
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
				<xsl:call-template name="toupper">
					<xsl:with-param name="text" select="translate($itemName, ' ', '_')" />
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
		<xsl:call-template name="toupper">
			<xsl:with-param name="text" select="translate($itemName, ' ', '_')" />
		</xsl:call-template>
		<xsl:text> = new Item("</xsl:text>
		<xsl:value-of select="$itemName" />
		<xsl:text>", "</xsl:text>
		<xsl:value-of select="@value" />
		<xsl:text>");
</xsl:text>
	</xsl:template>
</xsl:stylesheet>
