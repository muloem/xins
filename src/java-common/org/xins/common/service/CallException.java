/*
 * $Id$
 */
package org.xins.common.service;

import org.xins.common.ExceptionUtils;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.FastStringBuffer;

/**
 * Exception thrown to indicate that a <code>ServiceCaller</code> call failed.
 * This exception is typically only called from {@link ServiceCaller} and
 * subclasses.
 *
 * <p>When a cause exception is passed to any of the constructors, then the
 * root cause of that exception is passed up to the {@link Exception} class.
 * The root cause of an exception can be determined using
 * {@link ExceptionUtils#getRootCause(Throwable)}.
 *
 * <p>Call exceptions are linked. The first thrown exception is normally
 * returned. The next exception can then be retrieved using
 * {@link #getNext()}.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public abstract class CallException extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates an exception message for the constructor based on a short
    * reason, the original request, target called, call duration and detail
    * message.
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
    * @return
    *    the exception message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>shortReason == null
    *          || request == null
    *          || target == null
    *          || duration &lt; 0</code>.
    */
   private static final String createMessage(String           shortReason,
                                             CallRequest      request,
                                             TargetDescriptor target,
                                             long             duration,
                                             String           detail)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("shortReason", shortReason,
                                     "request",     request,
                                     "target",      target);
      if (duration < 0) {
         throw new IllegalArgumentException("duration (" + duration + ") < 0");
      }

      FastStringBuffer buffer = new FastStringBuffer(295, shortReason);
      buffer.append(" in ");
      buffer.append(duration);
      buffer.append(" ms while executing ");
      buffer.append(request.describe());

      buffer.append(" at ");
      buffer.append(target.getURL());

      buffer.append(" with connection time-out ");
      int connectionTimeOut = target.getConnectionTimeOut();
      if (connectionTimeOut < 1) {
         buffer.append("disabled, with socket time-out ");
      } else {
         buffer.append(connectionTimeOut);
         buffer.append(" ms, with socket time-out ");
      }

      int socketTimeOut = target.getSocketTimeOut();
      if (socketTimeOut < 1) {
         buffer.append("disabled and with total time-out ");
      } else {
         buffer.append(socketTimeOut);
         buffer.append(" ms and with total time-out ");
      }

      int totalTimeOut = target.getTotalTimeOut();
      if (totalTimeOut < 1) {
         buffer.append("disabled");
      } else {
         buffer.append(totalTimeOut);
         buffer.append(" ms");
      }

      if (detail == null) {
         buffer.append('.');
      } else {
         buffer.append(": ");
         buffer.append(detail);
      }

      return buffer.toString();
   }

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
   CallException(String           shortReason,
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

   /**
    * Returns the original request.
    *
    * @return
    *    the original request, never <code>null</code>.
    */
   public final CallRequest getRequest() {
      return _request;
   }

   /**
    * Returns the descriptor for the target that was attempted to be called.
    *
    * @return
    *    the target descriptor, cannot be <code>null</code>.
    */
   public final TargetDescriptor getTarget() {
      return _target;
   }

   /**
    * Returns the call duration. This is defined as the time elapsed between
    * the time the call attempt was started and the time the call returned.
    * The duration is in milliseconds and is always &gt;= 0.
    *
    * @return
    *    the call duration in milliseconds, always &gt;= 0.
    */
   public final long getDuration() {
      return _duration;
   }

   /**
    * Sets the next linked <code>CallException</code>. This method should be
    * called either never or once during the lifetime of a
    * <code>CallException</code> object.
    *
    * @param next
    *    the next linked <code>CallException</code>, not <code>null</code>.
    *
    * @throws IllegalStateException
    *    if the next linked <code>CallException</code> has already been set.
    *
    * @throws IllegalArgumentException
    *    if <code>next == null</code>.
    */
   final void setNext(CallException next)
   throws IllegalStateException, IllegalArgumentException {

      // Check preconditions
      if (_next != null) {
         throw new IllegalStateException("Next linked CallException already set.");
      }
      MandatoryArgumentChecker.check("next", next);

      _next = next;
   }

   /**
    * Gets the next linked <code>CallException</code>, if there is any.
    *
    * @return
    *    the next linked <code>CallException</code>, or <code>null</code> if
    *    there is none.
    */
   public final CallException getNext() {
      return _next;
   }
}
