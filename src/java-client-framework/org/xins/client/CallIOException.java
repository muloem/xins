/*
 * $Id$
 */
package org.xins.client;

import java.io.IOException;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.service.TargetDescriptor;

/**
 * Exception thrown to indicate that a XINS API call failed due to an I/O
 * error. An {@link IOException} needs to be passed to the constructor.
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

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallIOException</code> based on an
    * <code>IOException</code>.
    *
    * @param ioException
    *    the cause {@link IOException}, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>ioException == null</code>.
    *
    * @deprecated
    *    Deprecated since XINS 0.201. Use
    *    {@link #CallIOException(CallRequest,TargetDescriptor,IOException)}
    *    instead.
    */
   public CallIOException(IOException ioException)
   throws IllegalArgumentException {

      // Call superconstructor first
      super(null, ioException);

      // Check preconditions
      MandatoryArgumentChecker.check("ioException", ioException);
   }

   /**
    * Constructs a new <code>CallIOException</code> based on an
    * <code>IOException</code>, for the specified <code>CallRequest</code> and
    * <code>TargetDescriptor</code>.
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
    *    if <code>ioException == null</code>.
    */
   public CallIOException(CallRequest      request,
                          TargetDescriptor target,
                          IOException      ioException)
   throws IllegalArgumentException {

      // Call superconstructor first
      // TODO: super(request, target, checkArguments(request, target, ioException), null);
      super(request, target, null, ioException);

      // Check preconditions
      MandatoryArgumentChecker.check("ioException", ioException);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
