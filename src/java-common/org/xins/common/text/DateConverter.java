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

import java.text.SimpleDateFormat;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Utility class for converting an Epoch date to a human-readable time stamp.
 *
 * <p>For example, the date 26 July 2003, time 17:03, 59 seconds and 653
 * milliseconds will convert to the string
 * <code>"2003.07.26 17:03:59.653"</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
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

   /**
    * The time zone for this host.
    */
   private final static TimeZone TIME_ZONE = TimeZone.getDefault();

   /**
    * The cached time.
    */
   private static long CACHED_TIME = -1000L;

   /**
    * The <code>String</code> representation of the cached time as 
    * yyyyMMdd-HHmmss.
    */
   private static String CACHED_TIME_STRING;

   /**
    * The lock for the cached time.
    */
   private final static Object TIME_LOCK = new Object();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   static {

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
    * Formats the timestamp as a <code>String</code>.
    *
    * @param millis
    *    the timestamp, as a number of milliseconds since the Epoch.
    *
    * @param withCentury
    *    <code>true</code> if the century should be in the result, 
    *    <code>false</code> otherwise.
    *
    * @return
    *    the converted character string, cannot be <code>null</code>.
    *
    * @since XINS 1.3.0
    */
   public static String toDateString(long millis, boolean withCentury) {
      
      long millisOnly = millis % 1000L;
      long delta = millis - CACHED_TIME;
      
      synchronized (TIME_LOCK) {
         
         // If we are in the same second, just append the milli seconds.
         if (delta < 1000L && 
               millisOnly < CACHED_TIME % 1000L && delta > 0L) {
            FastStringBuffer buffer = new FastStringBuffer(18, CACHED_TIME_STRING);
            appendMillis(buffer, (int) millisOnly);
            String date = buffer.toString();
            if (withCentury) {
               return date;
            } else {
               return date.substring(2);
            }
            
         // If we are in the same minute, just change the seconds and append the milli seconds.
         } else if (CACHED_TIME_STRING != null && delta > 0L) {
            int secondsOnly = Integer.parseInt(CACHED_TIME_STRING.substring(13));
            long secondsDiff = delta / 1000L;
            if (secondsDiff < (60 - secondsOnly)) {
               FastStringBuffer buffer = new FastStringBuffer(18);
               buffer.append(CACHED_TIME_STRING.substring(0, 13));
               int newSeconds = secondsOnly + (int) secondsDiff;
               if (newSeconds < 10) {
                  buffer.append('0');
               }
               buffer.append(newSeconds);
               CACHED_TIME = millis;
               CACHED_TIME_STRING = buffer.toString();
               appendMillis(buffer, (int) millisOnly);
               String date = buffer.toString();
               if (withCentury) {
                  return date;
               } else {
                  return date.substring(2);
               }
            }
         }
      }
      String date = toDateString(millis, true, "-");
      synchronized (TIME_LOCK) {
         CACHED_TIME = millis;
         CACHED_TIME_STRING = date;
      }
      FastStringBuffer buffer = new FastStringBuffer(18);
      if (withCentury) {
         buffer.append(date);
      } else {
         buffer.append(date.substring(2));
      }
      appendMillis(buffer, (int) millisOnly);
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
    *    the separator between the date and the hours, or <code>null</code>
    *    if no separator should be set.
    *
    * @return
    *    the converted character string without the milliseconds, 
    *    cannot be <code>null</code>.
    */
   private static String toDateString(long    millis,
                                      boolean withCentury,
                                      String  separator) {

      // Convert the millis to a GregorianCalendar instance
      GregorianCalendar calendar = new GregorianCalendar(TIME_ZONE);
      Date date = new Date(millis);
      calendar.setTime(date);
      
      // Get all individual fields from the calendar
      int year  = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH);
      int day   = calendar.get(Calendar.DAY_OF_MONTH);
      int hour  = calendar.get(Calendar.HOUR_OF_DAY);
      int min   = calendar.get(Calendar.MINUTE);
      int sec   = calendar.get(Calendar.SECOND);
      
      // Add century and year or both
      FastStringBuffer buffer = new FastStringBuffer(23);
      if (withCentury) {
         int century  = year / 100;
         buffer.append(VALUES[century]);
      }
      int yearOnly = year % 100;
      buffer.append(VALUES[yearOnly]);

      // Add month (which is 0-based, so we need to add 1)
      buffer.append(VALUES[month + 1]);

      // Add day
      buffer.append(VALUES[day]);

      // Add separator between date and time
      if (separator != null) {
         buffer.append(separator);
      }

      // Add hours, minutes and seconds
      buffer.append(VALUES[hour]);
      buffer.append(VALUES[min]);
      buffer.append(VALUES[sec]);

      return buffer.toString();
   }

   /**
    * Appends the millis to the <code>FastStringBuffer</code>.
    *
    * @param buffer
    *    the buffer, cannot be <code>null</code>.
    *
    * @param millis
    *    the millis to add.
    */
   private static void appendMillis(FastStringBuffer buffer, int millis) {
      if (millis < 10) {
         buffer.append(VALUES[0]);
      } else if (millis < 100) {
         buffer.append('0');
      }
      buffer.append(millis);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>DateConverter</code>.
    *
    * @param withCentury
    *    <code>true</code> if the century should be in the result, 
    *    <code>false</code> otherwise.
    *
    * @since XINS 1.3.0
    */
   public DateConverter(boolean withCentury) {

      // Store the flag
      _withCentury = withCentury;

      // Determine the length of the formatted date strings
      _length           = withCentury ? 18 : 16;
      _cachedDateBuffer = new char[_length];

      // Construct a formatter for slow formatting
      String format = "yyMMdd-HHmmssSSS";
      if (withCentury) {
         format = "yy" + format;
      }
      _formatter = new SimpleDateFormat(format);

      // Pre-cache the current date
      recompute(System.currentTimeMillis());
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Date formatter that is used as a slow but accurate method for formatting
    * the date. Never <code>null</code>.
    */
   private final SimpleDateFormat _formatter;

   /**
    * Flag that indicates whether the century should be in the formatted date.
    * Is <code>true</code> if the century should be in there, or
    * <code>false</code> if not.
    */
   private final boolean _withCentury;

   /**
    * Length of the produced timestamp string. If the century is printed, it's
    * 18, otherwise it's 16.
    */
   private final int _length;

   /**
    * Cached date, as a number of milliseconds since the Epoch.
    */
   private long _cachedDate;

   /**
    * Cached date, as a number of minutes since the Epoch.
    */
   private long _cachedMinutes;

   /**
    * Part of the cached date, the number of seconds in the current minute.
    */
   private long _cachedJustSeconds;

   /**
    * Character buffer containing the formatted version of the cached date.
    * See {@link #_cachedDate}. The length is always equal to
    * {@link #_length}.
    */
   private char[] _cachedDateBuffer;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Recomputes the cached formatted date.
    *
    * @param date
    *    the timestamp to reinitialize the cache with, as a number of
    *    milliseconds since the Epoch.
    */
   private void recompute(long date) {

      // Store the cached date
      _cachedDate        = date;
      _cachedMinutes     = (date / 60000L);
      long seconds       = (date /  1000L);
      _cachedJustSeconds = (int) (seconds %    60L);

      // Format the date
      String s = _formatter.format(new Date(_cachedDate));
      s.getChars(0, _length, _cachedDateBuffer, 0);
   }

   /**
    * Formats the specified timestamp as a <code>String</code>. Depending on
    * the setting of the <em>withCentury</em> property (passed to the
    * constructor), the format is as follows. If <em>withCentury</em> is set,
    * then the format is:
    *
    * <blockquote><em>CCYYMMDD-hhmmssSSS</em> (length is 18)</blockquote>
    *
    * Otherwise, if <em>withCentury</em> is not set, then the format is
    * without the century:
    * 
    * <blockquote><em>YYMMDD-hhmmssSSS</em> (length is 16)</blockquote>
    *
    * <p>Note: This method is <em>not</em> thread-safe.
    *
    * @param date
    *    the timestamp, in milliseconds since the Epoch (midnight at the start
    *    of January 1, 1970, GMT); must be &gt;= <code>0L</code>.
    *
    * @param buffer
    *    the character buffer to put the formatted timestamp in, cannot be
    *    <code>null</code>.
    *
    * @param offset
    *    the offset into the character buffer.
    *
    * @throws IllegalArgumentException
    *    if <code>date &lt; 0L</code>.
    *
    * @throws NullPointerException
    *    if <code>buffer == null</code>.
    *
    * @throws IndexOutOfBoundsException
    *    if <code>offset</code> is invalid (less than <code>0</code>) or
    *    incorrect (not leaving enough room for the formatter date).
    *
    * @since XINS 1.3.0
    */
   public void format(long date, char[] buffer, int offset)
   throws IllegalArgumentException,
          NullPointerException,
          IndexOutOfBoundsException {

      // Check precondition
      if (date < 0L) {
         throw new IllegalArgumentException("date (" + date + ") < 0L");
      }

      // Cache the length of the generated string
      int length = _length;

      // Compute the delta with the cached date
      long delta = date - _cachedDate;

      // If we are in the same millisecond, then short-circuit
      if (delta == 0) {
         System.arraycopy(_cachedDateBuffer, 0, buffer, offset, length);
         return;
      }

      // Determine the number of seconds and milliseconds
      long minutes     =       (date    / 60000L);
      long seconds     =       (date    /  1000L);
      int  justSeconds = (int) (seconds %    60L);
      int  justMillis  = (int) (date    %  1000L);

      // We are in the same minute
      if (minutes == _cachedMinutes) {

         // First copy the whole cached formatted string
         System.arraycopy(_cachedDateBuffer, 0, buffer, offset, length);

         // If we are not in the same second, correct the seconds
         if (justSeconds != _cachedJustSeconds) {
            buffer[length - 5] = (char) (ZERO + (justSeconds / 10));
            buffer[length - 4] = (char) (ZERO + (justSeconds % 10));
         }

         // Correct the milliseconds
         buffer[length - 3] = (char) (ZERO + ( justMillis / 100)      );
         buffer[length - 2] = (char) (ZERO + ((justMillis % 100) / 10));
         buffer[length - 1] = (char) (ZERO + ( justMillis %  10)      );

      // We are not in the same minute, so recompute
      } else {
         recompute(date);
         System.arraycopy(_cachedDateBuffer, 0, buffer, offset, length);
      }
   }
}
