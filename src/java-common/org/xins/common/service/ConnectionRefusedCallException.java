/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import org.xins.common.Log;

/**
 * Exception that indicates that a connection to a service could not be
 * established since it was refused.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class ConnectionRefusedCallException
extends ConnectionCallException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = ConnectionRefusedCallException.class.getName();


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
      Log.log_1000(CLASSNAME, null);

      return shortReason;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ConnectionRefusedCallException</code>.
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
   public ConnectionRefusedCallException(CallRequest      request,
                                         TargetDescriptor target,
                                         long             duration)
   throws IllegalArgumentException {

      // Trace and then call constructor of superclass
      super(trace("Connection refused"),
            request, target, duration, null, null);

      // TRACE: Leave constructor
      Log.log_1002(CLASSNAME, null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
