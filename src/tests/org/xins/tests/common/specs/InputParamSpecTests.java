/*
 * $Id$
 */
package org.xins.tests.common.specs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.specs.APISpec;
import org.xins.specs.FunctionSpec;
import org.xins.specs.InputParamSpec;
import org.xins.specs.InvalidNameException;
import org.xins.specs.InvalidVersionException;
import org.xins.specs.TypeSpec;

/**
 * Tests for class <code>InputParamSpec</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class InputParamSpecTests extends TestCase {

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
      return new TestSuite(InputParamSpecTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>InputParamSpecTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public InputParamSpecTests(String name) {
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

   public void testInputParamSpec_java_lang_String__java_lang_String() throws Throwable {

      APISpec      api = new APISpec("api", "1.0");
      FunctionSpec fnc = new FunctionSpec(api, "Function", "1.0");
      TypeSpec     typ = new TypeSpec(api, "type", "1.0");
      InputParamSpec spec;

      // Test null arguments
      int argCount = 3;
      int combinations = 7; // (2 ** 3) - 1
      for (int i = 0; i < combinations; i++) {
         boolean arg1 = (i & 1) > 0;
         boolean arg2 = (i & 2) > 0;
         boolean arg3 = (i & 4) > 0;
         try {
            spec = new InputParamSpec(
               (arg1 ? fnc    : null),
               (arg2 ? "par"  : null),
               (arg3 ? typ    : null),
               false
            );
            fail("InputParamSpec() should throw an IllegalArgumentException. Configuration: "
                 + (arg1 ? "non-null" : "null") + ", "
                 + (arg2 ? "non-null" : "null") + ", "
                 + (arg3 ? "non-null" : "null") + ", false.");
         } catch (IllegalArgumentException iae) { /* as expected */ }
      }

      // Test invalid names
      String[] invalidNames = new String[] {
         "",         "TYP",  "TYp"  ,   "Typ",    "typ_", "_typ",
         "typ.",     ".typ", "typ.typ", "typ.ty", "1",    "ty_pe",
         "typ-typ", "-typ", "typ-",     "ty_p",   "typ--typ"
      };
      for (int i = 0; i < invalidNames.length; i++) {
         try {
            String name = invalidNames[i];
            spec = new InputParamSpec(fnc, name, typ, false);
            fail("InputParamSpec(FunctionSpec, \"" + name + "\") should throw an InvalidNameException.");
         } catch (InvalidNameException ine) { /* as expected */ }
      }

      // Test valid constructions
      String[] names = new String[] {
         "type", "someType", "someType2", "some2", "s", "s2", "typeAB"};
      for (int i = 0; i < names.length; i++) {
         String name = names[i];
         spec = new InputParamSpec(fnc, name, typ, false);
         spec = new InputParamSpec(fnc, name, typ, true);
      }
   }

   public void testGetTypeName() throws Throwable {
      assertEquals("function input parameter", InputParamSpec.TYPE.getTypeName());
   }

   public void testGetParentType() throws Throwable {
      assertEquals(FunctionSpec.TYPE, InputParamSpec.TYPE.getParentType());
   }
}
