/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.text;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.text.FormatException;
import org.xins.common.text.NonASCIIException;
import org.xins.common.text.URLEncoding;

/**
 * Tests for class <code>URLEncoding</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class URLEncodingTests extends TestCase {

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
      return new TestSuite(URLEncodingTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>URLEncodingTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public URLEncodingTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testURLEncodingDecode() throws Throwable {

      try {
         URLEncoding.decode(null);
         fail("Expected IllegalArgumentException for URLEncoding.decode(null).");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      assertEquals("", URLEncoding.decode(""));

      // Test format failures
      String[] erroneous = new String[] {
         "%", "a%", "aa%", "aa%a", "%a", "%%12", "aa%00%ag", "%g1", "%1g8",
         "%12abgg%Fg", "%GG", "%1G", "%G1"
      };
      for (int i = 0; i < erroneous.length; i++) {
         String url = erroneous[i];
         try {
            URLEncoding.decode(url);
            fail("Expected URLEncoding.decode(\"" + url + "\") to throw a FormatException.");
            return;
         } catch (FormatException exception) {
            // as expected
         }
      }

      // Test non-ASCII
      String[] nonAscii = new String[] {
         "abcd%ff", "abcd%FF", "%ff", "%FF", "%80", "abcd%80", "%80abcd",
         "%a0", "abcd%a0", "%a1aaaaa"
      };
      for (int i = 0; i < nonAscii.length; i++) {
         String url = nonAscii[i];
         try {
            URLEncoding.decode(url);
            fail("Expected URLEncoding.decode(\"" + url + "\") to throw a NonASCIIException.");
            return;
         } catch (NonASCIIException exception) {
            // as expected
         }
      }

      // Test success scenarios
      String[] ok = new String[] {
         "abcdABCD%20+",       "abcdABCD  ",
         "%20",                " ",
         "+",                  " ",
         "%2F%2f",             "//",
         "%2f%2F",             "//",
      };
      for (int i = 0; i < (ok.length / 2); i+=2) {
         String input    = ok[i];
         String actual   = URLEncoding.decode(input);
         String expected = ok[i+1];
         String message  = "Expected \""
                         + expected
                         + "\" instead of \""
                         + actual
                         + " for the input string \""
                         + input
                         + "\".";
         assertEquals(message, expected, actual);
      }
   }
}
