/*
 * $Id$
 */
package org.xins.common.types.standard;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_int32</em>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class Int32 extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static Int32 SINGLETON = new Int32();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Converts the specified non-<code>null</code> string value to an
    * <code>int</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the <code>int</code> value.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static int fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {
      if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else {
         try {
            int number = Integer.parseInt(string);
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
    * Converts the specified string value to an <code>Integer</code> value.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link Integer}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Integer fromStringForOptional(String string)
   throws TypeValueException {

      if (string == null) {
         return null;
      }

      try {
         Integer number = Integer.valueOf(string);
         int numberAsInt = number.intValue();
         if (numberAsInt < SINGLETON._minimum || numberAsInt > SINGLETON._maximum) {
            throw new TypeValueException(SINGLETON, string);
         }
         return number;
      } catch (NumberFormatException nfe) {
         throw new TypeValueException(SINGLETON, string);
      }
   }

   /**
    * Converts the specified <code>Integer</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(Integer value) {
      if (value == null) {
         return null;
      } else {
         return toString(value.intValue());
      }
   }

   /**
    * Converts the specified <code>int</code> to a string.
    *
    * @param value
    *    the value to convert.
    *
    * @return
    *    the textual representation of the value, never <code>null</code>.
    */
   public static String toString(int value) {
      return String.valueOf(value);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Int32</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Int32() {
      this("int32", Integer.MIN_VALUE, Integer.MAX_VALUE);
   }

   /**
    * Constructs a new <code>Int32</code> object (constructor for
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
   private Int32(String name, int minimum, int maximum) {
      super(name, java.lang.Integer.class);

      _minimum = minimum;
      _maximum = maximum;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The minimum value that this Int32 can have.
    */
   private final int _minimum;

   /**
    * The maximum value that this Int32 can have.
    */
   private final int _maximum;

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected boolean isValidValueImpl(String value) {
      try {
         int number = Integer.parseInt(value);
         if (number < _minimum || number > _maximum) {
            return false;
         }
         return true;
      } catch (NumberFormatException nfe) {
         return false;
      }
   }

   protected Object fromStringImpl(String string) {
      return Integer.valueOf(string);
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      java.lang.Integer i = (java.lang.Integer) value;
      return i.toString();
   }
}
