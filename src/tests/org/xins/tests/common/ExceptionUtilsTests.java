/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.ExceptionUtils;

/**
 * Tests for class <code>ExceptionUtils</code>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class ExceptionUtilsTests extends TestCase {

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
      return new TestSuite(ExceptionUtilsTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ExceptionUtilsTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ExceptionUtilsTests(String name) {
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

   private void reset() {
      // empty
   }

   /**
    * Tests the constructor.
    */
   public void testGetRootCause() throws Throwable {

      // Test with null argument
      try {
         ExceptionUtils.getRootCause(null);
         fail("Expected ExceptionUtils.getRootCause(null) to throw an IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }

      // Test with IOException with no defined cause
      Exception ex = new Exception();
      Throwable cause = ExceptionUtils.getRootCause(ex);
      assertEquals(ex, cause);

      // Test 2 levels
      Exception ex2 = new Exception(ex);
      cause = ExceptionUtils.getRootCause(ex2);
      assertEquals(ex, cause);

      // Test 3 levels
      Exception ex3 = new Exception(ex2);
      cause = ExceptionUtils.getRootCause(ex3);
      assertEquals(ex, cause);
   }
}
