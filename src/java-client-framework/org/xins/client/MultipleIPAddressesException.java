/*
 * $Id$
 */
package org.xins.client;

/**
 * Exception that indicates that a host name resolved to multiple IP
 * addresses, while only one IP address is allowed.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.44
 */
public final class MultipleIPAddressesException
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
    * Constructs a new <code>MultipleIPAddressesException</code>.
    */
   public MultipleIPAddressesException() {
      // TODO: Store host name and IP addresses? Create message from that?
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
