/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import java.io.IOException;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception that indicates that an I/O error interrupted a service call.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class IOCallException
extends GenericCallException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = IOCallException.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Checks the arguments for the constructor and then returns the short
    * reason.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param ioException
    *    the cause {@link IOException}, cannot be <code>null</code>.
    *
    * @return
    *    the short reason, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || ioException == null</code>
    */
   private static String getShortReason(CallRequest      request,
                                        TargetDescriptor target,
                                        IOException      ioException)
   throws IllegalArgumentException {

      // TRACE: Enter constructor
      Log.log_1000(CLASSNAME, null);

      // Check preconditions
      MandatoryArgumentChecker.check("request",     request,
                                     "target",      target,
                                     "ioException", ioException);

      // Return the short reason
      return "I/O error";
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>IOCallException</code>.
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
    * @param ioException
    *    the cause {@link IOException}, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || ioException == null
    *          || duration  &lt; 0</code>.
    */
   public IOCallException(CallRequest      request,
                          TargetDescriptor target,
                          long             duration,
                          IOException      ioException)
   throws IllegalArgumentException {

      // Trace, check arguments and then call constructor of superclass
      super(getShortReason(request, target, ioException),
            request,
            target,
            duration,
            null,
            ioException);

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
