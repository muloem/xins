/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.service.TargetDescriptor;

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
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the duration in milliseconds, must be &gt;= 0.
    *
    * @param code
    *    the received HTTP status code.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0</code>.
    *
    * @since XINS 0.202
    */
   UnexpectedHTTPStatusCodeException(CallRequest      request,
                                     TargetDescriptor target,
                                     long             duration,
                                     int              code)
   throws IllegalArgumentException {
      super("Unexected HTTP status code", request, target, duration,
            "HTTP status code " + code + '.',
            null);

      _code = code;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The returned HTTP status code.
    */
   private final int _code;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the HTTP status code.
    *
    * @return
    *    the HTTP status code.
    */
   public int getCode() {
      return _code;
   }
}
