/*
 * $Id$
 */
package org.xins.common.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Result of a call to a service. The actual result is returned, combined with
 * links to the services that failed and a link to the service to which the
 * call succeeded.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.115
 */
public abstract class CallResult extends Object {

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
    * Constructs a new <code>CallResult</code> object.
    *
    * @param request
    *    the call request that resulted in this result, cannot be
    *    <code>null</code>.
    *
    * @param succeededTarget
    *    the target for which the call succeeded, cannot be <code>null</code>.
    *
    * @param duration
    *    the call duration in milliseconds, cannot be a negative number.
    *
    * @param exceptions
    *    the list of {@link CallException}s, or <code>null</code> if the first
    *    call attempt succeeded.
    *
    * @throws IllegalArgumentException
    *    if <code>request         ==   null
    *          || succeededTarget ==   null
    *          || duration        &lt; 0L</code>.
    */
   protected CallResult(CallRequest       request,
                        TargetDescriptor  succeededTarget,
                        long              duration,
                        CallExceptionList exceptions)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("request",         request,
                                     "succeededTarget", succeededTarget);
      if (duration < 0L) {
         throw new IllegalArgumentException("duration (" + duration + "L) < 0L");
      }

      // Set fields
      _request         = request;
      _succeededTarget = succeededTarget;
      _duration        = duration;
      _exceptions      = exceptions;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The call request. This field cannot be <code>null</code>.
    */
   private final CallRequest _request;

   /**
    * The target for which the call succeeded. This field cannot be
    * <code>null</code>.
    */
   private final TargetDescriptor _succeededTarget;

   /**
    * The call duration, in milliseconds.
    */
   private final long _duration;

   /**
    * The list of <code>CallException</code>s. This field may be
    * <code>null</code>.
    */
   private final CallExceptionList _exceptions;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the call request.
    *
    * @return
    *    the {@link CallRequest}, never <code>null</code>.
    */
   public final CallRequest getRequest() {
      return _request;
   }

   /**
    * Returns the target for which the call succeeded.
    *
    * @return
    *    the {@link TargetDescriptor} for which the call succeeded, not
    *    <code>null</code>.
    */
   public final TargetDescriptor getSucceededTarget() {
      return _succeededTarget;
   }

   /**
    * Returns the call duration, in milliseconds.
    *
    * @return
    *    the duration of the succeeded calls, in milliseconds, guaranteed to
    *    be a non-negative number.
    */
   public final long getDuration() {
      return _duration;
   }

   /**
    * Returns the list of <code>CallException</code>s.
    *
    * @return
    *    the {@link CallException}s, collected in a {@link CallExceptionList}
    *    object, or <code>null</code> if the first call attempt succeeded.
    */
   public final CallExceptionList getExceptions() {
      return _exceptions;
   }
}
