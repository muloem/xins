/*
 * $Id$
 */
package org.xins.server;

/**
 * Exception that indicates that there is no function matching the request.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.149
 */
public final class NoSuchFunctionException
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
    * Constructs a new <code>NoSuchFunctionException</code> for the specified
    * function name.
    *
    * @param functionName
    *    the name, or <code>null</code>.
    */
   NoSuchFunctionException(String functionName) {
      // TODO: super(createMessage(functionName));
      super(functionName);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
