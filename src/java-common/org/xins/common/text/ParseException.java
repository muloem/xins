/*
 * $Id$
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
    * Constructs a new <code>ParseException</code> with no detail message.
    */
   public ParseException() {
      // empty
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
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
