<?xml version="1.0" encoding="US-ASCII"?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the Request classes.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" />

	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../java.xslt" />
	<xsl:include href="../rcs.xslt"  />
	<xsl:include href="../types.xslt"  />

	<xsl:template match="function">
		<xsl:variable name="version">
			<xsl:call-template name="revision2string">
				<xsl:with-param name="revision">
					<xsl:value-of select="//function/@rcsversion" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="functionName" select="@name" />
		<xsl:variable name="className">
			<xsl:value-of select="$functionName" />
			<xsl:text>Function</xsl:text>
		</xsl:variable>

		<xsl:call-template name="java-header" />
		<xsl:text>package </xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

/**
 * Representation of the <em>]]></xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text><![CDATA[</em> function.
 *
 * @see CAPI
 * @see ]]></xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text>Request
 * @see </xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text>Result
 */
final class </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>
extends org.xins.client.AbstractCAPIFunction {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The one and only instance of this class.
    */
   static final </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text> SINGLETON = new </xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------
		
   /**
    * Constructs a new <code>]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text><![CDATA[</code> instance.
    */
   public ]]></xsl:text>
		<xsl:value-of select="$className" />
		<xsl:text>() {
      super("</xsl:text>
		<xsl:value-of select="$functionName" />
		<xsl:text>",
            new org.xins.common.constraint.Constraint[] {</xsl:text>
		<!-- Required input parameters -->
		<xsl:for-each select="input/param[@required='true']">
			<xsl:text>
               new org.xins.common.constraint.RequiredParamConstraint("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>"),</xsl:text>
		</xsl:for-each>

		<!-- TODO: Input parameter types -->
		<xsl:for-each select="input/param[string-length(@type) &gt; 1 and not (@type = '_text')]">
			<xsl:text>
               new org.xins.common.constraint.TypedParamConstraint("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>", </xsl:text>

			<xsl:call-template name="javatypeclass_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>

			<xsl:text>.SINGLETON),</xsl:text>
		</xsl:for-each>

		<!-- TODO: Input parameter combos type 1 -->
		<!-- TODO: Input parameter combos type 2 -->
		<!-- TODO: Input parameter combos type 3 -->
		<xsl:text>},
            new org.xins.common.constraint.Constraint[] {</xsl:text>

		<!-- Required output parameters -->
		<xsl:for-each select="output/param[@required='true']">
			<xsl:text>
               new org.xins.common.constraint.RequiredParamConstraint("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>"),</xsl:text>
		</xsl:for-each>

		<!-- Output parameter types -->
		<xsl:for-each select="output/param[string-length(@type) &gt; 1 and not (@type = '_text')]">
			<xsl:text>
               new org.xins.common.constraint.TypedParamConstraint("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>", </xsl:text>

			<xsl:call-template name="javatypeclass_for_type">
				<xsl:with-param name="project_file" select="$project_file" />
				<xsl:with-param name="api"          select="$api"          />
				<xsl:with-param name="specsdir"     select="$specsdir"     />
				<xsl:with-param name="type"         select="@type"         />
			</xsl:call-template>

			<xsl:text>.SINGLETON),</xsl:text>
		</xsl:for-each>

		<!-- TODO: Output parameter combos type 1 -->
		<!-- TODO: Output parameter combos type 2 -->
		<!-- TODO: Output parameter combos type 3 -->
		<xsl:text>});
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
</xsl:text>
	</xsl:template>
</xsl:stylesheet>
