/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.XINSServiceCaller;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.service.UnsupportedProtocolException;

import org.xins.tests.server.servlet.HTTPServletHandler;

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
      File xinsProps = new File(System.getProperty("user.dir"), "src/tests/xins.properties");
      System.setProperty("org.xins.server.config", xinsProps.getAbsolutePath());
      String warLocation = "src/tests/build/webapps/allinone/allinone.war".replace('/', File.separatorChar);
      File warFile = new File(System.getProperty("user.dir"), warLocation);

      // Start the web server
      //System.out.println("Web server set up.");
      _httpServer = new HTTPServletHandler(warFile);
   }

   /**
    * Tests the constructor arguments.
    */
   public void testConstructor() throws Throwable {
      try {
         XINSServiceCaller upe = new XINSServiceCaller(null);
         fail("HTTPServiceCaller did not throw an exception with a <null> argument for the constructor.");
      } catch (IllegalArgumentException ex) {
         // As excepted
      }

      TargetDescriptor descriptor = new TargetDescriptor("hTtP://localhost:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);


      try {
         TargetDescriptor descriptor2 = new TargetDescriptor("blah://localhost:8080/");
         XINSServiceCaller caller2 = new XINSServiceCaller(descriptor2);
         fail("The \"blah\" protocol should not be supported.");
      } catch (UnsupportedProtocolException upe) {
         // As expected.
      }
   }
}
