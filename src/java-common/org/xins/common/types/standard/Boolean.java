/*
 * $Id$
 */
package org.xins.common.types.standard;

import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.BooleanConstants;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_boolean</em>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class Boolean extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static Boolean SINGLETON = new org.xins.common.types.standard.Boolean();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Converts the specified non-<code>null</code> string value to a
    * <code>boolean</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the <code>boolean</code> value.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static boolean fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {
      if ("true".equals(string)) {
         return true;
      } else if ("false".equals(string)) {
         return false;
      } else if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else {
         throw new TypeValueException(SINGLETON, string);
      }
   }

   /**
    * Converts the specified string value to a <code>java.lang.Boolean</code>
    * value.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link java.lang.Boolean}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static java.lang.Boolean fromStringForOptional(String string)
   throws TypeValueException {
      if ("true".equals(string)) {
         return BooleanConstants.TRUE;
      } else if ("false".equals(string)) {
         return BooleanConstants.FALSE;
      } else if (string == null) {
         return null;
      } else {
         throw new TypeValueException(SINGLETON, string);
      }
   }

   /**
    * Converts the specified <code>Boolean</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(java.lang.Boolean value) {
      if (value == null) {
         return null;
      } else {
         return toString(value.booleanValue());
      }
   }

   /**
    * Converts the specified <code>boolean</code> to a string.
    *
    * @param value
    *    the value to convert.
    *
    * @return
    *    the textual representation of the value, never <code>null</code>.
    */
   public static String toString(boolean value) {
      return value ? "true" : "false";
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Boolean</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Boolean() {
      super("boolean", java.lang.Boolean.class);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected boolean isValidValueImpl(String value) {
      return "true".equals(value) || "false".equals(value);
   }

   protected Object fromStringImpl(String string) {
      return "true".equals(string) ? BooleanConstants.TRUE : BooleanConstants.FALSE;
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      java.lang.Boolean b = (java.lang.Boolean) value;
      return b.toString();
   }
}
