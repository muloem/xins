/*
 * $Id$
 */
package org.xins.client;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Exception that indicates that an API call result was unsuccessful.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.36
 */
public final class UnsuccessfulCallException
extends Exception {

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
    * Constructs a new <code>UnsuccessfulCallException</code> with the
    * specified call result.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null || result.isSuccess()</code>.
    */
   public UnsuccessfulCallException(CallResult result)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("result", result);
      if (result.isSuccess()) {
         throw new IllegalArgumentException("result.isSuccess() == true");
      }

      _result = result;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The call result. The value of this field cannot be <code>null</code>.
    */
   private final CallResult _result;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the call result.
    *
    * @return
    *    the call result, cannot be <code>null</code>.
    */
   public CallResult getCallResult() {
      return _result;
   }
}
