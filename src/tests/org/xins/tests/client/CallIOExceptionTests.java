/*
 * $Id$
 */
package org.xins.tests.client;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.CallIOException;

/**
 * Tests for class <code>CallIOException</code>.
 *
 * @version $Revision$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class CallIOExceptionTests extends TestCase {

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
      return new TestSuite(CallIOExceptionTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallIOExceptionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public CallIOExceptionTests(String name) {
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
   public void testCallIOException() throws Throwable {

      // Test constructor with null argument
      try {
         new CallIOException(null);
         fail("Expected CallIOException(null) to throw an IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }

      // Test constructor with non-null argument
      IOException ioe = new IOException();
      CallIOException cie = new CallIOException(ioe);
      assertEquals(ioe, cie.getCause());
   }
}
