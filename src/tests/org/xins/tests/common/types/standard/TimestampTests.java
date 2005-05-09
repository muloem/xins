package org.xins.tests.common.types.standard;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.types.TypeValueException;
import org.xins.common.types.standard.Timestamp;

/**
 * Tests for the <code>Timestamp</code> type class.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class TimestampTests extends TestCase {

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
      return new TestSuite(TimestampTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>TimestampTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public TimestampTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   private TimeZone _timeZone;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void setUp() throws Exception {
      _timeZone = TimeZone.getDefault();

      TimeZone tz = TimeZone.getTimeZone("UTC");
      TimeZone.setDefault(tz);
   }

   /**
    * Tests the <code>Timestamp$Value</code> constructor that accepts a
    * <code>Calendar</code> instance.
    */
   public void testTimestampValue_Calendar() throws Exception {

      Calendar cal = null;
      Timestamp.Value v;
      try {
         v = new Timestamp.Value(cal);
         fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      cal = Calendar.getInstance();
      cal.set(2005, 4, 6, 15, 52, 21);
      v = new Timestamp.Value(cal);
      assertEquals(2005, v.getYear());
      assertEquals(5,    v.getMonthOfYear());
      assertEquals(6,    v.getDayOfMonth());
      assertEquals(15,   v.getHourOfDay());
      assertEquals(52,   v.getMinuteOfHour());
      assertEquals(21,   v.getSecondOfMinute());
      assertEquals("20050506155221", v.toString());
   }

   /**
    * Tests the <code>Timestamp$Value</code> constructor that accepts a
    * <code>Date</code> instance.
    */
   public void testTimestampValue_Date() throws Exception {

      Date d = null;
      Timestamp.Value v;
      try {
         v = new Timestamp.Value(d);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      d = new Date(2005 - 1900, 4, 6, 15, 52, 21);
      v = new Timestamp.Value(d);
      assertEquals(2005, v.getYear());
      assertEquals(5,    v.getMonthOfYear());
      assertEquals(6,    v.getDayOfMonth());
      assertEquals(15,   v.getHourOfDay());
      assertEquals(52,   v.getMinuteOfHour());
      assertEquals(21,   v.getSecondOfMinute());
      assertEquals("20050506155221", v.toString());

      d = new Date(0L);
      v = new Timestamp.Value(d);
      assertEquals(1970, v.getYear());
      assertEquals(1,    v.getMonthOfYear());
      assertEquals(1,    v.getDayOfMonth());
      assertEquals(0,    v.getHourOfDay());
      assertEquals(0,    v.getMinuteOfHour());
      assertEquals(0,    v.getSecondOfMinute());
      assertEquals("19700101000000", v.toString());
   }

   /**
    * Tests the <code>Timestamp$Value</code> constructor that accepts a
    * <code>long</code>.
    */
   public void testTimestampValue_long() throws Exception {

      long n = -1L;
      Timestamp.Value v;
      try {
         v = new Timestamp.Value(n);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      n = 0L;
      v = new Timestamp.Value(n);
      assertEquals(1970, v.getYear());
      assertEquals(1,    v.getMonthOfYear());
      assertEquals(1,    v.getDayOfMonth());
      assertEquals(0,    v.getHourOfDay());
      assertEquals(0,    v.getMinuteOfHour());
      assertEquals(0,    v.getSecondOfMinute());
      assertEquals("19700101000000", v.toString());

      n = 1000L;
      v = new Timestamp.Value(n);
      assertEquals(1970, v.getYear());
      assertEquals(1,    v.getMonthOfYear());
      assertEquals(1,    v.getDayOfMonth());
      assertEquals(0,    v.getHourOfDay());
      assertEquals(0,    v.getMinuteOfHour());
      assertEquals(1,    v.getSecondOfMinute());
      assertEquals("19700101000001", v.toString());
   }

   public void tearDown() throws Exception {
      TimeZone.setDefault(_timeZone);
   }
}
