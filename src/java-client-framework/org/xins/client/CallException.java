/*
 * $Id$
 */
package org.xins.client;

import java.util.Iterator;

import org.xins.common.ExceptionUtils;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.WhislEncoding;

/**
 * Exception thrown to indicate that a call to a XINS API failed.
 *
 * <p>When a cause exception is passed to any of the constructors, then the
 * root cause of that exception is passed up to the {@link Exception} class.
 * The root cause of an exception can be determined using
 * {@link ExceptionUtils#getRootCause(Throwable)}.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.115
 */
public abstract class CallException extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates an exception message based on a <code>CallRequest</code> and a
    * reason.
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
    *    the duration in milliseconds, must be &gt;= 0.
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
    *
    * @since XINS 0.202
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

      FastStringBuffer buffer = new FastStringBuffer(293, shortReason);
      buffer.append(" in ");
      buffer.append(duration);
      buffer.append(" ms while calling function ");
      buffer.append(request.getFunctionName());

      PropertyReader parameters = request.getParameters();
      if (parameters == null || parameters.size() < 1) {
         buffer.append(" with no parameters");
      } else {
         buffer.append(" with parameters ");
         Iterator names = parameters.getNames();
         boolean first = true;
         while (names.hasNext()) {
            String key = (String) names.next();
            String val = (String) parameters.get(key);

            if (first) {
               first = false;
            } else {
               buffer.append('&');
            }

            buffer.append(WhislEncoding.encode(key));
            buffer.append('=');
            buffer.append(WhislEncoding.encode(val));
         }
      }

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
    * Constructs a new <code>CallException</code>.
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
    *    the duration in milliseconds, must be &gt;= 0.
    *
    * @param detail
    *    a detailed description of the problem, can be <code>null</code> if
    *    there is no more detail.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @return
    *    the exception message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>shortReason == null
    *          || request == null
    *          || target == null
    *          || duration &lt; 0</code>.
    *
    * @since XINS 0.202
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
    * time the error was detected. The duration is in milliseconds and is
    * always &gt;= 0.
    */
   private final long _duration;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the original request.
    *
    * @return
    *    the original request, never <code>null</code>.
    *
    * @since XINS 0.198
    */
   public final CallRequest getRequest() {
      return _request;
   }

   /**
    * Returns the descriptor for the target that was attempted to be called.
    *
    * @return
    *    the target descriptor, cannot be <code>null</code>.
    *
    * @since XINS 0.201
    */
   public final TargetDescriptor getTarget() {
      return _target;
   }

   /**
    * The time elapsed between the time the call attempt was started and the
    * time the error was detected. The duration is in milliseconds.
    *
    * @return
    *    the duration in milliseconds, always &gt;= 0.
    *
    * @since XINS 0.202
    */
   public final long getDuration() {
      return _duration;
   }
}
