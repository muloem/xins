/*
 * $Id$
 */
package org.xins.common.http;

import org.xins.common.service.CallException;
import org.xins.common.service.TargetDescriptor;

/**
 * HTTP-specific call exception.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public abstract class HTTPCallException extends CallException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = HTTPCallException.class.getName();


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
    * Constructs a new <code>HTTPCallException</code> based on a short
    * reason, the original request, target called, call duration, detail
    * message and cause exception.
    *
    * @param shortReason
    *    the short reason, cannot be <code>null</code>.
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
    * @param detail
    *    a detailed description of the problem, can be <code>null</code> if
    *    there is no more detail.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>shortReason == null
    *          || request == null
    *          || target == null
    *          || duration &lt; 0</code>.
    */
   HTTPCallException(String           shortReason,
                     HTTPCallRequest  request,
                     TargetDescriptor target,
                     long             duration,
                     String           detail,
                     Throwable        cause)
   throws IllegalArgumentException {
      super(trace(shortReason), request, target, duration, detail, cause);

      // TRACE: Leave constructor
      Log.log_3002(CLASSNAME, null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
