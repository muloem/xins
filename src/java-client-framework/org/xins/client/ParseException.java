/*
 * $Id$
 */
package org.xins.client;

/**
 * Exception thrown to indicate parsing of XML failed. Either the string is
 * not a correct XML document, or the structure of the (valid) XML document is
 * invalid.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class ParseException extends Exception {

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
    * Constructs a new <code>ParseException</code> with no detail message.
    */
   public ParseException() {
      _exception = null;
   }

   /**
    * Constructs a new <code>ParseException</code> with the specified detail
    * message.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    */
   public ParseException(String message) {
      super(message);
      _exception = null;
   }

   /**
    * Constructs a new <code>ParseException</code> with the specified cause
    * exception.
    *
    * @param exception
    *    the cause exception, can be <code>null</code>.
    */
   public ParseException(Throwable exception) {
      _exception = exception;
   }

   /**
    * Constructs a new <code>ParseException</code> with the specified detail
    * message and cause exception.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    *
    * @param exception
    *    the cause exception, can be <code>null</code>.
    */
   public ParseException(String message, Throwable exception) {
      super(message);
      _exception = exception;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The wrapped cause exception. Can be <code>null</code>.
    */
   private final Throwable _exception;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the wrapped cause exception, if any.
    *
    * @return
    *    the wrapped cause exception, or <code>null</code>.
    */
   public Throwable getCauseException() {
      return _exception;
   }
}
