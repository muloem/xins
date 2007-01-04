/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.manageable;

import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.TextUtils;

import org.xins.logdoc.ExceptionUtils;

/**
 * Exception thrown when the initialization of a <code>Manageable</code>
 * object failed.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 *
 * @see Manageable#init(org.xins.common.collections.PropertyReader)
 */
public final class InitializationException
extends Exception {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates a message based on the specified constructor argument.
    *
    * @param detail
    *    the detail message passed to the constructor, or <code>null</code>.
    *
    * @param cause
    *    the cause exception, or <code>null</code>.
    *
    * @return
    *    the message, never <code>null</code>.
    */
   private static String createMessage(String detail, Throwable cause) {

      FastStringBuffer buffer = new FastStringBuffer(159);
      buffer.append("Initialization failed");

      if (detail != null) {
         buffer.append(": \"");
         buffer.append(detail);
         buffer.append('"');
      }

      if (cause != null) {
         buffer.append(". Caught ");
         buffer.append(cause.getClass().getName());

         String causeMessage = TextUtils.trim(cause.getMessage(), null);
         if (causeMessage != null) {
            buffer.append(" with message \"");
            buffer.append(causeMessage);
            buffer.append('"');
         }
      }
      buffer.append('.');

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>InitializationException</code> with the specified
    * detail message.
    *
    * @param detail
    *    the detail message, or <code>null</code>.
    */
   public InitializationException(String detail) {
      this(detail, null);
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
    * @param detail
    *    the detail message, or <code>null</code>.
    *
    * @param cause
    *    the cause exception, or <code>null</code>.
    */
   public InitializationException(String detail, Throwable cause) {
      super(createMessage(detail, cause));
      if (cause != null) {
         ExceptionUtils.setCause(this, cause);
      }
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
