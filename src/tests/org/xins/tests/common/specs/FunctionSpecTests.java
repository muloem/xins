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

      // Test null arguments
      try {
         spec = new FunctionSpec(null, null);
         fail("FunctionSpec(null,null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException iae) { /* as expected */ }
      try {
         spec = new FunctionSpec(null, "1.1");
         fail("FunctionSpec(null,non-null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException iae) { /* as expected */ }
      try {
         spec = new FunctionSpec("name", null);
         fail("FunctionSpec(non-null,null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException iae) { /* as expected */ }

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
            spec = new FunctionSpec(name, version);
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

            spec = new FunctionSpec(name, version);
            fail("FunctionSpec(\"" + name + "\", \"" + version + "\") should throw an InvalidVersionException.");
         } catch (InvalidVersionException ive) { /* as expected */ }
      }

      // Name must be checked before version
      try {
         spec = new FunctionSpec("1", "a");
      } catch (InvalidVersionException ive) {
         fail("Name should be checked before version, but version is checked first.");
      } catch (InvalidNameException ine) { /* as expected */ }

      // Test valid constructions
      spec = new FunctionSpec("Fun",      "1");
      spec = new FunctionSpec("FunCti",   "1.1");
      spec = new FunctionSpec("FunCtiOn", "1.12");
      spec = new FunctionSpec("FuNcTi",   "1.12.1");
      spec = new FunctionSpec("Fun",      "12.1.2");
      spec = new FunctionSpec("Fun12",    "1.2.3.4.5.6.7.8.9.10");
      spec = new FunctionSpec("F0",       "1.2.3.4.5.6.7.8.9.10");
   }

   public void testGetParentType() throws Throwable {
      assertEquals(APISpec.TYPE, FunctionSpec.TYPE.getParentType());
   }
}
