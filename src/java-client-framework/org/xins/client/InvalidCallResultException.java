/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.ExceptionUtils;

/**
 * Exception thrown to indicate that the result from a XINS API call was
 * invalid according to the XINS standard.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class InvalidCallResultException
extends CallException {

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
    * Constructs a new <code>InvalidCallResultException</code> with no detail
    * message.
    */
   public InvalidCallResultException() {
      this(null, null);
   }

   /**
    * Constructs a new <code>InvalidCallResultException</code> with the
    * specified detail message.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    */
   public InvalidCallResultException(String message) {
      this(message, null);
   }

   /**
    * Constructs a new <code>InvalidCallResultException</code> with the
    * specified cause exception.
    *
    * @param exception
    *    the cause exception, can be <code>null</code>.
    */
   public InvalidCallResultException(Throwable exception) {
      this(null, exception);
   }

   /**
    * Constructs a new <code>InvalidCallResultException</code> with the
    * specified detail message and cause exception.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    *
    * @param exception
    *    the cause exception, can be <code>null</code>.
    */
   public InvalidCallResultException(String message, Throwable exception) {

      super((message != null || exception == null) ? message
                                                   : ExceptionUtils.getRootCause(exception).getMessage(),
            (exception == null) ? null
                                : ExceptionUtils.getRootCause(exception));
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
