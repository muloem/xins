/*
 * $Id$
 */
package org.xins.util.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator implementation that reads from an array. No modifications are
 * allowed, so {@link #remove()} will throw an
 * {@link UnsupportedOperationException}.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class ArrayIterator
extends Object
implements Iterator {

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
    * Constructs a new <code>ArrayIterator</code> for the specified array.
    *
    * @param array
    *    the array, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>array == null</code>.
    */
   public ArrayIterator(Object[] array)
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

   /**
    * The current index in the array.
    */
   private int _index;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public boolean hasNext() {
      return _index < _array.length;
   }

   public Object next()
   throws NoSuchElementException {
      if (_index == _array.length) {
         throw new NoSuchElementException();
      }
      return _array[_index++];
   }

   public void remove()
   throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }
}
