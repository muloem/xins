/*
 * $Id$
 */
package org.xins.server;

/**
 * Exception that indicates that a response is considered invalid.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.50
 */
public final class InvalidResponseException
extends Exception {

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
    * Constructs a new <code>InvalidResponseException</code> with the
    * specified detail message.
    */
   public InvalidResponseException(String message) {
      super(message);
   }

   /**
    * Constructs a new <code>InvalidResponseException</code> with no detail
    * message.
    */
   public InvalidResponseException() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
