/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.service.TargetDescriptor;

/**
 * Abstract base class for generated CAPI exceptions that map to an
 * API-specific error code.
 *
 * <p>This class should not be derived from directly. Only generated CAPI
 * classes should derive from this class.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public abstract class AbstractCAPIErrorCodeException
extends UnsuccessfulXINSCallException {

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
    * Constructs a new <code>AbstractCAPIErrorCodeException</code>.
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
    * @throws IllegalArgumentException
    *    if <code>result == null
    *          || result.{@link XINSCallResultData#getErrorCode() getErrorCode()} == null</code>.
    */
   protected AbstractCAPIErrorCodeException(XINSCallRequest    request,
                                            TargetDescriptor   target,
                                            long               duration,
                                            XINSCallResultData resultData)
   throws IllegalArgumentException {
      super(request, target, duration, resultData, null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
