/*
 * $Id$
 */
package org.xins.common.manageable;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.FastStringBuffer;

/**
 * Exception thrown when the deinitialization of a <code>Manageable</code>
 * object caused an exception to be thrown.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class DeinitializationException
extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates a message based on the specified constructor argument.
    *
    * @param exception
    *    the cause exception, cannot be <code>null</code>.
    *
    * @return
    *    the message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>exception == null</code>.
    */
   private static final String createMessage(Throwable exception)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("exception", exception);

      String exceptionMessage = exception.getMessage();

      FastStringBuffer buffer = new FastStringBuffer(150);
      buffer.append("Caught ");
      buffer.append(exception.getClass().getName());
      if (exceptionMessage != null && exceptionMessage.length() > 0) {
         buffer.append(". Message: \"");
         buffer.append(exceptionMessage);
         buffer.append("\".");
      } else {
         buffer.append('.');
      }

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>DeinitializationException</code> with the
    * specified cause exception.
    *
    * @param exception
    *    the cause exception, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>exception == null</code>.
    */
   DeinitializationException(Throwable exception)
   throws IllegalArgumentException {
      super(createMessage(exception));
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
    * Returns the cause exception, if any.
    *
    * @return
    *    the wrapped cause exception, can be <code>null</code>.
    */
   public final Throwable getException() {
      return _exception;
   }
}
