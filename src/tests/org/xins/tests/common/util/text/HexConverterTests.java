/*
 * $Id$
 */
package org.xins.tests.common.util.text;

import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.common.text.HexConverter;

/**
 * Tests for class <code>HexConverter</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class HexConverterTests extends TestCase {

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
      return new TestSuite(HexConverterTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HexConverterTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public HexConverterTests(String name) {
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

   public void testToHexString() throws Throwable {
   }

   public void testParseHexString_String() throws Throwable {

      // Pass arguments that should trigger failure
      doTestParseHexString_String(null,                           0L);
      doTestParseHexString_String("",                             0L);
      doTestParseHexString_String("1",                            0L);
      doTestParseHexString_String("0",                            0L);
      doTestParseHexString_String("123456789012345",              0L);
      doTestParseHexString_String("12345678901234567",            0L);
      doTestParseHexString_String("123456789012345g",             0L);
      doTestParseHexString_String("0000000000000000",             0L);
      doTestParseHexString_String("0000000000000001",             1L);
      doTestParseHexString_String("0000000000000002",             2L);
      doTestParseHexString_String("0000000000000003",             3L);
      doTestParseHexString_String("000000000000000f",             0xfL);
      doTestParseHexString_String("00000000000000ff",             0xffL);
      doTestParseHexString_String("1234567890123456",             0x1234567890123456L);
      doTestParseHexString_String("123456789012345a",             0x123456789012345aL);
      doTestParseHexString_String(String.valueOf(Long.MAX_VALUE), Long.MAX_VALUE);
      doTestParseHexString_String("ffffffffffffffff",             -1L);
      doTestParseHexString_String("fffffffffffffffe",             -2L);
      doTestParseHexString_String(String.valueOf(Long.MIN_VALUE), Long.MIN_VALUE);
   }

   private void doTestParseHexString_String(String arg, long expected) {

      boolean illegalArg = (arg == null || arg.length() != 16);
      boolean invalidNumberFormat = false;
      for (int i = 0; !illegalArg && !invalidNumberFormat && i < 16; i++) {
         char c = arg.charAt(i);
         if (c >= '0' && c <= '9') {
            // okay
         } else if (c >= 'a' && c <= 'f') {
            // okay
         } else {
            invalidNumberFormat = true;
         }
      }

      String s = (arg == null) ? "null" : "\"" + arg + '"';

      if (illegalArg) {
         try {
            HexConverter.parseHexLong(arg);
            fail("HexConverter.parseHexLong(" + s + ") should throw an IllegalArgumentException.");
         } catch (IllegalArgumentException exception) {
            // as expected
         }
      } else if (invalidNumberFormat) {
         try {
            HexConverter.parseHexLong(arg);
            fail("HexConverter.parseHexLong(" + s + ") should throw a NumberFormatException.");
         } catch (NumberFormatException exception) {
            // as expected
         }
      } else {
         assertEquals(expected, HexConverter.parseHexLong(arg));
      }
   }
}
