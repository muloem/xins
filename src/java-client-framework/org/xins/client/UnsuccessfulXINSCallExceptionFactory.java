/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;

import org.xins.common.collections.PropertyReader;

import org.xins.common.service.TargetDescriptor;

/**
 * Factory for <code>UnsuccessfulXINSCallException</code> instances. By
 * registering such a factory with a {@link XINSServiceCaller}, it can be
 * configured what {@link UnsuccessfulXINSCallException} subclass is
 * constructed.
 *
 * <p>Although this class is currently package-private, it is expected to be
 * marked as public at some point.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
abstract class UnsuccessfulXINSCallExceptionFactory
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = UnsuccessfulXINSCallExceptionFactory.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>UnsuccessfulXINSCallExceptionFactory</code>.
    */
   protected UnsuccessfulXINSCallExceptionFactory() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>UnsuccessfulXINSCallExceptionFactory</code> (wrapper
    * method).
    *
    * <p>After checking the preconditions, this method delegates to
    * {@link #createImpl(XINSCallRequest,TargetDescriptor,long,XINSCallResultData)}
    * and checks the result from that method. If the result is an
    * {@link UnsuccessfulXINSCallException} instance with the correct details,
    * then it is returned to the caller. Otherwise, if the method throws an
    * exception, returns <code>null</code> or return an
    * {@link UnsuccessfulXINSCallException} instance with incorrect details,
    * then a programming error is logged and a new
    * {@link UnsuccessfulXINSCallException} is created and returned.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    the target on which the request was executed, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration, must be &gt;= <code>0L</code>.
    *
    * @param resultData
    *    the data returned from the call, cannot be <code>null</code> and must
    *    have an error code set.
    *
    * @return
    *    a new {@link UnsuccessfulXINSCallException} instance, never
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request                   ==   null
    *          || target                    ==   null
    *          || duration                  &lt; 0
    *          || resultData                ==   null
    *          || resultData.getErrorCode() ==   null</code>.
    */
   public final UnsuccessfulXINSCallException
   create(XINSCallRequest    request,
          TargetDescriptor   target,
          long               duration,
          XINSCallResultData resultData)
   throws IllegalArgumentException {

      final String THIS_METHOD_SIG = "("
                                   + XINSCallRequest.class.getName()
                                   + ','
                                   + TargetDescriptor.class.getName()
                                   + ",long,"
                                   + XINSCallResultData.class.getName()
                                   + ')';
      final String THIS_METHOD = "create" + THIS_METHOD_SIG;

      // Check arguments
      MandatoryArgumentChecker.check("request",    request,
                                     "target",     target,
                                     "resultData", resultData);
      if (duration < 0L) {
         throw new IllegalArgumentException("duration (" + duration + "L) < 0L");
      } else if (resultData.getErrorCode() == null) {
         throw new IllegalArgumentException("resultData.getErrorCode() == null");
      }

      final String SUBJECT_CLASS  = getClass().getName();
      final String SUBJECT_METHOD = "createImpl" + THIS_METHOD_SIG;

      // Delegate to the implementation method
      UnsuccessfulXINSCallException e;
      try {
         e = createImpl(request, target, duration, resultData);
         if (e == null) {
            final String DETAIL = "Method returned null.";
            Utils.logProgrammingError(CLASSNAME,     THIS_METHOD,
                                      SUBJECT_CLASS, SUBJECT_METHOD,
                                      DETAIL);
         }
      } catch (Throwable t) {
         Utils.logProgrammingError(CLASSNAME,     THIS_METHOD,
                                   SUBJECT_CLASS, SUBJECT_METHOD,
                                   null,          t);
         e = null;
      }

      // TODO: Make ResultData.getParameters() never return null

      // Check that the UnsuccessfulXINSCallException details are acceptable
      if (e != null) {
         if (! request.equals(e.getRequest())
         || (! target.equals(e.getTarget()))
         || (duration != e.getDuration())
         || (! resultData.getErrorCode().equals(e.getErrorCode()))
         || ((resultData.getParameters()  == null) && e.getParameters() != null)
         || (resultData.getParameters()   != null  && (! resultData.getParameters().equals(e.getParameters())))
         || ((resultData.getDataElement() == null) && e.getDataElement() != null)
         || ((resultData.getDataElement() != null) && (!  resultData.getDataElement().equals(e.getDataElement())))) {
            final String DETAIL = "Returned result does not match input.";
            Utils.logProgrammingError(CLASSNAME,     THIS_METHOD,
                                      SUBJECT_CLASS, SUBJECT_METHOD,
                                      DETAIL);
            e = null;
         }
      }

      // If there is no acceptable UnsuccessfulXINSCallException yet, then
      // create one now
      if (e == null) {
         e = new UnsuccessfulXINSCallException(request, target, duration,
                                               resultData, null);
      }

      return e;
   }

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
   protected abstract UnsuccessfulXINSCallException
   createImpl(XINSCallRequest    request,
              TargetDescriptor   target,
              long               duration,
              XINSCallResultData resultData);
}
