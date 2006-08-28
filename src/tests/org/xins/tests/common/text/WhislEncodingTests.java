/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.text;

import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.text.ParseException;
import org.xins.common.text.WhislEncoding;

/**
 * Tests for class <code>WhislEncoding</code>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 */
public class WhislEncodingTests extends TestCase {

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
      return new TestSuite(WhislEncodingTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>WhislEncodingTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public WhislEncodingTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testWhislEncodingEncode() throws Throwable {
      try {
         WhislEncoding.encode(null);
         fail("Expected IllegalArgumentException for WhislEncoding.encode(null).");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }
   }

   public void testWhislEncoding()
   throws Exception {
      String input, expected;

      input    = "";
      expected = input;
      doTestWhislEncoding(input, expected);

      input    = "abcdefghijklmnopqrstuvwxyz"
               + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
               + "0123456789"
               + "-_.*";
      expected = input;
      doTestWhislEncoding(input, expected);

      input    = "a\u1234 ~!";
      expected = "a$1234+%7E%21";
      doTestWhislEncoding(input, expected);

      // TODO: Test '+' in input
   }

   private void doTestWhislEncoding(String input, String expected)
   throws Exception {
      String encoded = WhislEncoding.encode(input);
      String message = "Expected encoded version of \""
                     + input
                     + "\" to be \""
                     + expected
                     + "\" instead of \""
                     + encoded
                     + "\".";
      assertEquals(message, expected, encoded);
   }
}
