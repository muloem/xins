/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.server.API;

/**
 * Tests for class <code>API</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class APITests extends TestCase {

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
      return new TestSuite(APITests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>APITests</code> test suite with
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

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      // empty
   }

   public void testAPI() throws Throwable {

      // Call constructor with null name (should fail)
      try {
         new TestAPI(null);
         fail("Expected API(null) to throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Call constructor with empty name (should fail)
      try {
         new TestAPI("");
         fail("Expected API(\"\") to throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Construct an instance
      String apiName = "My API";
      long before = System.currentTimeMillis();
      TestAPI api = new TestAPI(apiName);
      long after = System.currentTimeMillis();

      // Check getStartupTimestamp()
      long startup = api.getStartupTimestamp();
      assertTrue(before <= startup);
      assertTrue(after >= startup);

      // Check getName();
      assertEquals(apiName, api.getName());

      // Check getProperties()
      assertNotNull(api.getProperties());

      // Check getTimeZone()
      assertNotNull(api.getTimeZone());

      // Try bootstrapping it with no properties
      api.bootstrap(new BasicPropertyReader());
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   private class TestAPI extends API {

      TestAPI(String name) {
         super(name);
      }
   }
}
