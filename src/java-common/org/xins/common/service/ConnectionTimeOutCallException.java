/*
 * $Id$
 */
package org.xins.common.service;

import org.xins.common.Log;

/**
 * Exception that indicates that a connection to a service could not be
 * established due to a connection time-out.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public final class ConnectionTimeOutCallException
extends ConnectionCallException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = ConnectionTimeOutCallException.class.getName();


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
    * Constructs a new <code>ConnectionTimeOutCallException</code>.
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
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0</code>.
    *
    */
   public ConnectionTimeOutCallException(CallRequest      request,
                                         TargetDescriptor target,
                                         long             duration)
   throws IllegalArgumentException {

      // Trace and then call constructor of superclass
      super(trace("Connection time-out"),
            request, target, duration, null, null);

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
