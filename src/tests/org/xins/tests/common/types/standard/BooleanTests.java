package org.xins.tests.common.types.standard;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Boolean;
import org.xins.common.types.standard.Properties;
import org.xins.common.collections.PropertyReader;


/**
 * Tests for class <code>Boolean</code>.
 *
 * @version $Revision$ $Date$
 * @author Chris Gilbride (<a
 * href="mailto:chris.gilbride@nl.wanadoo.com">chris.gilbride@nl.wanadoo.com</a>)
 */
public class BooleanTests extends TestCase {

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
      return new TestSuite(PropertiesTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>BooleanTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public BooleanTests(String name) {
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

   public void testFromStringForRequired() throws Throwable {

      // test the fromStringForRequired method with all possabilities
      try { 
         Boolean.fromStringForRequired(null);
         fail("Should have thrown a String is null error");
      } catch (IllegalArgumentException iae) {
         // this is good
      }

      try { 
         Boolean.fromStringForRequired("fred");
         fail("Should have thrown a TypeValueException.");
      } catch (TypeValueException tve) {
         // this is good
      }

      assertFalse("fromStringForRequired(false) should return false.", Boolean.fromStringForRequired("false"));

      assertTrue("fromStringForRequired(true) should return true.", Boolean.fromStringForRequired("true"));
   }

   public void testFromStringForOptional() throws Throwable {
      // test the fromStringForOptional method with all possabilities
      try { 
         Boolean.fromStringForOptional("fred");
         fail("Should have thrown a TypeValueException.");
      } catch (TypeValueException tve2) {
         // this is good
      }

      assertNull("Should return a null from a null parameter.",Boolean.fromStringForOptional(null));

      assertTrue("fromStringForOptional(true) should return true.", Boolean.fromStringForOptional("true").booleanValue());

      assertFalse("fromStringForOptional(false) should return a false.", Boolean.fromStringForOptional("false").booleanValue());
   }

   public void testIsValidValue() {
      // test the isValidValue method inherited from the TYPE class

      assertTrue("Boolean.SINGLETON.isValidValue('true') is valid.", Boolean.SINGLETON.isValidValue("true"));

      assertTrue("Boolean.SINGLETON.isValidValue('false') is valid.", Boolean.SINGLETON.isValidValue("false"));

      assertTrue("Boolean.SINGLETON.isValidValue(null) is valid.", Boolean.SINGLETON.isValidValue(null));
   }

   public void testFromString() throws Throwable  {
      // test the fromString class inherited from the TYPE class

      assertNull("Boolean.SINGLETON.fromString(null) should return a null.", Boolean.SINGLETON.fromString(null));

      try {
         Boolean.SINGLETON.fromString("fred");
         fail("Should throw a type value exception.");
      } catch (TypeValueException tve) {
         // this is good
      }

      try {
         Boolean.SINGLETON.getValueClass().isInstance(Boolean.SINGLETON.fromString("fred"));
         fail("Should have thrown an error.");
      } catch (Exception e) {
         // this is good      
      }
   }

   public void testToString() throws Throwable {
      // test the toString methods
      boolean t = true;
      boolean f = false;

      assertNull("toString(Boolean value) should return a null for a null.", Boolean.SINGLETON.toString(null));

      if (! "false".equals(Boolean.SINGLETON.toString(f))) {
         fail("Should return string value of false from toString(f).");
      }

      if (! "true".equals(Boolean.SINGLETON.toString(t))) {
         fail("Should return string value of true from toString(t).");
      }

      if (! "true".equals(Boolean.SINGLETON.toString(java.lang.Boolean.TRUE))) {
         fail("Should have returned true from Boolean.SINGLETON.toString(java.lang.Boolean.TRUE).");
      }

      if (! "false".equals(Boolean.SINGLETON.toString(java.lang.Boolean.FALSE))) {
         fail("Should have returned false from Boolean.SINGLETON.toString(java.lang.Boolean.FALSE).");
      }

   }

}
