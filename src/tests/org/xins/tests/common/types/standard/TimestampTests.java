package org.xins.tests.common.types.standard;

import java.util.Calendar;
import java.util.Date;

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


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Tests the <code>Timestamp$Value</code> constructor that accepts a
    * <code>Calendar</code> instance.
    */
   public void testTimestampValue_Calendar() throws Throwable {

      Calendar cal = null;
      Timestamp.Value v;
      try {
         v = new Timestamp.Value(cal);
         fail("Expected NullPointerException");
      } catch (NullPointerException exception) {
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
}
