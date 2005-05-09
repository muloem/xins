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

      // 2 days after the Epoch (which is the start of January 1, 1970, in the
      // UTC time zone) at 1 o'clock in the afternoon
      long dayInMillis = 24 * 60 * 60 * 1000;
      n = (dayInMillis * 5) / 2;

      // Compensate for the time zone offset
      n -= TimeZone.getDefault().getOffset(n);

      v = new Timestamp.Value(n);
      assertEquals(1970, v.getYear());
      assertEquals(1,    v.getMonthOfYear());
      assertEquals(3,    v.getDayOfMonth());
      assertEquals(12,   v.getHourOfDay());
      assertEquals(0,    v.getMinuteOfHour());
      assertEquals(0,    v.getSecondOfMinute());
      assertEquals("19700103120000", v.toString());
   }

   /**
    * Tests the <code>Timestamp$Value</code> constructor that accepts a number
    * of <code>int</code> values.
    */
   public void testTimestampValue_ints() throws Exception {

      int year=2005, month=12, day=31, hour=12, minute=59, second=59;
      String asString = ""+year+month+day+hour+minute+second;
      Timestamp.Value v;

      v = new Timestamp.Value(year, month, day, hour, minute, second);
      assertEquals(year,     v.getYear());
      assertEquals(month,    v.getMonthOfYear());
      assertEquals(day,      v.getDayOfMonth());
      assertEquals(hour,     v.getHourOfDay());
      assertEquals(minute,   v.getMinuteOfHour());
      assertEquals(second,   v.getSecondOfMinute());
      assertEquals(asString, v.toString());
   }
}
