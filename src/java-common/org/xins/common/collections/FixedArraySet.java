/*
 * $Id$
 */
package org.xins.common.collections;

import java.util.AbstractSet;
import java.util.Iterator;

/**
 * Unmodifiable <code>Set</code> implementation, based on an array.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class FixedArraySet
extends AbstractSet {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>FixedArraySet</code> for the specified array.
    *
    * @param array
    *    the array, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>array == null</code>.
    */
   public FixedArraySet(Object[] array)
   throws IllegalArgumentException {
      if (array == null) {
         throw new IllegalArgumentException("array == null");
      }

      _array = array;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The underlying array.
    */
   private final Object[] _array;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public int size() {
      return _array.length;
   }

   public Iterator iterator() {
      return new ArrayIterator(_array);
   }
}
