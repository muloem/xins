/*
 * $Id$
 */
package org.xins.tests.common.specs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.specs.APISpec;
import org.xins.specs.FunctionSpec;
import org.xins.specs.InvalidNameException;
import org.xins.specs.InvalidVersionException;

/**
 * Tests for class <code>FunctionSpec</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class FunctionSpecTests extends TestCase {

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
      return new TestSuite(FunctionSpecTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>FunctionSpecTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public FunctionSpecTests(String name) {
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

   public void testFunctionSpec_java_lang_String__java_lang_String() throws Throwable {

      FunctionSpec spec;
      APISpec api = new APISpec("api", "1.0");

      // Test null arguments
      int argCount = 3;
      int combinations = 7; // (2 ** 3) - 1
      for (int i = 0; i < combinations; i++) {
         boolean arg1 = (i & 1) > 0;
         boolean arg2 = (i & 2) > 0;
         boolean arg3 = (i & 4) > 0;
         try {
            spec = new FunctionSpec(
               (arg1 ? api        : null),
               (arg2 ? "Function" : null),
               (arg3 ? "1.1"      : null)
            );
            fail("FunctionSpec() should throw an IllegalArgumentException. Configuration: "
                 + (arg1 ? "non-null" : "null") + ", "
                 + (arg2 ? "non-null" : "null") + ", "
                 + (arg3 ? "non-null" : "null") + '.');
         } catch (IllegalArgumentException iae) { /* as expected */ }
      }

      // Test invalid names
      String[] invalidNames = new String[] {
         "",         "fun",  "fuN"  ,   "fUN",    "Fun_", "_Fun",
         "Fun.",     ".Fun", "Fun.Fun", "Fun.ct", "1",    "Fu_nc",
         "Fun--cti", "-Fun", "Fun-",    "Fu_n",   "FuncT"
      };
      for (int i = 0; i < invalidNames.length; i++) {
         try {
            String name = invalidNames[i];
            String version = "1.1";
            spec = new FunctionSpec(api, name, version);
            fail("FunctionSpec(\"" + name + "\", \"" + version + "\") should throw an InvalidNameException.");
         } catch (InvalidNameException ine) { /* as expected */ }
      }

      // Test invalid versions
      String[] invalidVersions = new String[] {
         "a", "1.", "1.a", "a.1", ".", "", "1.0.0.a", "1.0.0."
      };
      for (int i = 0; i < invalidVersions.length; i++) {
         try {
            String name    = "Function";
            String version = invalidVersions[i];

            spec = new FunctionSpec(api, name, version);
            fail("FunctionSpec(\"" + name + "\", \"" + version + "\") should throw an InvalidVersionException.");
         } catch (InvalidVersionException ive) { /* as expected */ }
      }

      // Name must be checked before version
      try {
         spec = new FunctionSpec(api, "1", "a");
      } catch (InvalidVersionException ive) {
         fail("Name should be checked before version, but version is checked first.");
      } catch (InvalidNameException ine) { /* as expected */ }

      // Test valid constructions
      String[] names = new String[] {
         "Fun", "FunCti", "FunCtiOn", "FuNcTi", "Fun12", "F0"};
      String[] versions = new String[] {
         "1", "1.1", "1.12", "1.12.1", "12.1.2", "1.2.3.4.5.6.7.8.9.10", "100.2.10" };
      int max = Math.max(names.length, versions.length);
      for (int i = 0; i < max; i++) {
         String name    = names   [(i < names.length    ? i : names.length    - 1)];
         String version = versions[(i < versions.length ? i : versions.length - 1)];
         spec = new FunctionSpec(api, name, version);
      }
   }

   public void testGetTypeName() throws Throwable {
      assertEquals("function", FunctionSpec.TYPE.getTypeName());
   }

   public void testGetParentType() throws Throwable {
      assertEquals(APISpec.TYPE, FunctionSpec.TYPE.getParentType());
   }
}
