/*
 * $Id$
 */
package org.xins.client;

/**
 * Exception thrown to indicate that a call to a XINS API failed.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.115
 */
public abstract class CallException extends Exception {

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
    * Constructs a new <code>CallException</code> with the
    * specified detail message and cause exception.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    *
    * @param exception
    *    the cause exception, can be <code>null</code>.
    */
   protected CallException(String message, Throwable exception) {
      super(message);
      _exception = exception;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The wrapped exception.
    */
   private final Throwable _exception;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the wrapped exception, if any.
    *
    * @return
    *    the wrapped exception, can be <code>null</code>.
    */
   public final Throwable getException() {
      return _exception;
   }
}
