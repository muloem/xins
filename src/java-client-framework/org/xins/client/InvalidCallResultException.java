/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.service.TargetDescriptor;

/**
 * Exception thrown to indicate that the result from a XINS API call was
 * invalid according to the XINS standard.
 *
 * <p>Note that this exception is <em>only</em> thrown if the result is
 * invalid according to the XINS rules for an result XML document. If the
 * result is only invalid in relation to the applicable API specification,
 * then an {@link UnacceptableCallResultException} is thrown instead.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.33
 */
public final class InvalidCallResultException extends CallException {

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
    * Constructs a new <code>InvalidCallResultException</code>.
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
    *    a more detailed description of the problem, or <code>null</code> if
    *    none is available.
    *
    * @param cause
    *    the cause exception, or <code>null</code> if there is none.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0</code>.
    *
    * @since XINS 0.202
    */
   InvalidCallResultException(CallRequest      request,
                              TargetDescriptor target,
                              long             duration,
                              String           detail,
                              Throwable        cause)
   throws IllegalArgumentException {
      super("Invalid call result", request, target, duration, detail, cause);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
