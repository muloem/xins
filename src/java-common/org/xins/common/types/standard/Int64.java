/*
 * $Id$
 */
package org.xins.common.types.standard;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_int64</em>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class Int64 extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static Int64 SINGLETON = new Int64();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Converts the specified non-<code>null</code> string value to a
    * <code>long</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the <code>long</code> value.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static long fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {
      if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else {
         try {
            long number = Long.parseLong(string);
            if (number < SINGLETON._minimum || number > SINGLETON._maximum) {
               throw new TypeValueException(SINGLETON, string);
            }
            return number;
         } catch (NumberFormatException nfe) {
            throw new TypeValueException(SINGLETON, string);
         }
      }
   }

   /**
    * Converts the specified string value to a <code>Long</code> value.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link Long}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Long fromStringForOptional(String string)
   throws TypeValueException {

      if (string == null) {
         return null;
      }

      try {
         Long number = Long.valueOf(string);
         long numberAsLong = number.longValue();
         if (numberAsLong < SINGLETON._minimum || numberAsLong > SINGLETON._maximum) {
            throw new TypeValueException(SINGLETON, string);
         }
         return number;
      } catch (NumberFormatException nfe) {
         throw new TypeValueException(SINGLETON, string);
      }
   }

   /**
    * Converts the specified <code>Long</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(Long value) {
      if (value == null) {
         return null;
      } else {
         return toString(value.longValue());
      }
   }

   /**
    * Converts the specified <code>long</code> to a string.
    *
    * @param value
    *    the value to convert.
    *
    * @return
    *    the textual representation of the value, never <code>null</code>.
    */
   public static String toString(long value) {
      return String.valueOf(value);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Int64</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Int64() {
      this("int64", Long.MIN_VALUE, Long.MAX_VALUE);
   }

   /**
    * Constructs a new <code>Int34</code> object (constructor for
    * subclasses).
    *
    * @param name
    *    the name of this type, cannot be <code>null</code>.
    *
    * @param minimum
    *    the minimum for the value.
    *
    * @param maximum
    *    the maximum for the value.
    */
   private Int64(String name, long minimum, long maximum) {
      super(name, java.lang.Long.class);

      _minimum = minimum;
      _maximum = maximum;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The minimum value that this Int34 can have.
    */
   private final long _minimum;

   /**
    * The maximum value that this Int34 can have.
    */
   private final long _maximum;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected boolean isValidValueImpl(String value) {
      try {
         long number = Long.parseLong(value);
         if (number < _minimum || number > _maximum) {
            return false;
         }
         return true;
      } catch (NumberFormatException nfe) {
         return false;
      }
   }

   protected Object fromStringImpl(String string) {
      return Long.valueOf(string);
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      java.lang.Long l = (java.lang.Long) value;
      return l.toString();
   }
}
