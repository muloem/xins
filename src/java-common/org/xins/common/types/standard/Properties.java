/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types.standard;

import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;
import org.xins.common.text.URLEncoding;
import org.xins.common.text.FastStringBuffer;

/**
 * Standard type <em>_properties</em>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public class Properties extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static Properties SINGLETON = new Properties();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Constructs a <code>PropertyReader</code> from the specified string
    * which is guaranteed to be non-<code>null</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the {@link PropertyReader} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static PropertyReader fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return (PropertyReader) SINGLETON.fromString(string);
   }

   /**
    * Constructs a <code>PropertyReader</code> from the specified string.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link PropertyReader}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static PropertyReader fromStringForOptional(String string)
   throws TypeValueException {
      return (PropertyReader) SINGLETON.fromString(string);
   }

   /**
    * Converts the specified <code>PropertyReader</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public static String toString(PropertyReader value) {

      // Short-circuit if the argument is null
      if (value == null) {
         return null;
      }

      // Use a buffer to create the string
      FastStringBuffer buffer = new FastStringBuffer(255);

      // Iterate over all properties
      Iterator names = value.getNames();
      boolean first = true;
      while (names.hasNext()) {

         // Prepend an ampersand before all but the first
         if (!first) {
            buffer.append('&');
         } else {
            first = false;
         }

         // Get name and value
         String propName = (String) names.next();
         String propValue = value.get(propName);

         // Append the name encoded
         buffer.append(URLEncoding.encode(propName));
         buffer.append('=');

         // Append the value encoded, iff it is not null
         if (propValue != null) {
            buffer.append(URLEncoding.encode(propValue));
         }
      }

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Properties</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private Properties() {
      this("properties", null, null);
   }

   /**
    * Constructs a new <code>Properties</code> object (constructor for
    * subclasses).
    *
    * @param name
    *    the name of this type, cannot be <code>null</code>.
    *
    * @param nameType
    *    the type for property names, or <code>null</code> if {@link Text}
    *    should be assumed.
    *
    * @param valueType
    *    the type for property values, or <code>null</code> if {@link Text}
    *    should be assumed.
    */
   protected Properties(String name, Type nameType, Type valueType) {
      super(name, PropertyReader.class);

      _nameType  = nameType  == null ? Text.SINGLETON : nameType;
      _valueType = valueType == null ? Text.SINGLETON : valueType;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The type for property names. Cannot be <code>null</code>.
    */
   private final Type _nameType;

   /**
    * The type for property values. Cannot be <code>null</code>.
    */
   private final Type _valueType;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected final boolean isValidValueImpl(String string) {

      // Store the property keys
      HashSet propertyKeys = new HashSet();

      if (string == null) {
         return false;
      }

      // Separate the string by ampersands
      StringTokenizer tokenizer = new StringTokenizer(string, "&");
      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         int index = token.indexOf('=');
         if (index < 1) {
            return false;
         } else if (token.length() > (index + 1) && token.indexOf('=', index + 1) >= 0) {
            return false;
         } else {
            String name  = URLEncoding.decode(token.substring(0, index));
            if (propertyKeys.contains(name)) {
               return false;
            }
            propertyKeys.add(name);
            String value = token.substring(index + 1);
            if (value.length() < 1) {
               value = null;
            } else {
               value = URLEncoding.decode(value);
            }

            if (!_nameType.isValidValue(name)) {
               return false;
            }
            if (!_valueType.isValidValue(value)) {
               return false;
            }
         }
      }
      return true;
   }

   protected final Object fromStringImpl(String string)
   throws TypeValueException {

      // Construct a PropertyReader to store the properties in
      BasicPropertyReader pr = new BasicPropertyReader();

      // Store the property keys
      HashSet propertyKeys = new HashSet();

      // Separate the string by ampersands
      StringTokenizer tokenizer = new StringTokenizer(string, "&");
      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         int index = token.indexOf('=');
         if (index < 1) {
            throw new TypeValueException(SINGLETON, string, "The token \"" + token + "\" does not contain an equals sign ('=').");
         } else if (token.length() > (index + 1) && token.indexOf('=', index + 1) >= 0) {
            throw new TypeValueException(SINGLETON, string);
         } else {
            String name  = URLEncoding.decode(token.substring(0, index));
            if (propertyKeys.contains(name)) {
               throw new TypeValueException(SINGLETON, string, "The key \"" + name + "\" is already set.");
            }
            propertyKeys.add(name);
            String value = token.substring(index + 1);
            if (value.length() < 1) {
               value = null;
            } else {
               value = URLEncoding.decode(value);
            }

            _nameType.checkValue(name);
            _valueType.checkValue(value);

            pr.set(name, value);
         }
      }

      return pr;
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // The argument must be a PropertyReader
      return toString((PropertyReader) value);
   }
}
