/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.ExceptionUtils;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.service.TargetDescriptor;

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
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param message
    *    a description of the reason, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @return
    *    the exception message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null || target == null</code>.
    *
    * @since XINS 0.198
    */
   private static final String createMessage(CallRequest      request,
                                             TargetDescriptor target,
                                             String           message,
                                             Throwable        cause)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request,
                                     "target",  target);

      return null; // TODO
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
    * Constructs a new <code>CallException</code> with the
    * specified detail message and cause exception.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @deprecated
    *    Deprecated since XINS 0.198. Use
    *    {@link CallException(CallRequest,String,Throwable)} instead.
    */
   protected CallException(String message, Throwable cause) {
      super(message, rootCauseFor(cause));

      _request = null;
   }

   /**
    * Constructs a new <code>CallException</code> with the
    * specified detail message and cause exception.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null || target == null</code>.
    */
   protected CallException(CallRequest      request,
                           TargetDescriptor target,
                           String           message,
                           Throwable        cause)
   throws IllegalArgumentException {

      // Call superconstructor with fabricated message
      super(createMessage(request, target, message, cause), // message
            rootCauseFor(cause));                           // cause

      // Store request
      _request = request;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The original request.
    */
   private final CallRequest _request;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the original request.
    *
    * @return
    *    the original request, or <code>null</code> if the deprecated
    *    constructor was used.
    *
    * @since XINS 0.198
    */
   // TODO: Change @return to: the original request, cannot be <code>null</code>.
   public final CallRequest getRequest() {
      return _request;
   }
}
