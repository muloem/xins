package org.xins.tests.common.types.standard;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Int8;
import org.xins.common.types.standard.Properties;
import org.xins.common.collections.PropertyReader;


/**
 * Tests for class <code>Int16</code>.
 *
 * @version $Revision$ $Date$
 * @author Chris Gilbride (<a href="mailto:chris.gilbride@nl.wanadoo.com">chris.gilbride@nl.wanadoo.com</a>)
 */
public class Int8Tests extends TestCase {

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
    * Constructs a new <code>Int8Tests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public Int8Tests(String name) {
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
      lowerLimit.toString((byte)12);
      lowerLimit.toString(Byte.valueOf("12"));
      lowerLimit.toString(null);
   }

   public void testFromStringForRequired() throws Throwable {

      try { 
         lowerLimit.fromStringForRequired(null);
         fail("Should have thrown a String is null error");
      } catch (IllegalArgumentException iae) {
         // this is good
      }

      try { 
         lowerLimit.fromStringForRequired("fred");
         fail("Should have thrown a TypeValueException from a NumberFormatException.");
      } catch (TypeValueException tve) {
         // this is good
      }

      try {
         lowerLimit.fromStringForRequired("7");
      } catch (Exception e) {
         fail("Caught an unexpected error.");
      }
   }

   public void testFromStringForOptional() throws Throwable {

      try { 
         lowerLimit.fromStringForOptional("fred");
         fail("Should have thrown a TypeValueException from a NumberFormatException.");
      } catch (TypeValueException tve2) {
         // this is good
      }

      try {
         lowerLimit.fromStringForOptional("4");
      } catch (Exception e1) {
         fail("Caught unexpected error.");
      }

      assertNull("Null should be returned when a null is passed.",lowerLimit.fromStringForOptional(null));
   }

   public void testValidValue() throws Throwable {
    
      assertFalse("fred is not a valid value.",lowerLimit.isValidValue("fred"));
     
      assertFalse("12 is outside the bounds of the instance.",lowerLimit.isValidValue("12"));

      assertTrue("9 is a valid value as it is within the bounds.",lowerLimit.isValidValue("9"));

      assertTrue("null is considered to be a valid object",lowerLimit.isValidValue(null));
   }

   class ZeroToTen extends Int8 {
      
      // constructor
      public ZeroToTen() {
         super("ZeroToTen", (byte) 0, (byte) 10);
      }

  }

}
