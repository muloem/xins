/*
 * $Id$
 */
package org.xins.common.types;

import java.util.StringTokenizer;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.net.URLEncoding;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.types.standard.Text;

/**
 * Standard type <em>_list</em>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 0.185
 */
public class List extends Type {

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
      ItemList list = createList();

      // Separate the string by ampersands
      StringTokenizer tokenizer = new StringTokenizer(string, "&");
      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         String itemString = URLEncoding.decode(token);
         Object item = _itemType.fromString(itemString);
         list.addItem(item);
      }

      return list;
   }

   /**
    * Creates a new ItemList.
    *
    * @return
    *    the new list created, never <code>null</code>.
    */
   public ItemList createList() {
      return null;
   }

   /**
    * Converts the specified <code>ItemList</code> to a string.
    *
    * @param value
    *    the value to convert, can be <code>null</code>.
    *
    * @return
    *    the textual representation of the value, or <code>null</code> if and
    *    only if <code>value == null</code>.
    */
   public String toString(ItemList value) {

      // Short-circuit if the argument is null
      if (value == null) {
         return null;
      }

      // Use a buffer to create the string
      FastStringBuffer buffer = new FastStringBuffer(255);

      // Iterate over the list
      int listSize = value.getSize();
      for (int i=0; i < listSize; i++) {
         if (i != 0) {
            buffer.append('&');
         }

         Object nextItem = value.getItem(i);
         String stringItem = null;
         try {
            stringItem = _itemType.toString(nextItem);
         } catch (Exception ex) {
            ex.printStackTrace();
            // Should never happens as only add() is able to add items in the list.
            throw new IllegalArgumentException("Incorrect value for type: " + nextItem);
         }
         buffer.append(URLEncoding.encode(stringItem));
      }

      return buffer.toString();
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {

      // Check preconditions
      MandatoryArgumentChecker.check("value", value);

      // The argument must be a ItemList
      return toString((ItemList) value);
   }
}
