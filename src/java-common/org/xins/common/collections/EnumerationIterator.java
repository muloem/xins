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

   public boolean hasNext() {
      return _enumeration.hasMoreElements();
   }

   public Object next() throws NoSuchElementException {
      return _enumeration.nextElement();
   }

   public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }
}
