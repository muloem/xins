<?xml version="1.0" encoding="UTF-8" ?>
<!--
 XSLT that generates the unit tests of the function examples.

 $Id$

 Copyright 2003-2007 Orange Nederland Breedband B.V.
 See the COPYRIGHT file for redistribution and use restrictions.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="text"/>

	<xsl:param name="api" />
	<xsl:param name="package" />

	<xsl:include href="../xml_to_java.xslt"  />

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
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementBuilder;
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

   /**
    * The XINServiceCaller of the API.
    */
   private XINSServiceCaller _caller;

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

   public void test</xsl:text>
		<xsl:value-of select="/function/@name" />
		<xsl:text>Example</xsl:text>
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
			<xsl:call-template name="pcdata_to_java_string">
				<xsl:with-param name="text" select="." />
			</xsl:call-template>
			<xsl:text>");</xsl:text>
		</xsl:for-each>
		<xsl:if test="input-data-example/element-example">
			<xsl:text>
      ElementBuilder dataSectionBuilder = new ElementBuilder("data");</xsl:text>
			<xsl:apply-templates select="input-data-example/element-example">
				<xsl:with-param name="parent" select="'dataSectionBuilder'" />
			</xsl:apply-templates>
			<xsl:text>
      Element dataSection = dataSectionBuilder.createElement();
      request.setDataSection(dataSection);</xsl:text>
		</xsl:if>
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
				<xsl:text>", uxcex.getErrorCode());</xsl:text>
				<xsl:apply-templates select="output-example">
					<xsl:with-param name="resultVariable" select="'uxcex'" />
				</xsl:apply-templates>
				<xsl:apply-templates select="output-data-example/element-example | data-example/element-example">
					<xsl:with-param name="parent" select="'uxcex.getDataElement()'" />
				</xsl:apply-templates>
			<xsl:text>
      }</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>
      XINSCallResult result = _caller.call(request);</xsl:text>
				<xsl:apply-templates select="output-example">
					<xsl:with-param name="resultVariable" select="'result'" />
				</xsl:apply-templates>
				<xsl:apply-templates select="output-data-example/element-example | data-example/element-example">
					<xsl:with-param name="parent" select="'result.getDataElement()'" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text>
   }</xsl:text>
	</xsl:template>

	<!-- Check the result -->
	<xsl:template match="output-example">
		<xsl:param name="resultVariable" />

		<xsl:text>
      assertEquals("The returned parameter \"</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>\" is incorrect.", "</xsl:text>
		<xsl:call-template name="pcdata_to_java_string">
			<xsl:with-param name="text" select="." />
		</xsl:call-template>
		<xsl:text>", </xsl:text>
		<xsl:value-of select="$resultVariable" />
		<xsl:text>.getParameter("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>"));</xsl:text>
	</xsl:template>

	<!-- Check the returned data section -->
	<xsl:template match="data-example//element-example | output-data-example//element-example">
		<xsl:param name="parent" />

		<xsl:variable name="elementVariable">
			<xsl:choose>
				<xsl:when test="../../data-example or ../../output-data-example">
					<xsl:value-of select="concat(translate(@name, '-', '_'), position())" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($parent, translate(@name, '-', '_'), position())" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>
      Element </xsl:text>
		<xsl:value-of select="$elementVariable" />
		<xsl:text> = (Element) </xsl:text>
		<xsl:value-of select="$parent" />
		<xsl:text>.getChildElements().get(</xsl:text>
		<xsl:value-of select="position() - 1" />
		<xsl:text>);
      assertEquals("Incorrect element.", "</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>" , </xsl:text>
		<xsl:value-of select="$elementVariable" />
		<xsl:text>.getLocalName());</xsl:text>
		<xsl:for-each select="attribute-example">
			<xsl:text>
      assertEquals("The returned attribute \"</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>\" is incorrect.", "</xsl:text>
			<xsl:call-template name="pcdata_to_java_string">
				<xsl:with-param name="text" select="." />
			</xsl:call-template>
			<xsl:text>", </xsl:text>
			<xsl:value-of select="$elementVariable" />
			<xsl:text>.getAttribute("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>"));</xsl:text>
		</xsl:for-each>
		<xsl:if test="pcdata-example">
			<xsl:text>
      assertEquals("Incorrect PCDATA value.", "</xsl:text>
			<xsl:call-template name="pcdata_to_java_string">
				<xsl:with-param name="text" select="pcdata-example/text()" />
			</xsl:call-template>
			<xsl:text>", </xsl:text>
			<xsl:value-of select="$elementVariable" />
			<xsl:text>.getText());</xsl:text>
		</xsl:if>
		<xsl:apply-templates select="element-example">
			<xsl:with-param name="parent" select="$elementVariable" />
		</xsl:apply-templates>
	</xsl:template>

	<!-- Set the input data section in the request -->
	<xsl:template match="input-data-example//element-example">
		<xsl:param name="parent" />

		<xsl:variable name="elementVariable">
			<xsl:choose>
				<xsl:when test="../../input-data-example">
					<xsl:value-of select="concat(translate(@name, '-', '_'), position())" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($parent, translate(@name, '-', '_'), position())" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:text>
      ElementBuilder </xsl:text>
		<xsl:value-of select="$elementVariable" />
		<xsl:text> = new ElementBuilder("</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>");</xsl:text>
		<xsl:for-each select="attribute-example">
			<xsl:text>
      </xsl:text>
			<xsl:value-of select="$elementVariable" />
			<xsl:text>.setAttribute("</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>", "</xsl:text>
			<xsl:call-template name="pcdata_to_java_string">
				<xsl:with-param name="text" select="." />
			</xsl:call-template>
			<xsl:text>");</xsl:text>
		</xsl:for-each>
		<xsl:apply-templates select="element-example">
			<xsl:with-param name="parent" select="$elementVariable" />
		</xsl:apply-templates>
		<xsl:text>
      </xsl:text>
		<xsl:value-of select="$parent" />
		<xsl:text>.addChild(</xsl:text>
		<xsl:value-of select="$elementVariable" />
		<xsl:text>.createElement());</xsl:text>
	</xsl:template>
</xsl:stylesheet>
