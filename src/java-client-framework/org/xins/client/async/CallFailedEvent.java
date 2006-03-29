/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client.async;

import org.xins.client.AbstractCAPI;
import org.xins.client.AbstractCAPICallRequest;

/**
 * Event fired the call to the function failed.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.4.0
 */
public final class CallFailedEvent extends CallEvent {

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a failed call event.
    *
    * @param capi
    *    the CAPI used to call the function. The CAPI is used as the event source.
    *
    * @param request
    *    the request of the call to the function.
    *
    * @param duration
    *    the duration of the call.
    *
    * @param exception
    *    the exception thrown by the CAPI call.
    */
   public CallFailedEvent(AbstractCAPI capi, AbstractCAPICallRequest request,
                          long duration, Exception exception) {
      super(capi, request, duration);
      _exception = exception;
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The exception thrown by the call.
    */
   private Exception _exception;

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the exception thrown by the CAPI call.
    *
    * @return
    *    the exception, most probably a sub class of the
    *    {@link org.xins.common.service.CallException CallException}.
    */
   public Exception getException() {
      return _exception;
   }
}
