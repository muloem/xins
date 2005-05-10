/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.service.CallException;
import org.xins.common.service.CallExceptionList;
import org.xins.common.service.TargetDescriptor;

/**
 * Base class for generated CAPI function result classes.
 *
 * <p>This class should not be subclassed manually. It is only intended to be
 * subclassed by classes generated by XINS.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public abstract class AbstractCAPICallResult
extends Object {

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
    * Creates a new <code>AbstractCAPICallResult</code> object, based on the
    * specified <code>XINSCallResult</code>.
    *
    * @param result
    *    the lower-level {@link XINSCallResult}, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null</code>.
    */
   protected AbstractCAPICallResult(XINSCallResult result)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("result", result);

      _result = result;

      // Check preconditions
      if (result.getErrorCode() != null) {
         throw new java.lang.IllegalArgumentException("result.getErrorCode() != null");
      }

   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The XINS call result. This field cannot be <code>null</code>.
    */
   private XINSCallResult _result;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the underlying XINS call result.
    *
    * @return
    *    the underlying {@link XINSCallResult} object, never
    *    <code>null</code>.
    */
   XINSCallResult getXINSCallResult() {
      return _result;
   }

   /**
    * Returns the target for which the call succeeded.
    *
    * @return
    *    the {@link TargetDescriptor} for which the call succeeded, not
    *    <code>null</code>.
    *
    * @since XINS 1.1.0
    */
   public final TargetDescriptor succeededTarget() {
      return _result.getSucceededTarget();
   }

   /**
    * Returns the call duration, in milliseconds.
    *
    * @return
    *    the duration of the succeeded call, in milliseconds, guaranteed to
    *    be a non-negative number.
    *
    * @since XINS 1.1.0
    */
   public final long duration() {
      // TODO: Duration of succeeded call or of the complete attempt?
      return _result.getDuration();
   }

   /**
    * Returns the list of <code>CallException</code>s.
    *
    * @return
    *    the {@link CallException}s, collected in a {@link CallExceptionList}
    *    object, or <code>null</code> if the first call attempt succeeded.
    *
    * @since XINS 1.1.0
    */
   public final CallExceptionList exceptions() {
      return _result.getExceptions();
   }
}
