/*
 * $Id$
 */
package org.xins.common.types.standard;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.types.Type;

/**
 * Item in an enumeration type.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @see List
 * @see Set
 */
public class ItemList {

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
    * Creates a new <code>ItemList</code>.
    */
   public ItemList() {
      _list = new java.util.ArrayList(10);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The list that contains the items. Cannot <code>null</code>.
    */
   private final java.util.List _list;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Adds an item to the list. The item is added at the end of the list.
    *
    * @param value
    *    the value of the item to add in the list, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>value == null</code>.
    */
   protected final  void add(Object value) {

      MandatoryArgumentChecker.check("value", value);

      _list.add(value);
   }

   /**
    * Gets the item at the specified index as an <code>Object</code>.
    *
    * @param index
    *    the position of the required item, it should be >= 0 and < getSize().
    *
    * @return
    *    the item, not <code>null</code>.
    */
   protected final Type get(int index) {
      return (Type) _list.get(index);
   }

   /**
    * Gets the number of items included in the list.
    *
    * @return
    *    the size of the list.
    */
   public int getSize() {
      return _list.size();
   }
}
