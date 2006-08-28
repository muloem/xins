/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.FastStringBuffer;

/**
 * Standard type <em>_date</em>. A value of this type represents a
 * certain moment in time, with day-precision, without an indication of the
 * time zone.
 *
 * <p>The textual representation of a timestamp is always 8 numeric
 * characters, in the format:
 *
 * <blockquote><em>YYYYMMDD</em></blockquote>
 *
 * where:
 *
 * <ul>
 *    <li><em>YYYY</em> is the year, including the century, between 1970 and
 *        2999, for example <code>"2005"</code>.
 *    <li><em>MM</em> is the month of the year, 1-based, for example
 *        <code>"12"</code> for December.
 *    <li><em>DD</em> is the day of the month, 1-based, for example
 *        <code>"31"</code> for the last day of December.
 * </ul>
 *
 * <p>Note that all timestamps will be based on the current time zone (see
 * {@link java.util.TimeZone#getDefault()}).
 *
 * <p>A number of milliseconds can be used to indicate a specific instant in
 * time. This number of milliseconds is since the
 * <a href="http://en.wikipedia.org/wiki/Unix_Epoch">UNIX Epoch</a>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public class Date extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static Date SINGLETON = new Date();

   /**
    * Formatter that converts a date to a string.
    */
   private final static SimpleDateFormat FORMATTER =
      new SimpleDateFormat("yyyyMMdd");


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Constructs a <code>Date.Value</code> with the value of the current date.
    *
    * @return
    *    the {@link Value} for today, never <code>null</code>.
    */
   public static Value today() {
      return new Value(System.currentTimeMillis());
   }

   /**
    * Constructs a <code>Date.Value</code> from the specified string
    * which is guaranteed to be non-<code>null</code>.
    *
    * @param string
    *    the string to convert in the ISO format YYYYMMDD, cannot be <code>null</code>.
    *
    * @return
    *    the {@link Value} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Value fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return (Value) SINGLETON.fromString(string);
   }

   /**
    * Constructs a <code>Date.Value</code> from the specified string.
    *
    * @param string
    *    the string to convert in the ISO format YYYYMMDD, can be <code>null</code>.
    *
    * @return
    *    the {@link Value}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Value fromStringForOptional(String string)
   throws TypeValueException {
      return (Value) SINGLETON.fromString(string);
   }

   /**
    * Converts the specified <code>Date.Value</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value in the ISO format YYYYMMDD,
    *    or <code>null</code> if and only if <code>value == null</code>.
    */
   public static String toString(Value value) {

      // Short-circuit if the argument is null
      if (value == null) {
         return null;
      }

      return toString(value.getYear(),
                      value.getMonthOfYear(),
                      value.getDayOfMonth());
   }

   /**
    * Converts the specified combination of a year, month and day to a string.
    *
    * @param year
    *    the year, must be &gt;=0 and &lt;= 9999.
    *
    * @param month
    *    the month of the year, must be &gt;= 1 and &lt;= 12.
    *
    * @param day
    *    the day of the month, must be &gt;= 1 and &lt;= 31.
    *
    * @return
    *    the textual representation of the value in the ISO format YYYYMMDD,
    *    never <code>null</code>.
    */
   private static String toString(int year, int month, int day) {

      // Use a buffer to create the string
      FastStringBuffer buffer = new FastStringBuffer(8);

      // Append the year
      if (year < 10) {
         buffer.append("000");
      } else if (year < 100) {
         buffer.append("00");
      } else if (year < 1000) {
         buffer.append('0');
      }
      buffer.append(year);

      // Append the month
      if (month < 10) {
         buffer.append('0');
      }
      buffer.append(month);

      // Append the day
      if (day < 10) {
         buffer.append('0');
      }
      buffer.append(day);

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Date</code> instance.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Date() {
      super("_date", Value.class);
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected final boolean isValidValueImpl(String value) {

      // First check the length
      if (value.length() != 8) {
         return false;
      }

      // Convert all 3 components of the string to integers
      int y, m, d;
      try {
         y = Integer.parseInt(value.substring(0, 4));
         m = Integer.parseInt(value.substring(4, 6));
         d = Integer.parseInt(value.substring(6, 8));
      } catch (NumberFormatException nfe) {
         return false;
      }

      // Check that the values are in the correct range
      return (y >= 0) && (m >= 1) && (m <= 12) && (d >= 1) && (d <= 31);
   }

   protected final Object fromStringImpl(String string)
   throws TypeValueException {

      // Convert all 3 components of the string to integers
      int y, m, d;
      try {
         y = Integer.parseInt(string.substring(0, 4));
         m = Integer.parseInt(string.substring(4, 6));
         d = Integer.parseInt(string.substring(6, 8));
      } catch (NumberFormatException nfe) {

         // Should never happen, since isValidValueImpl(String) will have been
         // called
         throw new TypeValueException(this, string);
      }

      // Check that the values are in the correct range
      return new Value(y, m, d);
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // The argument must be a PropertyReader
      return toString((Value) value);
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Value for the type <em>_date</em>. Represents a specific moment in
    * time, with day-precision.
    *
    * @version $Revision$ $Date$
    * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
    *
    * @since XINS 1.0.0
    */
   public static final class Value extends Object implements Cloneable {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new date value. The values will not be checked.
       *
       * @param year
       *    the year, e.g. <code>2005</code>.
       *
       * @param month
       *    the month of the year, e.g. <code>11</code> for November.
       *
       * @param day
       *    the day of the month, e.g. <code>1</code> for the first day of the
       *    month.
       */
      public Value(int year, int month, int day) {

         // Construct the Calendar
         _calendar = Calendar.getInstance();
         _calendar.set(year, month - 1, day);
      }

      /**
       * Constructs a new date value based on the specified
       * <code>Calendar</code>.
       *
       * @param calendar
       *    the {@link java.util.Calendar} object to get the exact date from, cannot be
       *    <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>calendar == null</code>.
       *
       * @since XINS 1.2.0
       */
      public Value(Calendar calendar)
      throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("calendar", calendar);

         // Initialize fields
         _calendar = (Calendar) calendar.clone();
      }

      /**
       * Constructs a new date value based on the specified
       * <code>java.util.Date</code> object.
       *
       * @param date
       *    the {@link java.util.Date} object to get the exact date from,
       *    cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>date == null</code>.
       *
       * @since XINS 1.2.0
       */
      public Value(java.util.Date date) throws IllegalArgumentException {

         // Check preconditions
         MandatoryArgumentChecker.check("date", date);

         // Construct the Calendar
         _calendar = Calendar.getInstance();
         _calendar.setTime(date);
      }

      /**
       * Constructs a new date value based on the specified number of
       * milliseconds since the UNIX Epoch.
       *
       * @param millis
       *    the number of milliseconds since the
       *    <a href="http://en.wikipedia.org/wiki/Unix_Epoch">UNIX Epoch</a>.
       *
       * @throws IllegalArgumentException
       *    if <code>millis &lt; 0L</code>.
       *
       * @see System#currentTimeMillis()
       *
       * @since XINS 1.2.0
       */
      public Value(long millis) throws IllegalArgumentException {

         // Check preconditions
         if (millis < 0L) {
            throw new IllegalArgumentException("millis (" + millis + " < 0L");
         }

         // Convert the number of milliseconds to a Date object
         java.util.Date date = new java.util.Date(millis);

         // Construct the Calendar
         _calendar = Calendar.getInstance();
         _calendar.setTime(date);
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * Calendar representing the moment in time.
       */
      private Calendar _calendar;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Creates and returns a copy of this object.
       *
       * @return
       *    a copy of this object, never <code>null</code>.
       *
       * @see Object#clone()
       */
      public Object clone() {
         return new Value(_calendar);
      }

      /**
       * Returns the year.
       *
       * @return
       *    the year, between 1970 and 2999 (inclusive).
       */
      public int getYear() {
         return _calendar.get(Calendar.YEAR);
      }

      /**
       * Returns the month of the year.
       *
       * @return
       *    the month of the year, between 1 and 12 (inclusive).
       */
      public int getMonthOfYear() {
         return _calendar.get(Calendar.MONTH) + 1;
      }

      /**
       * Returns the day of the month.
       *
       * @return
       *    the day of the month, between 1 and 31 (inclusive).
       */
      public int getDayOfMonth() {
         return _calendar.get(Calendar.DAY_OF_MONTH);
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Value)) {
            return false;
         }

         // Compare relevant values
         Value that = (Value) obj;
         return (getYear()           == that.getYear()       )
             && (getMonthOfYear()    == that.getMonthOfYear())
             && (getDayOfMonth()     == that.getDayOfMonth() );
      }

      public int hashCode() {
         return _calendar.hashCode();
      }

      /**
       * Converts to a <code>java.util.Date</code> object.
       *
       * @return
       *    the {@link java.util.Date} corresponding to this value.
       *
       * @since XINS 1.2.0
       */
      public java.util.Date toDate() {
         return _calendar.getTime();
      }

      /**
       * Returns a textual representation of this object.
       *
       * @return
       *    the textual representation of this timestamp, never
       *    <code>null</code>.
       */
      public String toString() {
         return FORMATTER.format(_calendar.getTime());
      }
   }
}
