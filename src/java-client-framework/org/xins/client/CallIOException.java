/*
 * $Id$
 */
package org.xins.client;

import java.io.IOException;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.service.TargetDescriptor;

/**
 * Exception thrown to indicate that a XINS API call failed due to an I/O
 * error. An {@link IOException} needs to be passed to the constructor. This
 * {@link IOException} or the cause of it can be retrieved again using the
 * method {@link #getCause()}.
 *
 * <p>If <code>{@link IOException IOException}.{@link Throwable#getCause()
 * getCause}() == null</code> then {@link #getCause()} returns the
 * {@link IOException}, otherwise it returns the result of that method.
 * {@link IOException} will be returned from {@link #getCause()}.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.136
 */
public final class CallIOException
extends CallException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Checks the arguments for the constructor and then returns the short
    * reason.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param ioException
    *    the cause {@link IOException}, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || ioException == null</code>
    *
    * @since XINS 0.202
    */
   private static String getShortReason(CallRequest      request,
                                        TargetDescriptor target,
                                        IOException      ioException)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("request",     request,
                                     "target",      target,
                                     "ioException", ioException);

      // Return the short reason
      return "I/O error";
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallIOException</code>.
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
    * @param ioException
    *    the cause {@link IOException}, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || ioException == null
    *          || duration  &lt; 0</code>.
    *
    * @since XINS 0.202
    */
   CallIOException(CallRequest      request,
                   TargetDescriptor target,
                   long             duration,
                   IOException      ioException)
   throws IllegalArgumentException {

      // Check arguments first and then call superconstructor
      super(getShortReason(request, target, ioException),
            request,
            target,
            duration,
            null,
            ioException);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
