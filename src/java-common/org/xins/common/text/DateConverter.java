/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.text;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.DateTime;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Utility class for converting an Epoch date to a human-readable time stamp.
 *
 * <p>For example, the date 26 July 2003, time 17:03, 59 seconds and 653
 * milliseconds will convert to the string <code>"2003.07.26
 * 17:03:59.653"</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public class DateConverter extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Two-length string representations of the digits 0 to 99.
    */
   private static final char[][] VALUES;

   /**
    * The character zero (<code>'0'</code>) as an <code>int</code>.
    */
   private static final int ZERO = (int) '0';


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   static {
      // XXX: Allow test coverage analysis tools to report 100% coverage
      new DateConverter();

      // Fill the VALUES array
      VALUES = new char[100][];

      for (int i = 0; i < 100; i++) {
         int first  = ZERO + (i / 10);
         int second = ZERO + (i % 10);
         VALUES[i] = new char[] { (char) first, (char) second };
      }
   }

   /**
    * Convert the specified <code>long</code> to a human-readable time stamp.
    *
    * @param timeZone
    *    the time zone to use, cannot be <code>null</code>.
    *
    * @param n
    *    the time stamp to be converted to a human-readable character string,
    *    as a number of milliseconds since the Epoch (midnight January 1,
    *    1970), must be greater than {@link Long#MIN_VALUE} and smaller than
    *    {@link Long#MAX_VALUE}.
    *
    * @return
    *    the converted character string, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>n == {@link Long#MIN_VALUE} || n == {@link Long#MAX_VALUE} || timeZone == null</code>.
    */
   public static String toDateString(TimeZone timeZone, long n)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("timeZone", timeZone);
      if (n == Long.MIN_VALUE) {
         throw new IllegalArgumentException("n == Long.MIN_VALUE");
      } else if (n == Long.MAX_VALUE) {
         throw new IllegalArgumentException("n == Long.MAX_VALUE");
      }

      FastStringBuffer buffer = new FastStringBuffer(23);

      GregorianCalendar calendar = new GregorianCalendar(timeZone);

      // XXX: This works with Java 1.4+ only
      // calendar.setTimeInMillis(n);

      // XXX: This works with Java 1.3 as well
      calendar.setTime(new Date(n));

      int year  = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH);
      int day   = calendar.get(Calendar.DAY_OF_MONTH);
      int hour  = calendar.get(Calendar.HOUR_OF_DAY);
      int min   = calendar.get(Calendar.MINUTE);
      int sec   = calendar.get(Calendar.SECOND);
      int ms    = calendar.get(Calendar.MILLISECOND);

      // Append year followed by a dot, length is now 5
      buffer.append(year);
      buffer.append('.');

      // Append month followed by a dot, length is now 8
      buffer.append(VALUES[month + 1]); // Month is 0-based
      buffer.append('.');

      // Append day followed by a space, length is now 11
      buffer.append(VALUES[day]);
      buffer.append(' ');

      // Append hour followed by a colon, length is now 14
      buffer.append(VALUES[hour]);
      buffer.append(':');

      // Append minute followed by a colon, length is now 17
      buffer.append(VALUES[min]);
      buffer.append(':');

      // Append second followed by a dot, length is now 20
      buffer.append(VALUES[sec]);
      buffer.append('.');

      // Append milli-second, length is now 23
      if (ms < 10) {
         buffer.append("00");
      } else if (ms < 100) {
         buffer.append('0');
      }
      buffer.append(String.valueOf(ms));

      return buffer.toString();
   }

   /**
    * Formats a timestamp as a <code>String</code>.
    *
    * @param millis
    *    the timestamp, as a number of milliseconds since the Epoch.
    *
    * @param withCentury
    *    <code>true</code> if the century should be in the result, 
    *    <code>false</code> otherwise.
    *
    * @param separator
    *    the separator between the date and the hours, never <code>null</code>
    *    (can be an empty string though).
    *
    * @return
    *    the converted character string, cannot be <code>null</code>.
    *
    * @since XINS 1.3.0
    */
   public static String toDateString(long    millis,
                                     boolean withCentury,
                                     String  separator) {

      // Check preconditions
      MandatoryArgumentChecker.check("separator", separator);
      int separatorLength = separator.length();

      // Convert the millis to a GregorianCalendar instance
      DateTime calendar = new DateTime(millis);
      
      // Get all individual fields from the calendar
      int year  = calendar.getYear();
      int month = calendar.getMonthOfYear();
      int day   = calendar.getDayOfMonth();
      int hour  = calendar.getHourOfDay();
      int min   = calendar.getMinuteOfHour();
      int sec   = calendar.getSecondOfMinute();
      int ms    = calendar.getMillisOfSecond();
      
      // Add century and year or both
      int length = withCentury ? 17 : 15;
      length += separatorLength;
      char[] buffer = new char[length];
      int pos      = 0;
      char[] c;
      if (withCentury) {
         c = VALUES[year / 100];
         buffer[pos++] = c[0];
         buffer[pos++] = c[1];
      }
      c = VALUES[year % 100];
      buffer[pos++] = c[0];
      buffer[pos++] = c[1];

      // Add month (which is 0-based, so we need to add 1)
      c = VALUES[month + 1];
      buffer[pos++] = c[0];
      buffer[pos++] = c[1];

      // Add day
      c = VALUES[day];
      buffer[pos++] = c[0];
      buffer[pos++] = c[1];

      // Add separator between date and time
      for (int i = 0; i < separatorLength; i++) {
         buffer[pos++] = separator.charAt(i);
      }

      // Add hours
      c = VALUES[hour];
      buffer[pos++] = c[0];
      buffer[pos++] = c[1];

      // Add minutes
      c = VALUES[min];
      buffer[pos++] = c[0];
      buffer[pos++] = c[1];

      // Add seconds
      c = VALUES[sec];
      buffer[pos++] = c[0];
      buffer[pos++] = c[1];
         
      // Add milliseconds
      c = VALUES[ms / 10];
      buffer[pos++] = c[0];
      buffer[pos++] = c[1];
      buffer[pos++] = (char) (ZERO + (ms % 10));

      return new String(buffer);
   }

   
   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>DateConverter</code> object.
    */
   private DateConverter() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
