/*
 * $Id$
 */
package org.xins.server;

/**
 * Exception that indicates that a session ID is not just unknown, it's
 * invalid.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.66
 */
public final class InvalidSessionIDException
extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The one and only <code>InvalidSessionIDException</code> instance.
    */
   static final InvalidSessionIDException SINGLETON = new InvalidSessionIDException();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>InvalidSessionIDException</code>.
    */
   private InvalidSessionIDException() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
