/*
 * $Id$
 */
package org.xins.common.types.standard;

import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.xins.common.types.Type;
import org.xins.common.types.TypeValueException;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.net.URLEncoding;
import org.xins.common.text.FastStringBuffer;

/**
 * Standard type <em>_list</em>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 0.179
 */
public class List extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The only instance of this class. This field is never <code>null</code>.
    */
   public final static List SINGLETON = new List();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Constructs a <code>java.util.List</code> from the specified string
    * which is guaranteed to be non-<code>null</code>.
    *
    * @param string
    *    the string to convert, cannot be <code>null</code>.
    *
    * @return
    *    the {@link ItemList} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static ItemList fromStringForRequired(String string)
   throws IllegalArgumentException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("string", string);

      return (ItemList) SINGLETON.fromString(string);
   }

   /**
    * Constructs a <code>List</code> from the specified string.
    *
    * @param string
    *    the string to convert, can be <code>null</code>.
    *
    * @return
    *    the {@link ItemList}, or <code>null</code> if
    *    <code>string == null</code>.
    *
    * @throws TypeValueException
    *    if the specified string does not represent a valid value for this
    *    type.
    */
   public static ItemList fromStringForOptional(String string)
   throws TypeValueException {
      return (ItemList) SINGLETON.fromString(string);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>List</code>.
    * This constructor is private, the field {@link #SINGLETON} should be
    * used.
    */
   private List() {
      this("list", null);
   }

   /**
    * Constructs a new <code>List</code> object (constructor for
    * subclasses).
    *
    * @param name
    *    the name of this type, cannot be <code>null</code>.
    *
    * @param itemType
    *    the type for the values, or <code>null</code> if {@link Text}
    *    should be assumed.
    */
   protected List(String name, Type itemType) {
      super(name, ItemList.class);

      _itemType = itemType == null ? Text.SINGLETON : itemType;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The type for the values. Cannot be <code>null</code>.
    */
   private final Type _itemType;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected final boolean isValidValueImpl(String string) {

      if (string == null) {
         return false;
      }

      // Separate the string by ampersands
      StringTokenizer tokenizer = new StringTokenizer(string, "&");
      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         String item = URLEncoding.decode(token);
         if (!_itemType.isValidValue(item)) {
            return false;
         }
      }
      return true;
   }

   protected final Object fromStringImpl(String string)
   throws TypeValueException {

      // Construct a ItemList to store the values in
      ItemList list = new ItemList();

      // Separate the string by ampersands
      StringTokenizer tokenizer = new StringTokenizer(string, "&");
      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         String itemString = URLEncoding.decode(token);
         Object item = _itemType.fromString(itemString);
         list.add(item);
      }

      return list;
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // The argument must be a ItemList
      ItemList list = (ItemList) value;
      // Short-circuit if the argument is null
      if (list == null) {
         return null;
      }

      // Use a buffer to create the string
      FastStringBuffer buffer = new FastStringBuffer(255);

      // Iterate over the list
      for (int i=0; i < list.getSize(); i++) {
         if (i != 0) {
            buffer.append('&');
         }
         Type nextItem = list.get(i);

         buffer.append(URLEncoding.encode(nextItem.toString(nextItem)));
      }

      return buffer.toString();
   }
}
