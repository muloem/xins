/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.service.TargetDescriptor;

/**
 * Basic implementation of a factory for
 * <code>UnsuccessfulXINSCallException</code> instances.
 *
 * <p>Although this class is currently package-private, it is expected to be
 * marked as public at some point.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
final class BasicUnsuccessfulXINSCallExceptionFactory
extends UnsuccessfulXINSCallExceptionFactory {

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
    * Constructs a new <code>BasicUnsuccessfulXINSCallExceptionFactory</code>.
    */
   public BasicUnsuccessfulXINSCallExceptionFactory() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>UnsuccessfulXINSCallExceptionFactory</code>
    * (implementation method).
    *
    * <p>This method should only be called from
    * {@link #create(XINSCallRequest,TargetDescriptor,long,XINSCallResultData)}.
    *
    * @param request
    *    the original request, guaranteed not to be <code>null</code>.
    *
    * @param target
    *    the target on which the request was executed, guaranteed not to be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration, guaranteed to be &gt;= <code>0L</code>.
    *
    * @param resultData
    *    the data returned from the call, guaranteed to be <code>null</code>
    *    and must have an error code set.
    *
    * @return
    *    a new {@link UnsuccessfulXINSCallException} instance, should not be
    *    <code>null</code>.
    */
   protected UnsuccessfulXINSCallException createImpl(XINSCallRequest    request,
                                                      TargetDescriptor   target,
                                                      long               duration,
                                                      XINSCallResultData resultData) {

      // Determine error code
      String errorCode = resultData.getErrorCode();

      // Invalid request
      if ("_InvalidRequest".equals(errorCode)
       || "MissingParameters".equals(errorCode)
       || "InvalidParameters".equals(errorCode)) {
         return new InvalidRequestException(request, target, duration, resultData);

      // Otherwise just return a plain UnsuccessfulXINSCallException
      } else {
         return new UnsuccessfulXINSCallException(request, target, duration, resultData, null);
      }
   }
}
