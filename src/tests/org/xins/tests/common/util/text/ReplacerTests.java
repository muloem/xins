/*
 * $Id$
 */
package org.xins.tests.common.util.text;

import java.util.Properties;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.common.text.Replacer;

/**
 * Tests for class <code>Replacer</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class ReplacerTests extends TestCase {

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
      return new TestSuite(ReplacerTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ReplacerTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ReplacerTests(String name) {
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

   public void testReplace() throws Throwable {

      String text;
      char tagStart = '{';
      char tagEnd   = '}';

      try {
         Replacer.replace((String) null, tagStart, tagEnd, new Properties());
         fail("Replacer.replace(null, '" + tagStart + "', '" + tagEnd + "', new java.util.Properties()) should throw a java.lang.IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         text = "Some text.";
         Replacer.replace(text, tagStart, tagEnd, null);
         fail("Replacer.replace(\"" + text + "\", '" + tagStart + "', '" + tagEnd + "', null) should throw a java.lang.IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         Replacer.replace((String) null, tagStart, tagEnd, null);
         fail("Replacer.replace(null, '" + tagStart + "', '" + tagEnd + "', null) should throw a java.lang.IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // If no tag start nor tag end, then expect input == output
             text     = "Hello";
      String expected = text;
      String result   = Replacer.replace(text, tagStart, tagEnd, new Properties());
      assertEquals(expected, result);

      // If only tag end, still expect input == output
      text     = "Hello" + tagEnd + " there";
      expected = text;
      result   = Replacer.replace(text, tagStart, tagEnd, new Properties());
      assertEquals(expected, result);

      // If no replacement found, expect Replacer.Exception
      text = "Hello " + tagStart + "name" + tagEnd;
      try {
         Replacer.replace(text, tagStart, tagEnd, new Properties());
      } catch (Replacer.Exception exception) {
         // as expected
      }

      // If no end tag, expect Replacer.Exception
      text = "Hello " + tagStart + "name";
      try {
         Replacer.replace(text, tagStart, tagEnd, new Properties());
      } catch (Replacer.Exception exception) {
         // as expected
      }

      // Expect success
      String propertyName  = "name";
      String propertyValue = "Ernst";
      text     = "Hello " + tagStart + propertyName + tagEnd + '.';
      expected = "Hello " + propertyValue                    + '.';
      Properties properties = new Properties();
      properties.setProperty(propertyName, propertyValue);
      result = Replacer.replace(text, tagStart, tagEnd, properties);
      assertEquals(expected, result);
   }
}
