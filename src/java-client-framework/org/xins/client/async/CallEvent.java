/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client.async;

import java.util.EventObject;
import org.xins.client.AbstractCAPI;
import org.xins.client.AbstractCAPICallRequest;

/**
 * Event fired and the result of the call is returned.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 *
 * @since XINS 1.4.0
 */
class CallEvent extends EventObject {

   /**
    * Duration of the call.
    */
   private long _duration;

   /**
    * Request of the call.
    */
   private AbstractCAPICallRequest _request;

   /**
    * Creates a new call event.
    *
    * @param capi
    *    the CAPI used to call the function. The CAPI is used as the event source.
    *
    * @param request
    *    the request of the call to the function.
    *
    * @param duration
    *    the duration of the call.
    */
   protected CallEvent(AbstractCAPI capi, AbstractCAPICallRequest request, long duration) {
      super(capi);
      _request = request;
      _duration = duration;
   }

   /**
    * Gets the time it took to call the function.
    *
    * @return
    *    the duration of the call in milliseconds.
    */
   public long getDuration() {
      return _duration;
   }

   /**
    * The request of the call.
    *
    * @return
    *    the request of the call to the function.
    */
   public AbstractCAPICallRequest getRequest() {
      return _request;
   }
}
