/*
 * $Id$
 */
package org.xins.tests.common.specs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.specs.APISpec;
import org.xins.specs.TypeSpec;
import org.xins.specs.InvalidNameException;
import org.xins.specs.InvalidVersionException;

/**
 * Tests for class <code>TypeSpec</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class TypeSpecTests extends TestCase {

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
      return new TestSuite(TypeSpecTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>TypeSpecTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public TypeSpecTests(String name) {
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

   public void testTypeSpec_java_lang_String__java_lang_String() throws Throwable {

      APISpec api = new APISpec("api", "1.0");
      TypeSpec spec;

      // Test null arguments
      int argCount = 3;
      int combinations = 7; // (2 ** 3) - 1
      for (int i = 0; i < combinations; i++) {
         boolean arg1 = (i & 1) > 0;
         boolean arg2 = (i & 2) > 0;
         boolean arg3 = (i & 4) > 0;
         try {
            spec = new TypeSpec(
               (arg1 ? api    : null),
               (arg2 ? "type" : null),
               (arg3 ? "1.1"  : null)
            );
            fail("TypeSpec() should throw an IllegalArgumentException. Configuration: "
                 + (arg1 ? "non-null" : "null") + ", "
                 + (arg2 ? "non-null" : "null") + ", "
                 + (arg3 ? "non-null" : "null") + '.');
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
            String version = "1.1";
            spec = new TypeSpec(api, name, version);
            fail("TypeSpec(\"" + name + "\", \"" + version + "\") should throw an InvalidNameException.");
         } catch (InvalidNameException ine) { /* as expected */ }
      }

      // Test invalid versions
      String[] invalidVersions = new String[] {
         "a", "1.", "1.a", "a.1", ".", "", "1.0.0.a", "1.0.0."
      };
      for (int i = 0; i < invalidVersions.length; i++) {
         try {
            String name    = "type";
            String version = invalidVersions[i];

            spec = new TypeSpec(api, name, version);
            fail("TypeSpec(\"" + name + "\", \"" + version + "\") should throw an InvalidVersionException.");
         } catch (InvalidVersionException ive) { /* as expected */ }
      }

      // Name must be checked before version
      try {
         spec = new TypeSpec(api, "1", "a");
      } catch (InvalidVersionException ive) {
         fail("Name should be checked before version, but version is checked first.");
      } catch (InvalidNameException ine) { /* as expected */ }

      // Name must be checked before version
      try {
         spec = new TypeSpec(api, "1", "a");
      } catch (InvalidVersionException ive) {
         fail("Name should be checked before version, but version is checked first.");
      } catch (InvalidNameException ine) { /* as expected */ }

      // Test valid constructions
      String[] names = new String[] {
         "type", "someType", "someType2", "some2", "s", "s2", "typeAB"};
      String[] versions = new String[] {
         "1", "1.1", "1.12", "1.12.1", "12.1.2", "1.2.3.4.5.6.7.8.9.10", "100.2.10" };
      int max = Math.max(names.length, versions.length);
      for (int i = 0; i < max; i++) {
         String name    = names   [(i < names.length    ? i : names.length    - 1)];
         String version = versions[(i < versions.length ? i : versions.length - 1)];
         spec = new TypeSpec(api, name, version);
      }
   }

   public void testGetTypeName() throws Throwable {
      assertEquals("type", TypeSpec.TYPE.getTypeName());
   }

   public void testGetParentType() throws Throwable {
      assertEquals(APISpec.TYPE, TypeSpec.TYPE.getParentType());
   }
}
