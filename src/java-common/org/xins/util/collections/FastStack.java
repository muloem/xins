/*
 * $Id$
 */
package org.xins.util.collections;

import java.util.EmptyStackException;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Fast, unsynchronized stack implementation.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class FastStack extends Object {

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
    * Constructs a new <code>FastStack</code> object with the specified
    * initial capacity.
    *
    * @param capacity
    *    the initial capacity, must be &gt;= 0.
    *
    * @throws IllegalArgumentException
    *    if <code>capacity &lt; 0</code>.
    */
   public FastStack(int capacity)
   throws IllegalArgumentException {
      if (capacity < 0) {
         throw new IllegalArgumentException("capacity (" + capacity + ") < 0");
      }

      _elements = new Object[capacity];
      _size     = 0;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The underlying <code>Object[]</code> array. The size of this array is
    * the capacity of this stack.
    */
   private Object[] _elements;

   /**
    * The actual size of this stack. Is always less than or equal to the
    * capacity.
    */
   private int _size;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Ensures that the specified needed capacity is actually available. If it
    * is not, then the internal array will be expanded. The new capacity will
    * be larger than or equal to the needed capacity.
    *
    * @param needed
    *    the needed capacity.
    */
   private void ensureCapacity(int needed) {
      int current = _elements.length;
      if (current < needed) {
         int newCapacity = needed + 4; // XXX: Is this okay?
         Object[] newArray = new Object[newCapacity];
         System.arraycopy(_elements, 0, newArray, 0, current);
         _elements = newArray;
      }
   }

   /**
    * Returns the size of this stack.
    *
    * @return
    *    the size of this stack, always &gt;= 0.
    */
   public int getSize() {
      return _size;
   }

   /**
    * Pushes the specified element. If necessary, the capacity of this
    * stack will be increased.
    *
    * @param o
    *    the {@link Object} to add, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>o == null</code>.
    */
   public void push(Object o) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("o", o);
      ensureCapacity(_size + 1);
      _elements[_size++] = o;
   }

   /**
    * Pops the top element from this stack. This reduces the size of the stack
    * with 1.
    *
    * @return
    *    the top element, cannot be <code>null</code>.
    *
    * @throws EmptyStackException
    *    if there are no elements on this stack.
    */
   public Object pop()
   throws EmptyStackException {
      if (_size == 0) {
         throw new EmptyStackException();
      }
      Object o = _elements[--_size];
      _elements[_size] = null;
      return o;
   }

   /**
    * Gets the top element from this stack. This does not modify this stack.
    *
    * @return
    *    the top element, cannot be <code>null</code>.
    *
    * @throws EmptyStackException
    *    if there are no elements on this stack.
    */
   public Object peek()
   throws EmptyStackException {
      if (_size == 0) {
         throw new EmptyStackException();
      }
      return _elements[_size - 1];
   }

   /**
    * Removes all elements from this stack. The capacity will remain
    * untouched, though.
    */
   public void clear() {
      for (int i = 0; i < _size; i++) {
         _elements[i] = null;
      }
      _size = 0;
   }
}
