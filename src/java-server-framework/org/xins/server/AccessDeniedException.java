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
 * @since XINS 0.153
 */
public final class AccessDeniedException
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
    * Constructs a new <code>AccessDeniedException</code> for the specified
    * IP address and function name.
    *
    * @param ip
    *    the IP address, or <code>null</code>.
    *
    * @param functionName
    *    the name of the function, or <code>null</code>.
    */
   AccessDeniedException(String ip, String functionName) {
      // TODO: super(createMessage(ip, functionName));
      super(ip + " / " + functionName);
      // TODO: store IP
      // TODO: store functionName
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
