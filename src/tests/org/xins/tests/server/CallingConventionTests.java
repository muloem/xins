/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.http.HTTPCallRequest;
import org.xins.common.http.HTTPCallResult;
import org.xins.common.http.HTTPServiceCaller;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.HexConverter;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

import org.xins.tests.server.servlet.HTTPServletHandler;

/**
 * Tests for XINS call convention.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class CallingConventionTests extends TestCase {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(CallingConventionTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The random number generator.
    */
   private final static Random RANDOM = new Random();

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallingConventionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public CallingConventionTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The HTTP server used to handle the requests.
    */
   private HTTPServletHandler _httpServer;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Starts the HTTP server with the correct parameters.
    */
   protected void setUp() throws ServletException, IOException {
      File xinsProps = new File(System.getProperty("user.dir"), "src/tests/xins.properties".replace('/', File.separatorChar));
      System.setProperty("org.xins.server.config", xinsProps.getAbsolutePath());
      String warLocation = "src/tests/build/webapps/allinone/allinone.war".replace('/', File.separatorChar);
      File warFile = new File(System.getProperty("user.dir"), warLocation);

      // Start the web server
      //System.out.println("Web server set up.");
      _httpServer = new HTTPServletHandler(warFile);
   }

   /**
    * Tests the standard calling convention which should be the default.
    */
   public void testStandardCallingConvention1() throws Throwable {
      callResultCodeStandard(null);
   }

   /**
    * Tests the standard calling convention by passing xins-std as argument.
    */
   public void testStandardCallingConvention2() throws Throwable {
      callResultCodeStandard("xins-std");
   }

   /**
    * Tests the standard calling convention by passing an unknown calling convention.
    */
   public void testStandardCallingConvention3() throws Throwable {
      callResultCodeStandard("xins-bla");
   }

   /**
    * Calls the ResultCode function and expect the standard calling convention back.
    *
    * @param convention
    *    the name of the calling convention parameter, or <code>null</code>
    *    if no calling convention parameter should be sent.
    *
    * @throw Throwable
    *    if anything goes wrong.
    */
   public void callResultCodeStandard(String convention) throws Throwable {
      FastStringBuffer buffer = new FastStringBuffer(16);
      HexConverter.toHexString(buffer, RANDOM.nextLong());
      String randomFive = buffer.toString().substring(0, 5);

      Element result1 = callResultCode(convention, randomFive);
      assertNull("The method returned an error code for the first call: " + result1.getAttribute("errorcode"), result1.getAttribute("errorcode"));
      assertNull("The method returned a code attribute for the first call: " + result1.getAttribute("code"), result1.getAttribute("code"));
      assertNull("The method returned a success attribute for the first call: " + result1.getAttribute("success"), result1.getAttribute("success"));
      
      Element result2 = callResultCode(convention, randomFive);
      assertNotNull("The method did not return an error code for the second call.", result2.getAttribute("errorcode"));
      assertNull("The method returned a code attribute for the second call: " + result2.getAttribute("code"), result2.getAttribute("code"));
      assertNull("The method returned a success attribute for the second call: " + result2.getAttribute("success"), result2.getAttribute("success"));
   }

   /**
    * Tests the old style calling convention.
    */
   public void testOldCallingConvention1() throws Throwable {
      FastStringBuffer buffer = new FastStringBuffer(16);
      HexConverter.toHexString(buffer, RANDOM.nextLong());
      String randomFive = buffer.toString().substring(0, 5);

      Element result1 = callResultCode("xins-old", randomFive);
      assertNull("The method returned an error code for the first call: " + result1.getAttribute("errorcode"), result1.getAttribute("errorcode"));
      assertNull("The method returned a code attribute for the first call: " + result1.getAttribute("code"), result1.getAttribute("code"));
      assertNotNull("The method did not return a success attribute for the first call.", result1.getAttribute("success"));
      
      Element result2 = callResultCode("xins-old", randomFive);
      assertNotNull("The method did not return an error code for the second call.", result2.getAttribute("errorcode"));
      assertNotNull("The method did not return a code attribute for the second call.", result2.getAttribute("code"));
      assertNotNull("The method did not return a success attribute for the second call.", result2.getAttribute("success"));
      assertEquals("The code and errorcode are different.", result2.getAttribute("code"), result2.getAttribute("errorcode"));
   }

   /**
    * Call the ResultCode function with the specified calling convention.
    *
    * @param convention
    *    the name of the calling convention parameter, or <code>null</code>
    *    if no calling convention parameter should be sent.
    *
    * @param inputText
    *    the value of the parameter to send as input.
    *
    * @return
    *    the parsed result as an Element.
    *
    * @throw Throwable
    *    if anything goes wrong.
    */
   private Element callResultCode(String convention, String inputText) throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/", 2000);
      BasicPropertyReader params = new BasicPropertyReader();
      params.set("_function",  "ResultCode");
      params.set("inputText",  inputText);
      if (convention != null) {
         params.set("_convention", convention);
      }
      HTTPCallRequest request = new HTTPCallRequest(params);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);

      HTTPCallResult result = caller.call(request);
      byte[] data = result.getData();
      ElementParser parser = new ElementParser();
      return parser.parse(data);
   }

   /**
    * Stop the server.
    */
   protected void tearDown() {
      _httpServer.close();
   }
}