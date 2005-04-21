/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.client.InvalidResultXINSCallException;
import org.xins.client.XINSCallConfig;
import org.xins.client.XINSCallRequest;
import org.xins.client.XINSCallResult;

import org.xins.client.XINSServiceCaller;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;
import org.xins.common.http.HTTPMethod;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.service.UnsupportedProtocolException;

/**
 * Tests the <code>XINSServiceCaller</code>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class XINSServiceCallerTests extends TestCase {

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
      return new TestSuite(XINSServiceCallerTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>XINSServiceCallerTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public XINSServiceCallerTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Tests the constructor arguments.
    */
   public void testXINSServiceCallerConstructor() throws Throwable {
      XINSServiceCaller caller = new XINSServiceCaller(null);
      assertEquals(null, caller.getDescriptor());

      // TODO: Move this test to a different function
      try {
         TargetDescriptor descriptor2 = new TargetDescriptor("blah://127.0.0.1:8080/");
         caller.setDescriptor(descriptor2);
         fail("The \"blah\" protocol should not be supported.");
      } catch (UnsupportedProtocolException upe) {
         // As expected.
      }

      TargetDescriptor descriptor = new TargetDescriptor("hTtP://127.0.0.1:8080/");
      caller = new XINSServiceCaller(descriptor);

      try {
         TargetDescriptor descriptor2 = new TargetDescriptor("blah://127.0.0.1:8080/");
         XINSServiceCaller caller2 = new XINSServiceCaller(descriptor2);
         fail("The \"blah\" protocol should not be supported.");
      } catch (UnsupportedProtocolException upe) {
         // As expected.
      }
   }
   
   /**
    * Test using the XINSServiceCaller with https
    */
   public void testXINSServiceCallerWithHTTPS() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_GetVersion", null);
      TargetDescriptor descriptor = new TargetDescriptor("https://sourceforge.net/", 10000);
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      try {
         caller.call(request);
         fail("Received result where an exception was expected");
      } catch (InvalidResultXINSCallException exception) {
         // as expected
      }
   }
   
   /**
    * Test the XINSServiceCaller with HTTP GET
    */
   public void testXINSServiceCallerWithGET() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_GetVersion", null);
      XINSCallConfig config = new XINSCallConfig();
      config.setHTTPMethod(HTTPMethod.GET);
      request.setXINSCallConfig(config);
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      XINSCallResult result = caller.call(request);
      PropertyReader parameters = result.getParameters();
      assertNotNull("No java version specified.", parameters.get("java.version"));
      
      BasicPropertyReader parameters2 = new BasicPropertyReader();
      parameters2.set("inputText", "bonjour");
      XINSCallRequest request2 = new XINSCallRequest("ResultCode", parameters2);
      request2.setXINSCallConfig(config);
      caller.call(request2);
   }
}
