/*
 * $Id$
 */
package org.xins.tests.common.specs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.specs.APISpec;
import org.xins.specs.InvalidNameException;

/**
 * Tests for class <code>APISpec</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class APISpecTests extends TestCase {

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
      return new TestSuite(APISpecTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>APISpecTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public APISpecTests(String name) {
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

   public void testAPISpec_java_lang_String__java_lang_String() throws Throwable {

      APISpec spec;

      try {
         spec = new APISpec(null, null);
         fail("APISpec(null,null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException iae) { /* as expected */ }

      try {
         spec = new APISpec(null, "1.1");
         fail("APISpec(null,non-null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException iae) { /* as expected */ }

      try {
         spec = new APISpec("name", null);
         fail("APISpec(non-null,null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException iae) { /* as expected */ }

      try {
         spec = new APISpec("", "1.1");
         fail("APISpec(empty-string,non-null) should throw an InvalidNameException.");
      } catch (InvalidNameException ine) { /* as expected */ }

      try {
         spec = new APISpec("1", "1.1");
         fail("APISpec(digit,non-null) should throw an InvalidNameException.");
      } catch (InvalidNameException ine) { /* as expected */ }

      spec = new APISpec("sso", "1.1");
   }
}
