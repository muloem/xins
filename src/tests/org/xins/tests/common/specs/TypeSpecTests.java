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

      TypeSpec spec;

      // Test null arguments
      try {
         spec = new TypeSpec(null, null);
         fail("TypeSpec(null,null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException iae) { /* as expected */ }
      try {
         spec = new TypeSpec(null, "1.1");
         fail("TypeSpec(null,non-null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException iae) { /* as expected */ }
      try {
         spec = new TypeSpec("name", null);
         fail("TypeSpec(non-null,null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException iae) { /* as expected */ }

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
            spec = new TypeSpec(name, version);
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

            spec = new TypeSpec(name, version);
            fail("TypeSpec(\"" + name + "\", \"" + version + "\") should throw an InvalidVersionException.");
         } catch (InvalidVersionException ive) { /* as expected */ }
      }

      // Name must be checked before version
      try {
         spec = new TypeSpec("1", "a");
      } catch (InvalidVersionException ive) {
         fail("Name should be checked before version, but version is checked first.");
      } catch (InvalidNameException ine) { /* as expected */ }

      // Test valid constructions
      spec = new TypeSpec("type",      "1");
      spec = new TypeSpec("someType",  "1.1");
      spec = new TypeSpec("someType2", "1.12");
      spec = new TypeSpec("some2",     "1.12.1");
      spec = new TypeSpec("someType",  "12.1.2");
      spec = new TypeSpec("s",         "1.2.3.4.5.6.7.8.9.10");
      spec = new TypeSpec("s2",        "1.2.3.4.5.6.7.8.9.10");
      spec = new TypeSpec("typeAB",    "10.2.3.4.5.6.7.8.9.0");
   }

   public void testGetTypeName() throws Throwable {
      assertEquals("type", TypeSpec.TYPE.getTypeName());
   }

   public void testGetParentType() throws Throwable {
      assertEquals(APISpec.TYPE, TypeSpec.TYPE.getParentType());
   }
}
