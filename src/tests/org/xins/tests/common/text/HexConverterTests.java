/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.text;

import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.common.text.HexConverter;
import org.xins.common.text.FastStringBuffer;

/**
 * Tests for class <code>HexConverter</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
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

      // Test other methods
      assertTrue(HexConverter.isHexDigit('6'));
      assertTrue(HexConverter.isHexDigit('b'));
      assertTrue(HexConverter.isHexDigit('F'));

      assertEquals("ANT", new String(HexConverter.parseHexBytes("414e54", 0, 6)));
      assertEquals(2, HexConverter.parseHexBytes("414e54", 2, 4).length);
      assertEquals((byte)78, HexConverter.parseHexBytes("414e54", 2, 2)[0]);

      assertEquals(0x123b56F, HexConverter.parseHexInt("Testing 0123b56F", 8));
      assertEquals(0x123b56F, HexConverter.parseHexInt("0123b56F"));
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

   public void testToHexString() throws Throwable {
      doTestToHexString("", 1L, "0000000000000001");
      doTestToHexString("", 0x1234567890123456L, "1234567890123456");
      doTestToHexString("Testing ", 1L, "Testing 0000000000000001");
      doTestToHexString("Testing ", 0x1234567890123456L, "Testing 1234567890123456");

      byte[] input1 = { (byte)56, (byte)10, (byte) 230};
      String output1 = HexConverter.toHexString(input1);
      assertEquals("380ae6", output1);
      assertEquals("e2", HexConverter.toHexString((byte)226));
      assertEquals("1234", HexConverter.toHexString((short)0x1234));
      assertEquals("00e9", HexConverter.toHexString('\u00e9'));
      assertEquals("0020", HexConverter.toHexString(' '));
      assertEquals("000000e2", HexConverter.toHexString(226));
      assertEquals("1234567890123456", HexConverter.toHexString(0x1234567890123456L));

      FastStringBuffer buffer = new FastStringBuffer("Testing ");
      HexConverter.toHexString(buffer, 0x123456);
      assertEquals("Testing 00123456", buffer.toString());
   }

   private void doTestToHexString(String arg, long value, String expectedResult) {
      FastStringBuffer buffer = new FastStringBuffer(arg);
      HexConverter.toHexString(buffer, value);
      assertEquals(expectedResult, buffer.toString());
   }

}
