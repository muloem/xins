/*
 * $Id$
 */
package org.xins.common.collections;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Iterator implementation that reads from an <code>Enumeration</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class EnumerationIterator
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
    * Constructs a new <code>EnumerationIterator</code> on top of the
    * specified <code>Enumeration</code>.
    *
    * @param enumeration
    *    the {@link Enumeration} object, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>enumeration == null</code>.
    */
   public EnumerationIterator(Enumeration enumeration)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("enumeration", enumeration);

      _enumeration = enumeration;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The underlying <code>Enumeration</code> object.
    */
   private final Enumeration _enumeration;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Checks if the iteration has more elements.
    *
    * @return
    *    <code>true</code> if the iteration has more elements,
    *    <code>false</code> otherwise.
    */
   public boolean hasNext() {
      return _enumeration.hasMoreElements();
   }

   /**
    * Returns the next element in the iteration.
    *
    * @return
    *    the next element.
    *
    * @throws NoSuchElementException
    *    if the iteration has no more elements.
    */
   public Object next() throws NoSuchElementException {
      return _enumeration.nextElement();
   }

   /**
    * Removes the last element returned by the iterator (unsupported
    * operation).
    *
    * <p>The implementation of this method in class
    * {@link EnumerationIterator} always throws an
    * {@link UnsupportedOperationException}.
    *
    * @throws UnsupportedOperationException
    *    if this operation is not supported, which is the case for this
    *    implementation, so always.
    */
   public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }
}
