/*
 * $Id$
 */
package org.xins.client;

import java.util.Iterator;

import org.xins.common.ExceptionUtils;
import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.collections.PropertyReader;

import org.xins.common.service.CallException;
import org.xins.common.service.TargetDescriptor;

import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.WhislEncoding;

/**
 * XINS-specific call exception.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public abstract class XINSCallException extends CallException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Determines the root cause for the specified exception. If the argument
    * is <code>null</code>, then <code>null</code> is returned.
    *
    * @param t
    *    the exception to determine the root cause for, or <code>null</code>.
    *
    * @return
    *    the root cause of the specified exception, or <code>null</code> if
    *    and only <code>t == null</code>.
    *
    * @since XINS 0.201
    */
   private static final Throwable rootCauseFor(Throwable t) {
      if (t == null) {
         return null;
      } else {
         return ExceptionUtils.getRootCause(t);
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallException</code> based on a short reason, the
    * original request, target called, call duration, detail message and cause
    * exception.
    *
    * @param shortReason
    *    the short reason, cannot be <code>null</code>.
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
    * @param detail
    *    a detailed description of the problem, can be <code>null</code> if
    *    there is no more detail.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>shortReason == null
    *          || request == null
    *          || target == null
    *          || duration &lt; 0</code>.
    */
   XINSCallException(String           shortReason,
                     CallRequest      request,
                     TargetDescriptor target,
                     long             duration,
                     String           detail,
                     Throwable        cause)
   throws IllegalArgumentException {

      // Call superconstructor with fabricated message
      super(createMessage(shortReason, request, target, duration, detail),
            rootCauseFor(cause));

      // Store request and target
      _request  = request;
      _target   = target;
      _duration = duration;
   }

   /**
    * Constructs a new <code>CallException</code> based on a short reason, a
    * call result, detail message and cause exception.
    *
    * @param shortReason
    *    the short reason, cannot be <code>null</code>.
    *
    * @param result
    *    the call result, cannot be <code>null</code>; stores the original
    *    call request, the target descriptor and the call duration.
    *
    * @param detail
    *    a detailed description of the problem, can be <code>null</code> if
    *    there is no more detail.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>shortReason == null
    *          || result == null</code>.
    */
   CallException(String shortReason, Result result, String detail, Throwable cause)
   throws IllegalArgumentException {

      // Call superconstructor with fabricated message
      super(createMessage(shortReason, result, detail),
            rootCauseFor(cause));

      // Store request and target
      _request  = result.getRequest();
      _target   = result.getTarget();
      _duration = result.getDuration();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The original request. Cannot be <code>null</code>.
    */
   private final CallRequest _request;

   /**
    * Descriptor for the target that was attempted to be called. Cannot be
    * <code>null</code>.
    */
   private final TargetDescriptor _target;

   /**
    * The time elapsed between the time the call attempt was started and the
    * time the call returned. The duration is in milliseconds and is always
    * &gt;= 0.
    */
   private final long _duration;

   /**
    * The next linked <code>CallException</code>. Can be <code>null</code> if
    * there is none or if it has not been set yet.
    */
   private CallException _next;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
