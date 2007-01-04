<?xml version="1.0" encoding="UTF-8" ?>
<!--
 XSLT that generates the unit tests of the function examples.

 $Id$

 Copyright 2003-2007 Orange Nederland Breedband B.V.
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

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.servlet.container.HTTPServletHandler;

/**
 * Testcase that includes all the tests for the </xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text><![CDATA[ API.
 *
 * @version $]]><![CDATA[Revision$ $]]>Date$
 */
public class APITests extends TestCase {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The Servlet server running the API. &lt;code&gt;null&lt;/code&gt; if the server is not started.
    */
   private static HTTPServletHandler API_SERVER;

   /**
    * Flag that indicates that the API has been started.
    */
   private static boolean API_STARTED = false;


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

      TestSuite suite = new TestSuite();

      if ("true".equals(System.getProperty("test.start.server"))) {
         suite.addTestSuite(StartServer.class);
      }
      // Add all tests</xsl:text>
		<xsl:for-each select="function">
			<xsl:text>
      suite.addTestSuite(</xsl:text>
			<xsl:value-of select="@name" />
			<xsl:text>Tests.class);</xsl:text>
		</xsl:for-each>
		<xsl:text>
      if ("true".equals(System.getProperty("test.start.server"))) {
         suite.addTestSuite(StopServer.class);
      }

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
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Starts the web server.
    */
   public static class StartServer extends TestCase {

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
         return new TestSuite(StartServer.class);
      }


      //-------------------------------------------------------------------------
      // Constructor
      //-------------------------------------------------------------------------

      /**
       * Constructs a new &lt;code&gt;StartServer&lt;/code&gt; test suite with
       * the specified name. The name will be passed to the superconstructor.
       *
       * @param name
       *    the name for this test suite.
       */
      public StartServer(String name) {
         super(name);
      }


      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

      public void testStartServer() throws Exception {

         String warLocation = "build/webapps/</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>/</xsl:text>
		<xsl:value-of select="@name" />
		<xsl:text>.war".replace('/', File.separatorChar);
         File warFile = new File(System.getProperty("user.dir"), warLocation);
         int port = 8080;
         if (System.getProperty("servlet.port") != null &amp;&amp; !System.getProperty("servlet.port").equals("")) {
            port = Integer.parseInt(System.getProperty("servlet.port"));
         }

         // Start the web server
         API_SERVER = new HTTPServletHandler(warFile, port, true);
         API_STARTED = true;
      }
   }

   /**
    * Stops the web server.
    */
   public static class StopServer extends TestCase {

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
         return new TestSuite(StopServer.class);
      }


      //-------------------------------------------------------------------------
      // Constructor
      //-------------------------------------------------------------------------

      /**
       * Constructs a new &lt;code&gt;StopServer&lt;/code&gt; test suite with
       * the specified name. The name will be passed to the superconstructor.
       *
       * @param name
       *    the name for this test suite.
       */
      public StopServer(String name) {
         super(name);
      }


      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

      public void testStopServer() throws Exception {

         if (API_STARTED) {
            API_SERVER.close();
         }
      }
   }
}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
