/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.XINSCallConfig;
import org.xins.common.http.HTTPMethod;

/**
 * Tests for class <code>XINSCallConfig</code>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class XINSCallConfigTests extends TestCase {

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
      return new TestSuite(XINSCallConfigTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>XINSCallConfigTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public XINSCallConfigTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testXINSCallConfig() throws Exception {

      // Test first constructor
      XINSCallConfig config = new XINSCallConfig();
      assertEquals("Incorrect HTTP method.", HTTPMethod.POST, config.getHTTPMethod());
      config.setHTTPMethod(HTTPMethod.GET);
      assertEquals("Incorrect HTTP method.", HTTPMethod.GET, config.getHTTPMethod());
      config.describe();
   }
}