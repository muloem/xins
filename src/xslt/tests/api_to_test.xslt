<?xml version="1.0" encoding="UTF-8" ?>
<!--
 -*- mode: Fundamental; tab-width: 4; -*-
 ex:ts=4

 XSLT that generates the unit tests of the function examples.

 $Id$

 Copyright 2003-2005 Wanadoo Nederland B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="text"/>

	<xsl:param name="package"      />

	<xsl:template match="api">
		<xsl:text><![CDATA[/*
 * $]]><![CDATA[Id$
 */
package ]]></xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text>;

/**
 * Testcase that includes all the tests for the </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[ API.
 *
 * @version $]]><![CDATA[Revision$ $]]><![CDATA[Date$
 */
public class APITests extends junit.framework.TestCase {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never &lt;code&gt;null&lt;/code&gt;.
    */
   public static junit.framework.Test suite() {
      junit.framework.TestSuite suite = new junit.framework.TestSuite();

      // Add all tests]]></xsl:text>
		<xsl:for-each select="function">
			<xsl:text>
      suite.addTestSuite(</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>Tests.class);</xsl:text>
		</xsl:for-each>	
		<xsl:text>

      return suite;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new &lt;code&gt;APITests&lt;/code&gt; test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public APITests(String name) {
      super(name);
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected void setUp() throws Exception {
   }

   protected void tearDown() throws Exception {
   }
}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
