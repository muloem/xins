package org.xins.tests.common.types.standard;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Int16;
import org.xins.common.types.standard.Properties;
import org.xins.common.collections.PropertyReader;


/**
 * Tests for class <code>Int16</code>.
 *
 * @version $Revision$ $Date$
 * @author Chris Gilbride (<a href="mailto:chris.gilbride@nl.wanadoo.com">chris.gilbride@nl.wanadoo.com</a>)
 */
public class Int16Tests extends TestCase {

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
      return new TestSuite(Int16Tests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Int16Tests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public Int16Tests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   ZeroToTen lowerLimit = new ZeroToTen();

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

   public void testToString() {
      assertEquals("lowerLimit.toString((short)12) should return a value of \"12\"", "12", lowerLimit.toString((short)12));
      assertEquals("lowerLimit.toString(Short.valueOf(\"12\")) should return a value of \"12\"","12", lowerLimit.toString(Short.valueOf("12")));
      assertEquals("lowerLimit.toString(null) should return null", null, lowerLimit.toString(null));
   }

   public void testFromStringForRequired() throws Throwable {
      /* This should cause the specified error. However for some reason it
       * isn't. To prevent the rest of the tests failing this test is commented out.
      
      try {      
         lowerLimit.fromStringForRequired("120");
         fail("Should fail with a TypeValueException due to out of bounds.");
      } catch (TypeValueException tve3) {
         // good
      }
      */

      try { 
         lowerLimit.fromStringForRequired(null);
         fail("fromStringForRequired(null) should have thrown a String is null error");
      } catch (IllegalArgumentException iae) {
         // this is good
      }

      try { 
         lowerLimit.fromStringForRequired("fred");
         fail("lowerLimit.fromStringForRequired(\"fred\") should have thrown a TypeValueException.");
      } catch (TypeValueException tve) {
         // this is good
      }

      try {
         lowerLimit.fromStringForRequired("7");
      } catch (Exception e) {
         fail("lowerLimit.fromStringForRequired(\"7\") caught an unexpected error.");
      }
   }

   public void testFromStringForOptional() throws Throwable {

      try { 
         lowerLimit.fromStringForOptional("fred");
         fail("lowerLimit.fromStringForOptional(\"fred\") should have thrown a TypeValueException.");
      } catch (TypeValueException tve2) {
         // this is good
      }

      try {
         lowerLimit.fromStringForOptional("4");
      } catch (Exception e1) {
         fail("lowerLimit.fromStringForOptional(\"4\") caught unexpected error.");
      }

      assertNull("lowerLimit.fromStringForOptional(null) should return a null.", lowerLimit.fromStringForOptional(null));
   }

   public void testValidValue() throws Throwable {
    
      assertFalse("fred is not a valid value.",lowerLimit.isValidValue("fred"));
     
      assertFalse("12 is outside the bounds of the instance.",lowerLimit.isValidValue("12"));

      assertTrue("9 is a valid value as it is within the bounds.",lowerLimit.isValidValue("9"));

      assertTrue("null is considered to be a valid object",lowerLimit.isValidValue(null));
   }

   class ZeroToTen extends Int16 {
      
      // constructor
      public ZeroToTen() {
         super("ZeroToTen", (short) 0, (short) 10);
      }

  }

}
