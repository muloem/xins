/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.perftests;

import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.text.DateConverter;
import org.xins.common.text.FastStringBuffer;

/**
 * Performance tests for class <code>DateConverter</code>.
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

   private static final int ROUNDS = 1000000;


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

   public void testDateConverter1() throws Exception {

      long millis = System.currentTimeMillis();

      for (int i = 0; i < ROUNDS; i++) {
         millis += i & 0xff;
         DateConverter.toDateString(millis, true);
         DateConverter.toDateString(millis, true);
         millis++;
         DateConverter.toDateString(millis, true);
      }
   }

   public void testDateConverter2() throws Exception {

      long millis = System.currentTimeMillis();
      DateConverter dc = new DateConverter(true);

      char[] buffer = new char[30];
      for (int i = 0; i < ROUNDS; i++) {
         millis += i & 0xff;
         dc.format(millis, buffer, 0);
         dc.format(millis, buffer, 0);
         millis++;
         dc.format(millis, buffer, 0);
      }
   }
}
