/*
 * $Id$
 */
package org.xins.common.types;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Enumeration type. An enumeration type only accepts a limited set of
 * possible values.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<A href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</A>)
 *
 * @see EnumItem
 */
public abstract class EnumType extends Type {

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
      super(name, EnumItem.class);

      Map namesToValues = new HashMap();
      Map valuesToNames = new HashMap();
      Map namesToItems  = new HashMap();
      Map valuesToItems = new HashMap();
      List itemList = new ArrayList();

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
            namesToItems.put(itemName,   item);
            valuesToItems.put(itemValue, item);
            itemList.add(item);
         }
      }

      _values = new String[actualItems];
      System.arraycopy(values, 0, _values, 0, actualItems);

      _namesToValues = Collections.unmodifiableMap(namesToValues);
      _valuesToNames = Collections.unmodifiableMap(valuesToNames);
      _namesToItems  = Collections.unmodifiableMap(namesToItems);
      _valuesToItems = Collections.unmodifiableMap(valuesToItems);
      _items = Collections.unmodifiableList(itemList);
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
    * Map that links symbolic names to enumeration item objects. This map is
    * unmodifiable.
    */
   protected final Map _namesToItems;

   /**
    * Map that links enumeration values to enumeration item objects. This map
    * is unmodifiable.
    */
   protected final Map _valuesToItems;

   /**
    * List of the <code>EnumItem</code>. This list is unmodifiable.
    */
   protected final List _items;

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

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      EnumItem item = (EnumItem) value;
      return item.getValue();
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
   public final String getValueByName(String name)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

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
   public final String getNameByValue(String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      return (String) _valuesToNames.get(value);
   }

   /**
    * Get the list of the EnumItem included in this <code>EnumType</code>.
    *
    * @return
    *    the list of {@link EnumItem} included in this <code>EnumType</code>.
    */
   public final List getEnumItems() {
      return _items;
   }
}
