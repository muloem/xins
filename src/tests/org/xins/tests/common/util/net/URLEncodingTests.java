/*
 * $Id$
 */
package org.xins.tests.common.util.net;

import java.net.URLDecoder;
import java.net.URLEncoder;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.common.net.URLEncoding;
import org.xins.common.text.FormatException;
import org.xins.common.text.NonASCIIException;

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

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      // empty
   }

   private void reset() {
      // empty
   }

   public void testEncode() throws Throwable {

      // Test that a null argument fails
      try {
         URLEncoding.encode(null);
         fail("URLEncoding.encode(null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      // Make sure that characters higher than 127 make the conversion fail
      try {
         URLEncoding.encode("\u0080");
         fail("URLEncoding.encode(\\u0080) should throw a NonASCIIException.");
      } catch (NonASCIIException exception) { /* as expected */ }

      String input  = "HelloThere0999";
      String result = URLEncoding.encode(input);
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

      // Make sure that characters higher than 127 make the conversion fail
      String input = "\u0080";
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a FormatException.");
      } catch (FormatException exception) { /* as expected */ }

      input = "A\u0080";
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a FormatException.");
      } catch (FormatException exception) { /* as expected */ }

      input = "AA\u0080";
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a FormatException.");
      } catch (FormatException exception) { /* as expected */ }

      input = "\u0080 ";
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a FormatException.");
      } catch (FormatException exception) { /* as expected */ }

      input = "\u0080 1";
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a FormatException.");
      } catch (FormatException exception) { /* as expected */ }

      // Before-last character cannot be a percentage sign
      input = "abcd%a";
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a FormatException.");
      } catch (FormatException exception) { /* as expected */ }

      // The decoded value must be less than 128
      input = "%80";
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a NonASCIIException.");
      } catch (NonASCIIException exception) { /* as expected */ }

      input = "a%80";
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a NonASCIIException.");
      } catch (NonASCIIException exception) { /* as expected */ }

      input = "aa%80";
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a NonASCIIException.");
      } catch (NonASCIIException exception) { /* as expected */ }

      input = "%80a";
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a NonASCIIException.");
      } catch (NonASCIIException exception) { /* as expected */ }

      input = "%80aa";
      try {
         URLEncoding.decode(input);
         fail("URLEncoding.decode(\"" + input + "\") should throw a NonASCIIException.");
      } catch (NonASCIIException exception) { /* as expected */ }

      input = "HelloThere0999";
      String expect = input;
      String result = URLEncoding.decode(input);
      assertEquals(expect, result);

      input  = "+";
      expect = " ";
      result = URLEncoding.decode(input);
      assertEquals(expect, result);

      input  = "a+";
      expect = "a ";
      result = URLEncoding.decode(input);
      assertEquals(expect, result);

      input  = "aa+";
      expect = "aa ";
      result = URLEncoding.decode(input);
      assertEquals(expect, result);

      input  = "+a";
      expect = " a";
      result = URLEncoding.decode(input);
      assertEquals(expect, result);

      input  = "+aa";
      expect = " aa";
      result = URLEncoding.decode(input);
      assertEquals(expect, result);

      input  = "+aa+";
      expect = " aa ";
      result = URLEncoding.decode(input);
      assertEquals(expect, result);

      input  = "Hello+there";
      expect = "Hello there";
      result = URLEncoding.decode(input);
      assertEquals(expect, result);

      input  = "Hello%20there";
      result = URLEncoding.decode(input);
      assertEquals(expect, result);

      // Make sure java.net.URLDecoder produces an equivalent result
      input = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
              "01234567890`-=[]\\;',./ ~!@#$%^&*()_+{}|:\"<>?";
      result = URLEncoding.encode(input);
      assertEquals(URLDecoder.decode(result), input);
      assertEquals(URLDecoder.decode(result), URLEncoding.decode(result));
   }
}
