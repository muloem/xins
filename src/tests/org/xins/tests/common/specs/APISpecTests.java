/*
 * $Id$
 */
package org.xins.tests.common.specs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.specs.APISpec;
import org.xins.specs.InvalidNameException;
import org.xins.specs.InvalidVersionException;

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

      // Test null arguments
      int argCount = 2;
      int combinations = 3; // (2 ** 2) - 1
      for (int i = 0; i < combinations; i++) {
         boolean arg1 = (i & 1) > 0;
         boolean arg2 = (i & 2) > 0;
         try {
            spec = new APISpec(
               (arg1 ? "api" : null),
               (arg2 ? "1.0" : null)
            );
            fail("APISpec() should throw an IllegalArgumentException. Configuration: "
                 + (arg1 ? "non-null" : "null") + ", "
                 + (arg2 ? "non-null" : "null") + '.');
         } catch (IllegalArgumentException iae) { /* as expected */ }
      }

      // Test invalid names
      String[] invalidNames = new String[] {
         "",         "API",  "apI"  ,   "Api",    "api_", "_api",
         "api.",     ".api", "api.api", "api.ii", "1",    "ap_ii",
         "api--api", "-api", "api-",    "ap_i"
      };
      for (int i = 0; i < invalidNames.length; i++) {
         try {
            String name = invalidNames[i];
            String version = "1.1";
            spec = new APISpec(name, version);
            fail("APISpec(\"" + name + "\", \"" + version + "\") should throw an InvalidNameException.");
         } catch (InvalidNameException ine) { /* as expected */ }
      }

      // Test invalid versions
      String[] invalidVersions = new String[] {
         "a", "1.", "1.a", "a.1", ".", "", "1.0.0.a", "1.0.0."
      };
      for (int i = 0; i < invalidVersions.length; i++) {
         try {
            String name    = "api";
            String version = invalidVersions[i];

            spec = new APISpec(name, version);
            fail("APISpec(\"" + name + "\", \"" + version + "\") should throw an InvalidVersionException.");
         } catch (InvalidVersionException ive) { /* as expected */ }
      }

      // Name must be checked before version
      try {
         spec = new APISpec("1", "a");
      } catch (InvalidVersionException ive) {
         fail("Name should be checked before version, but version is checked first.");
      } catch (InvalidNameException ine) { /* as expected */ }

      // Test valid constructions
      spec = new APISpec("api",       "1");
      spec = new APISpec("api-api",   "1.1");
      spec = new APISpec("api-api-a", "1.12");
      spec = new APISpec("a-api",     "1.12.1");
      spec = new APISpec("api-a",     "12.1.2");
      spec = new APISpec("a-api-a",   "1.2.3.4.5.6.7.8.9.10");
   }

   public void testGetTypeName() throws Throwable {
      assertEquals("api", APISpec.TYPE.getTypeName());
   }

   public void testGetParentType() throws Throwable {
      assertNull(APISpec.TYPE.getParentType());
   }
}
