/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.manageable;

import org.xins.common.text.FastStringBuffer;

/**
 * Exception thrown when the initialization of a <code>Manageable</code>
 * object failed.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
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
    * @param message
    *    the message passed to the constructor, or <code>null</code>.
    *
    * @param cause
    *    the cause exception, or <code>null</code>.
    *
    * @return
    *    the message, never <code>null</code>.
    */
   private static final String createMessage(String message, Throwable cause) {

      // TODO: Improve this method.

      if (message != null) {
         return message;
      }

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
      this(message, null);
   }

   /**
    * Constructs a new <code>InitializationException</code> with the specified
    * cause exception.
    *
    * @param cause
    *    the cause exception, or <code>null</code>.
    */
   public InitializationException(Throwable cause) {
      this(null, cause);
   }

   /**
    * Constructs a new <code>InitializationException</code> with the specified
    * detail message and cause exception.
    *
    * @param message
    *    the detail message, or <code>null</code>.
    *
    * @param cause
    *    the cause exception, or <code>null</code>.
    */
   public InitializationException(String message, Throwable cause) {
      super(createMessage(message, cause), cause);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
