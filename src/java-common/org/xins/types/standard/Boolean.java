/*
 * $Id$
 */
package org.xins.types.standard;

import org.xins.types.Type;
import org.xins.types.TypeValueException;

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
   public final static Boolean SINGLETON = new Boolean();

   /**
    * Value <code>true</code>.
    */
   private final static java.lang.Boolean TRUE = new java.lang.Boolean(true);

   /**
    * Value <code>false</code>.
    */
   private final static java.lang.Boolean FALSE = new java.lang.Boolean(false);


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
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static java.lang.Boolean fromStringForOptional(String string)
   throws TypeValueException {
      if ("true".equals(string)) {
         return TRUE;
      } else if ("false".equals(string)) {
         return FALSE;
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
      if ("true".equals(string)) {
         return TRUE;
      } else {
         return FALSE;
      }
   }
}
