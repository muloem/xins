/*
 * $Id$
 */
package org.xins.client;

/**
 * Exception thrown to indicate that the result from a XINS API call was
 * invalid.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
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
      super(null, null);
   }

   /**
    * Constructs a new <code>InvalidCallResultException</code> with the
    * specified detail message.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    */
   public InvalidCallResultException(String message) {
      super(message, null);
   }

   /**
    * Constructs a new <code>InvalidCallResultException</code> with the
    * specified cause exception.
    *
    * @param exception
    *    the cause exception, can be <code>null</code>.
    */
   public InvalidCallResultException(Throwable exception) {
      super(null, exception);
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
      super(message, exception);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
