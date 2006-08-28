/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.manageable;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.text.FastStringBuffer;

import org.xins.logdoc.ExceptionUtils;

/**
 * Exception thrown when the bootstrapping of a <code>Manageable</code>
 * object failed.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 *
 * @see Manageable#bootstrap(org.xins.common.collections.PropertyReader)
 */
public final class BootstrapException
extends Exception {

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
   private static String createMessage(Throwable cause)
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


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
