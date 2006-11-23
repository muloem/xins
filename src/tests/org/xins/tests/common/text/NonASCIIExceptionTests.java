/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.text;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.text.NonASCIIException;

/**
 * Tests for class <code>NonASCIIException</code>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class NonASCIIExceptionTests extends TestCase {

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
      return new TestSuite(NonASCIIExceptionTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>NonASCIIExceptionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public NonASCIIExceptionTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testNonASCIIException() throws Throwable {
      char invalid = (char) 127;
      char   valid = (char) 128;

      try {
         new NonASCIIException(invalid);
         fail("Expected NonASCIIException((char)" + ((int) invalid) + ") to throw an IllegalArgumentException.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      NonASCIIException e = new NonASCIIException(valid);
   }
}
