/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_int16</em>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public class Int16 extends Type {

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public static final Int16 SINGLETON = new Int16();

   /**
    * The minimum value that this Int16 can have.
    */
   private final short _minimum;

   /**
    * The maximum value that this Int16 can have.
    */
   private final short _maximum;

   /**
    * Constructs a new <code>Int16</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Int16() {
      this("_int16", Short.MIN_VALUE, Short.MAX_VALUE);
   }

   /**
    * Constructs a new <code>Int16</code> object (constructor for
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
   protected Int16(String name, short minimum, short maximum) {
      super(name, Short.class);

      _minimum = minimum;
      _maximum = maximum;
   }

   /**
    * Converts the specified non-<code>null</code> string value to a
    * <code>short</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the <code>short</code> value.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static short fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {
      if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else {
         try {
            return Short.parseShort(string);
         } catch (NumberFormatException nfe) {
            throw new TypeValueException(SINGLETON, string);
         }
      }
   }

   /**
    * Converts the specified string value to a <code>Short</code> value.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link Short}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static Short fromStringForOptional(String string)
   throws TypeValueException {

      if (string == null) {
         return null;
      }

      try {
         return  Short.valueOf(string);
      } catch (NumberFormatException nfe) {
         throw new TypeValueException(SINGLETON, string);
      }
   }

   /**
    * Converts the specified <code>Short</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(Short value) {
      if (value == null) {
         return null;
      } else {
         return toString(value.shortValue());
      }
   }

   /**
    * Converts the specified <code>short</code> to a string.
    *
    * @param value
    *    the value to convert.
    *
    * @return
    *    the textual representation of the value, never <code>null</code>.
    */
   public static String toString(short value) {
      return String.valueOf(value);
   }

   protected boolean isValidValueImpl(String value) {
      try {
         short number = Short.parseShort(value);
         if (number < _minimum || number > _maximum) {
            return false;
         }
         return true;
      } catch (NumberFormatException nfe) {
         return false;
      }
   }

   protected Object fromStringImpl(String string) throws TypeValueException {
      if (!isValidValue(string)) {
          throw new TypeValueException(this, string);
      }
      return Short.valueOf(string);
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      Short s = (Short) value;
      return s.toString();
   }

   public String getDescription() {
      return "A 16 bits precision integer number.";
   }
}
