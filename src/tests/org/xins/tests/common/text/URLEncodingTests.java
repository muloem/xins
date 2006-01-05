/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.text;

import java.net.URLDecoder;
import java.net.URLEncoder;

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
         } catch (FormatException exception) {
            fail("Expected URLEncoding.decode(\"" + url + "\") to succeed.");
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

   public void testEncode() throws Throwable {

      // Test that a null argument fails
      try {
         URLEncoding.encode(null);
         fail("URLEncoding.encode(null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      // Test Unicode character
      String input  = "\u0080";
      String result = URLEncoding.encode(input);
      assertEquals("Incorrect result:" + result, "%80", result);

      input  = "HelloThere0999";
      result = URLEncoding.encode(input);
      assertEquals(input, result);

      input  = "Hello there";
      result = URLEncoding.encode(input);
      assertEquals("Hello+there", result);

      // Make sure java.net.URLEncoder produces an equivalent result
      input = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
              "01234567890`-=[]\\;',./ ~!@#$%^&*()_+{}|:\"<>?";
      result = URLEncoding.encode(input);
      assertEquals(URLEncoder.encode(input), result);
   }

   public void testDecode() throws Throwable {

      // Test that a null argument fails
      try {
         URLEncoding.decode(null);
         fail("URLEncoding.decode(null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      // Make sure that characters higher than 255 make the conversion fail
      failDecode("\u0080");
      failDecode("A\u0080");
      failDecode("AA\u0080");
      failDecode("\u0080 ");
      failDecode("\u0080 1");
      
      compareDecode("%80", "\u0080");
      failDecode("%u80");
      compareDecode("A%80", "A\u0080");
      failDecode("%80 ");
      
      // Before-last character cannot be a percentage sign
      failDecode("abcd%a");

      // Test unicode characters
      compareDecode("%u0080", "\u0080");
      compareDecode("a%u0080", "a\u0080");
      compareDecode("aa%U0080", "aa\u0080");
      compareDecode("%u0080a", "\u0080a");
      compareDecode("%u0080aa", "\u0080aa");

      compareDecode("HelloThere0999", "HelloThere0999");
      compareDecode("+", " ");
      compareDecode("a+", "a ");
      compareDecode("aa+", "aa ");
      compareDecode("+a", " a");
      compareDecode("+aa", " aa");
      compareDecode("+aa+", " aa ");
      compareDecode("Hello+there", "Hello there");
      compareDecode("Hello%20there", "Hello there");

      // Make sure java.net.URLDecoder produces an equivalent result
      String input = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
              "01234567890`-=[]\\;',./ ~!@#$%^&*()_+{}|:\"<>?";
      String result = URLEncoding.encode(input);
      assertEquals(URLDecoder.decode(result), input);
      assertEquals(URLDecoder.decode(result), URLEncoding.decode(result));
   }
   
   /**
    * Compares if the input String is decoded as expected.
    */
   private void compareDecode(String input, String expect) {
      String result = URLEncoding.decode(input);
      assertEquals("The input '" + input + "' was decoded to '" + result +
           "' instead of '" + expect +"'.", expect, result);
   }

   /**
    * Tests that a FormatException is thrown when we try to decode the input.
    */
   private void failDecode(String input) {
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a FormatException.");
      } catch (FormatException exception) { /* as expected */ }
   }
}
