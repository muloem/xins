/*
 * $Id$
 */
package org.xins.types;

import java.util.HashMap;
import java.util.Map;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Enumeration type. An enumeration type only accepts a limited set of
 * possible values.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<A href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</A>)
 */
public class EnumType extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>EnumType</code> instance. The name of the type needs
    * to be specified. The value class (see {@link Type#getValueClass()}) is
    * set to {@link String String.class}.
    *
    * <p />The items this type accepts should be passed. If
    * <code>items == null</code>, then this type will contain no items. This
    * is the same as passing a zero-size {@link EnumItem} array.
    *
    * <p />Note that the <code>items</code> array may contain
    * <code>null</code> values. These will be ignored.
    *
    * @param name
    *    the name of the type, not <code>null</code>.
    *
    * @param items
    *    the items for the type, or <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public EnumType(String name, EnumItem[] items)
   throws IllegalArgumentException {
      super(name, String.class);

      Map namesToValues = new HashMap();
      Map valuesToNames = new HashMap();

      // TODO: Use ArrayMap ?

      int count = items == null ? 0 : items.length;
      String[] values = new String[count];
      int actualItems = 0;
      for (int i = 0; i < count; i++) {
         EnumItem item = items[i];
         if (item != null) {
            String itemName  = item.getName();
            String itemValue = item.getValue();

            namesToValues.put(itemName,  itemValue);
            valuesToNames.put(itemValue, itemName);
            values[actualItems++] = itemValue;
         }
      }

      _values = new String[actualItems];
      System.arraycopy(values, 0, _values, 0, actualItems);

      _namesToValues = Collections.unmodifiableMap(namesToValues);
      _valuesToNames = Collections.unmodifiableMap(valuesToNames);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Map that links symbolic names to enumeration values. This map is
    * unmodifiable.
    */
   protected final Map _namesToValues;

   /**
    * Map that links enumeration values to their symbolic names. This map is
    * unmodifiable.
    */
   protected final Map _valuesToNames;

   /**
    * The list of accepted values.
    */
   private final String[] _values;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected final boolean isValidValueImpl(String value) {
      for (int i = 0; i < _values.length; i++) {
         if (_values[i].equals(value)) {
            return true;
         }
      }
      return false;
   }

   protected final Object fromStringImpl(String value) {
      return value;
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      String s = (String) value;
      if (!isValidValueImpl(s)) {
         throw new TypeValueException(this, s);
      }
      return s;
   }

   /**
    * Gets the value matching the specified name.
    *
    * @param name
    *    the name to match a corresponding value by, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the corresponding value, or <code>null</code> if there is none.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    *
    * @since XINS 0.109
    */
   public final String getValueByName(String name) {
      return (String) _namesToValues.get(name);
   }

   /**
    * Gets the name matching the specified value.
    *
    * @param value
    *    the value to match a corresponding name by, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the corresponding name, or <code>null</code> if there is none.
    *
    * @throws IllegalArgumentException
    *    if <code>value == null</code>.
    *
    * @since XINS 0.109
    */
   public final String getNameByValue(String value) {
      return (String) _valuesToNames.get(value);
   }

   /**
    * Gets the value matching the specified name.
    *
    * @param name
    *    the name to match a corresponding value by, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the corresponding value, or <code>null</code> if there is none.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    *
    * @deprecated
    *    Deprecated since XINS 0.109, use {@link #getValueByName(String)}
    *    instead.
    */
   public final String getByName(String name) {
      return (String) _namesToValues.get(name);
   }

   /**
    * Gets the name matching the specified value.
    *
    * @param value
    *    the value to match a corresponding name by, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the corresponding name, or <code>null</code> if there is none.
    *
    * @throws IllegalArgumentException
    *    if <code>value == null</code>.
    *
    * @deprecated
    *    Deprecated since XINS 0.109, use {@link #getNameByValue(String)}
    *    instead.
    */
   public final String getByValue(String value) {
      return (String) _valuesToNames.get(value);
   }
}
