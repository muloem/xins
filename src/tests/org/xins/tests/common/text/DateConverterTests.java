/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.text;

import java.util.TimeZone;

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

   public void testParseDateString_String() throws Throwable {
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
}
