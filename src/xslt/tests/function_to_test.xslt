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

	<xsl:param name="project_home" />
	<xsl:param name="project_file" />
	<xsl:param name="specsdir"     />
	<xsl:param name="package"      />
	<xsl:param name="api"          />
	<xsl:param name="api_file"     />

	<xsl:include href="../types.xslt"  />
	
	<xsl:template match="function">
		<xsl:text><![CDATA[/*
 * $]]><![CDATA[Id$
 */
package ]]></xsl:text>
		<xsl:value-of select="$package" />
		<xsl:text><![CDATA[;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.UnsuccessfulXINSCallException;
import org.xins.client.XINSCallRequest;
import org.xins.client.XINSCallResult;
import org.xins.client.XINSServiceCaller;

import org.xins.common.service.TargetDescriptor;

/**
 * Implementation of the <code>]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[</code> tests.
 *
 * @version $]]><![CDATA[Revision$ $]]><![CDATA[Date$
 */
public class ]]></xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>Tests extends TestCase {

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
   public static Test suite() {
      return new TestSuite(</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>Tests.class);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new &lt;code&gt;</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>Tests&lt;/code&gt; test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>Tests(String name) {
      super(name);
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The XINServiceCaller of the API.
    */
   private XINSServiceCaller _caller;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected void setUp() throws Exception {
      String target = System.getProperty("test.environment");
      if (target == null || target.trim().equals("")) {
         target = "http://localhost:8080/</xsl:text>
		<xsl:value-of select="$api" />
		<xsl:text>/";
      }
      _caller = new XINSServiceCaller(new TargetDescriptor(target));
   }

   protected void tearDown() throws Exception {
   }</xsl:text>
		<xsl:apply-templates select="example" mode="method" />
		<xsl:text>
}</xsl:text>
	</xsl:template>

	<xsl:template match="example" mode="method">
		<xsl:text>

   public void testExample</xsl:text>
		<xsl:value-of select="position()" />
		<xsl:text>() throws Exception {
      XINSCallRequest request = new XINSCallRequest("</xsl:text>
		<xsl:value-of select="../@name" />
		<xsl:text>");</xsl:text>
		<xsl:for-each select="input-example">
			<xsl:text>
      request.setParameter("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>", "</xsl:text>
			<xsl:value-of select="." />
			<xsl:text>");</xsl:text>
		</xsl:for-each>
		<xsl:choose>
			<xsl:when test="@resultcode">
				<xsl:text>
      try {
         XINSCallResult result = _caller.call(request);
         fail("An error code \"</xsl:text>
				<xsl:value-of select="@resultcode" />
				<xsl:text>\" was expected but did not occur.");
      } catch(UnsuccessfulXINSCallException uxcex) {
         assertEquals("Incorrect error code received.", "</xsl:text>
			<xsl:value-of select="@resultcode" />
			<xsl:text>", uxcex.getErrorCode());
      }</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>
      XINSCallResult result = _caller.call(request);</xsl:text>
				<xsl:for-each select="output-example">
					<xsl:text>
      assertEquals("The returned parameter \"</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>\" is incorrect.", "</xsl:text>
					<xsl:value-of select="." />
					<xsl:text>", result.getParameter("</xsl:text>
					<xsl:value-of select="@name" />
					<xsl:text>"));</xsl:text>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>	
   }</xsl:text>
	</xsl:template>
</xsl:stylesheet>
