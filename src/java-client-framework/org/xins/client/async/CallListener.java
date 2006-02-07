/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client.async;

import java.util.EventListener;

/**
 * Event fired and the result of the call is returned.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.4.0
 */
public interface CallListener extends EventListener {

   /**
    * Invoked when a successful result has been returned by the function.
    *
    * @param event
    *    the call event that has the result of the call.
    */
   void callSucceeded(CallSucceededEvent event);
   
   /**
    * Invoked when the call to the function failed.
    *
    * @param event
    *    the call event that has the details of the failure.
    */
   void callFailed(CallFailedEvent event);
}
