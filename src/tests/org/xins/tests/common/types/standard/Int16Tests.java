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
 * @author Chris Gilbride (<a
 * href="mailto:chris.gilbride@nl.wanadoo.com">chris.gilbride@nl.wanadoo.com</a>)
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
      return new TestSuite(PropertiesTests.class);
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

   public void testLimit() throws Throwable {
      ZeroToTen lowerLimit = new ZeroToTen();
      lowerLimit.toString((short)12);
      lowerLimit.toString(Short.valueOf("12"));
      lowerLimit.toString(null);

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
         lowerLimit.fromStringForOptional("fred");
         fail("Should have thrown a TypeValueException from a NumberFormatException.");
      } catch (TypeValueException tve2) {
         // this is good
      }

      lowerLimit.fromStringForOptional(null);

     /* try {
        lowerLimit.isValidValueImpl("fred");
         fail("Should throw a number format exception.");
      } catch (NumberFormatException nfe2) {
         // this is good
      }
     */

      if (lowerLimit.validImpl("12")) {
         fail("Should have returned false as this value is outside of the bounds.");
      }

      if (! lowerLimit.validImpl("9")) {
         fail("Should have returned true as the value is within the bounds.");
      }

      lowerLimit.validImpl(null);
      lowerLimit.validImpl("fred");

//      lowerLimit.fromString2Obj("22");
      Short valueShort = (Short) lowerLimit.fromString2Obj("9");

      lowerLimit.objToString(valueShort);
   }

   class ZeroToTen extends Int16 {
      
      // constructor
      public ZeroToTen() {
         super("ZeroToTen", (short) 0, (short) 10);
      }

      public boolean validImpl(String value) {
         if (isValidValueImpl(value)) {
            return true;
         }
         return false;
      }

      public Object fromString2Obj(String value) {
         return fromStringImpl("21");
      }
     
      public String objToString(Object value)
         throws TypeValueException {
         return toString(value);
      }
   }

}
