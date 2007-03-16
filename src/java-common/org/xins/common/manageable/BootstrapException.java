/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.manageable;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.logdoc.ExceptionUtils;

/**
 * Exception thrown when the bootstrapping of a <code>Manageable</code>
 * object failed.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 *
 * @see Manageable#bootstrap(org.xins.common.collections.PropertyReader)
 */
public final class BootstrapException extends Exception {

   /**
    * Constructs a new <code>BootstrapException</code> with the specified
    * message.
    *
    * @param message
    *    the detail message, or <code>null</code>.
    */
   public BootstrapException(String message) {
      super(message);
   }

   /**
    * Constructs a new <code>BootstrapException</code> with the specified
    * cause exception.
    *
    * @param cause
    *    the cause exception, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>cause == null</code>.
    */
   public BootstrapException(Throwable cause)
   throws IllegalArgumentException {
      super(createMessage(cause));
      ExceptionUtils.setCause(this, cause);
   }

   /**
    * Creates a message based on the specified constructor argument.
    *
    * @param cause
    *    the cause exception, cannot be <code>null</code>.
    *
    * @return
    *    the message, never <code>null</code>.
    */
   private static String createMessage(Throwable cause)
   throws IllegalArgumentException {

      String exceptionMessage = cause.getMessage();

      String message = "Caught " + cause.getClass().getName();
      if (exceptionMessage != null && exceptionMessage.length() > 0) {
         message += ". Message: \"" + exceptionMessage + "\".";
      } else {
         message += '.';
      }

      return message;
   }
}
