/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.text;

import java.util.Date;
import java.util.Random;
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

      String separator = "-";
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd" + separator + "HHmmssSSS");

      long   millis    = 0L;
      String expected  = formatter.format(new Date(millis));
      String actual    = DateConverter.toDateString(millis, true);
      String message   = "Expected DateConverter.toDateString(long,boolean,String) to return \"" + expected + "\" instead of \"" + actual + "\".";
      assertEquals(message, expected, actual);

      millis++;
      expected  = formatter.format(new Date(millis));
      actual    = DateConverter.toDateString(millis, true);
      message   = "Expected DateConverter.toDateString(long,boolean,String) to return \"" + expected + "\" instead of \"" + actual + "\".";
      assertEquals(message, expected, actual);

      millis++;
      expected  = formatter.format(new Date(millis));
      actual    = DateConverter.toDateString(millis, true);
      message   = "Expected DateConverter.toDateString(long,boolean,String) to return \"" + expected + "\" instead of \"" + actual + "\".";
      assertEquals(message, expected, actual);

      millis = System.currentTimeMillis();
      expected  = formatter.format(new Date(millis));
      actual    = DateConverter.toDateString(millis, true);
      message   = "Expected DateConverter.toDateString(long,boolean,String) to return \"" + expected + "\" instead of \"" + actual + "\".";
      assertEquals(message, expected, actual);

      Random random = new Random();
      DateConverter dc = new DateConverter(true);
      String s1 = "The date is: ";
      String s2 = "YYYYMMDDxHHMMSSNNN";
      String s3 = s1 + s2;
      char[] buffer = s3.toCharArray();
      for (int i = 0; i < 50; i++) {
         if ((i % 2) == 0) {
            millis += random.nextInt();
            millis += random.nextInt();
         } else {
            millis += random.nextInt(5);
         }

         expected  = formatter.format(new Date(millis));
         actual    = DateConverter.toDateString(millis, true);
         message   = "Expected DateConverter.toDateString(long,boolean,String) to return \"" + expected + "\" instead of \"" + actual + "\".";
         assertEquals(message, expected, actual);

         expected  = s1 + formatter.format(new Date(millis));
         dc.format(millis, buffer, 13);
         actual    = new String(buffer);
         message   = "Expected DateConverter.format(long,char[],int) to return \"" + expected + "\" instead of \"" + actual + "\".";
         assertEquals(message, expected, actual);
      }
   }
}
