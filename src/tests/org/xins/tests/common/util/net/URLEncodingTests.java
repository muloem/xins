/*
 * $Id$
 */
package org.xins.tests.common.util.net;

import java.net.URLEncoder;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.util.net.URLEncoding;
import org.xins.util.text.NonASCIIException;

/**
 * Tests for class <code>URLEncoding</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
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
         fail("URLEncoding.encode(\\u0080) should throw an IllegalArgumentException.");
      } catch (NonASCIIException exception) { /* as expected */ }

      String s = "HelloThere0999";
      String result = URLEncoding.encode(s);
      assertEquals(s, result);

      s = "Hello there";
      result = URLEncoding.encode(s);
      assertEquals("Hello+there", result);

      // Make sure java.net.URLEncoder produces an equivalent result
      s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890" +
          "`-=[]\\;',./ ~!@#$%^&*()_+{}|:\"<>?";
      result = URLEncoding.encode(s);
      assertEquals(URLEncoder.encode(s), result);
   }
}
