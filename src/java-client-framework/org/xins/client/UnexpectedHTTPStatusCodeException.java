/*
 * $Id$
 */
package org.xins.client;

/**
 * Exception that indicates that an unexpected HTTP status code was received.
 * An HTTP status code in the 2xx range (200-299) is expected.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.196
 */
public final class UnexpectedHTTPStatusCodeException extends CallException {

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
    * Constructs a new <code>UnexpectedHTTPStatusCodeException</code>.
    *
    * @param code
    *    the received HTTP result code.
    */
   UnexpectedHTTPStatusCodeException(int code) {
      // TODO: Create a descriptive message
      super(null, null);

      _code = code;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The returned HTTP result code.
    */
   private final int _code;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the HTTP result code.
    *
    * @return
    *    the HTTP result code.
    */
   public int getCode() {
      return _code;
   }
}
