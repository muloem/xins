/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import java.net.MalformedURLException;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Standard type <em>_url</em>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public final class URL extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static URL SINGLETON = new org.xins.common.types.standard.URL();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Converts the specified non-<code>null</code> string value to a
    * <code>String</code>. This is in fact a no-op, the method will just
    * return the input value. This method exists to be in line with the
    * interfaces of the other standard type classes.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the original {@link String}.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    */
   public static String fromStringForRequired(String string)
   throws TypeValueException, IllegalArgumentException {
      if (string == null) {
         throw new IllegalArgumentException("string == null");
      } else if (!SINGLETON.isValidValue(string)) {
         throw new TypeValueException(SINGLETON, string);
      } else {
         return string;
      }
   }

   /**
    * Converts the specified string value to a <code>String</code>
    * value.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the original {@link String}, can be <code>null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static String fromStringForOptional(String string)
   throws TypeValueException {
      if (!SINGLETON.isValidValue(string)) {
         throw new TypeValueException(SINGLETON, string);
      } else {
         return string;
      }
   }

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Boolean</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private URL() {
      super("_url", java.lang.String.class);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected boolean isValidValueImpl(String value) {
      try {
         new TargetDescriptor(value);
         return true;
      } catch (MalformedURLException muex) {
         return false;
      }
   }

   protected Object fromStringImpl(String string) {
      return string;
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      return (String) value;
   }
}