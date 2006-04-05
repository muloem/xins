/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.http;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

import org.xins.common.http.HTTPCallConfig;
import org.xins.common.http.HTTPMethod;

/**
 * Tests for class <code>HTTPCallConfig</code>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class HTTPCallConfigTests extends TestCase {

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
      return new TestSuite(HTTPCallConfigTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HTTPCallConfigTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    * 
    * 
    * @param name
    *    the name for this test suite.
    */
   public HTTPCallConfigTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testHTTPCallConfig() throws Exception {

      // Test first constructor
      HTTPCallConfig config = new HTTPCallConfig();
      assertEquals("Incorrect HTTP method.", HTTPMethod.POST, config.getMethod());
      config.setMethod(HTTPMethod.GET);
      assertEquals("Incorrect HTTP method.", HTTPMethod.GET, config.getMethod());
      config.setUserAgent("Anthony");
      assertEquals("Incorrect HTTP agent.", "Anthony", config.getUserAgent());
      config.describe();
   }
}