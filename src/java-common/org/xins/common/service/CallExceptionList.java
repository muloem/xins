/*
 * $Id$
 */
package org.xins.common.service;

import java.util.ArrayList;
import java.util.List;

import org.xins.common.MandatoryArgumentChecker;

/**
 * List of call exceptions. See class {@link CallException}.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public final class CallExceptionList extends Object {

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
    * Constructs a new <code>CallExceptionList</code> object.
    */
   public CallExceptionList() {
      _exceptions = new ArrayList();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The underlying collection to store the <code>CallException</code> objects
    * in.
    */
   private final List _exceptions;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Adds a <code>CallException</code>.
    *
    * @param exception
    *    the {@link CallException} to add, cannot be <code>null</code>.
    */
   void add(CallException exception) {
      _exceptions.add(exception);
   }

   /**
    * Counts the number of elements.
    *
    * @return
    *    the number of {@link CallException}s, always &gt;= 0.
    */
   public int size() {
      return _exceptions.size();
   }

   /**
    * Retrieves an element by index.
    *
    * @param index
    *    the element index, must be &gt;= 0 and &lt; {@link #size()}.
    *
    * @return
    *    the {@link CallException} element at the specified index, never
    *    <code>null</code>.
    *
    * @throws ArrayIndexOutOfBoundsException
    *    if <code>index &lt; 0 || index &gt;= {@link #size()}</code>.
    */
   public CallException get(int index)
   throws ArrayIndexOutOfBoundsException {
      return (CallException) _exceptions.get(index);
   }
}
