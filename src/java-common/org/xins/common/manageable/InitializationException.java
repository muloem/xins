/*
 * $Id$
 */
package org.xins.common.manageable;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.FastStringBuffer;

/**
 * Exception thrown when the initialization of a <code>Manageable</code>
 * object failed.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class InitializationException
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
    * @param cause
    *    the cause exception, cannot be <code>null</code>.
    *
    * @return
    *    the message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>cause == null</code>.
    */
   private static final String createMessage(Throwable cause)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("cause", cause);

      String exceptionMessage = cause.getMessage();

      FastStringBuffer buffer = new FastStringBuffer(150);
      buffer.append("Caught ");
      buffer.append(cause.getClass().getName());
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
    * Constructs a new <code>InitializationException</code> with the specified
    * message.
    *
    * @param message
    *    the detail message, or <code>null</code>.
    */
   public InitializationException(String message) {
      super(message);
   }

   /**
    * Constructs a new <code>InitializationException</code> with the specified
    * detail message and cause exception.
    *
    * @param cause
    *    the cause exception, or <code>null</code>.
    */
   public InitializationException(Throwable cause) {
      super(createMessage(cause), cause);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
