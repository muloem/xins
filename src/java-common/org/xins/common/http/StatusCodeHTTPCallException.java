/*
 * $Id$
 */
package org.xins.common.http;

import org.xins.common.Log;

import org.xins.common.service.TargetDescriptor;

/**
 * Exception that indicates that an HTTP call failed because the returned HTTP
 * status code was considered invalid.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public final class StatusCodeHTTPCallException
extends HTTPCallException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = StatusCodeHTTPCallException.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Logs the fact that the constructor was entered. The short reason passed
    * to the constructor is both the input and the output for this class
    * function.
    *
    * @param shortReason
    *    the short reason, could be <code>null</code>.
    *
    * @return
    *    <code>shortReason</code>.
    */
   private static final String trace(String shortReason) {

      // TRACE: Enter constructor
      Log.log_3000(CLASSNAME, null);

      return shortReason;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>StatusCodeHTTPCallException</code> based on the
    * original request, target called, call duration and HTTP status code.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration in milliseconds, must be &gt;= 0.
    *
    * @param code
    *    the HTTP status code.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null
    *          || target == null
    *          || duration &lt; 0</code>.
    */
   StatusCodeHTTPCallException(HTTPCallRequest  request,
                               TargetDescriptor target,
                               long             duration,
                               int              code)
   throws IllegalArgumentException {
      super(trace("Unsupported HTTP status code " + code),
            request, target, duration, null, null);

      _code = code;

      // TRACE: Leave constructor
      Log.log_3002(CLASSNAME, null);
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
    *    the HTTP status code that is considered unacceptable.
    */
   public int getStatusCode() {
      return _code;
   }
}
