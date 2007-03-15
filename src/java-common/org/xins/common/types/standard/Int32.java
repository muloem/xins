/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_int32</em>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class Int32 extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static Int32 SINGLETON = new Int32();

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
            return Integer.parseInt(string);
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
         return Integer.valueOf(string);
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

   /**
    * The minimum value that this Int32 can have.
    */
   private final int _minimum;

   /**
    * The maximum value that this Int32 can have.
    */
   private final int _maximum;

   /**
    * Constructs a new <code>Int32</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Int32() {
      this("_int32", Integer.MIN_VALUE, Integer.MAX_VALUE);
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
   protected Int32(String name, int minimum, int maximum) {
      super(name, Integer.class);

      _minimum = minimum;
      _maximum = maximum;
   }

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
      Integer i = (Integer) value;
      return i.toString();
   }
}
