/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.text;

/**
 * Exception thrown to indicate parsing.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
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
    * Constructs a new <code>ParseException</code> with no detail message and
    * no cause exception.
    */
   public ParseException() {
      this(null, null);
   }

   /**
    * Constructs a new <code>ParseException</code> with the specified detail
    * message and no cause exception.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    */
   public ParseException(String message) {
      this(message, null);
   }

   /**
    * Constructs a new <code>ParseException</code> with the specified detail
    * message and cause exception.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    */
   public ParseException(String message, Throwable cause) {
      super(message, cause);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
