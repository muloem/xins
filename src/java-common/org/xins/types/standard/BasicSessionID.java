/*
 * $Id$
 */
package org.xins.types.standard;

import org.xins.types.Type;
import org.xins.types.TypeValueException;
import org.xins.util.LongUtils;

/**
 * Standard type <em>_basicSessionID</em>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class BasicSessionID extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static BasicSessionID SINGLETON = new BasicSessionID();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Converts the specified non-<code>null</code> string value to a
    * <code>long</code> session identifier.
    *
    * <p>The string must be in the form:
    *
    * <blockquote><em>startup</em>:<em>counter</em>
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
   public static long fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {
      if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else if (string.length() != 17) {
         throw new TypeValueException(SINGLETON, string);
      }

      String part1 = string.substring(0, 9);
      if (false) { // TODO: Make sure part1 is equal to the API startup time
         throw new TypeValueException(SINGLETON, string);
      }

      String part2 = string.substring(9);

      try {
         return Long.parseLong(part2);
      } catch (NumberFormatException nfe) {
         throw new TypeValueException(SINGLETON, string);
      }
   }

   /**
    * Converts the specified string value to a <code>Long</code> session
    * identifier.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
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

      return new Long(fromStringForRequired(string));
   }

   /**
    * Converts the specified <code>Long</code> session identifier to a string.
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
    * Converts the specified <code>long</code> session identifier to a string.
    *
    * @param value
    *    the value to convert.
    *
    * @return
    *    the textual representation of the value, never <code>null</code>.
    */
   public static String toString(long value) {
      // TODO: Prepend API startup time
      return "TODOTODO:" + LongUtils.toHexString(value);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>BasicSessionID</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private BasicSessionID() {
      super("basicSessionID", java.lang.Long.class);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected boolean isValidValueImpl(String value) {
      return false; // TODO
   }

   protected Object fromStringImpl(String string)
   throws TypeValueException {
      return fromStringForOptional(string);
   }
}
