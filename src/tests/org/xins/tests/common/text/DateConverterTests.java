/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.text;

import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.text.DateConverter;
import org.xins.common.text.FastStringBuffer;

/**
 * Tests for class <code>DateConverter</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class DateConverterTests extends TestCase {

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
      return new TestSuite(DateConverterTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>DateConverterTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public DateConverterTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testToDateString1() throws Exception {
      try {
         DateConverter.toDateString(null, 0);
         fail("Expected DateConverter.toDateString(null, <irrelevant>) to throw an IllegalArgumentException.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         DateConverter.toDateString(TimeZone.getDefault(), Long.MIN_VALUE);
         fail("Expected DateConverter.toDateString(<irrelevant>, Long.MIN_VALUE) to throw an IllegalArgumentException.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         DateConverter.toDateString(TimeZone.getDefault(), Long.MAX_VALUE);
         fail("Expected DateConverter.toDateString(<irrelevant>, Long.MAX_VALUE) to throw an IllegalArgumentException.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      TimeZone tz = TimeZone.getTimeZone("GMT");
      String expected = "1970.01.01 00:00:00.000";
      String actual = DateConverter.toDateString(tz, 0L);
      assertEquals("Expected data converter to return " + expected + " instead of " + actual + '.', expected, actual);

      expected = "1970.01.01 00:00:00.001";
      actual = DateConverter.toDateString(tz, 1L);
      assertEquals("Expected data converter to return " + expected + " instead of " + actual + '.', expected, actual);
   }

   public void testToDateString2() throws Exception {

      try {
         DateConverter.toDateString(0L, true, null);
         fail("Expected DateConverter.toDateString(<long>,<boolean>,null) to throw an IllegalArgumentException.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      String separator = " ";
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd" + separator + "HHmmssSSS");

      long   millis    = 0L;
      String expected  = formatter.format(new Date(millis));
      String actual    = DateConverter.toDateString(millis, true, separator);
      String message   = "Expected DateConverter.toDateString(long,boolean,String) to return \"" + expected + "\" instead of \"" + actual + "\".";
      assertEquals(message, expected, actual);

      millis = System.currentTimeMillis();
      expected  = formatter.format(new Date(millis));
      actual    = DateConverter.toDateString(millis, true, separator);
      message   = "Expected DateConverter.toDateString(long,boolean,String) to return \"" + expected + "\" instead of \"" + actual + "\".";
      assertEquals(message, expected, actual);

      for (int i = 0; i < 50; i++) {
         millis   += 123456L;
         expected  = formatter.format(new Date(millis));
         actual    = DateConverter.toDateString(millis, true, separator);
         message   = "Expected DateConverter.toDateString(long,boolean,String) to return \"" + expected + "\" instead of \"" + actual + "\".";
         assertEquals(message, expected, actual);
      }
   }
}
