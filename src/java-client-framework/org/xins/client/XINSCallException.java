/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;


import org.xins.common.service.CallException;
import org.xins.common.service.TargetDescriptor;

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
    * Checks the mandatory arguments for the constructor and then returns the
    * short reason.
    *
    * @param shortReason
    *    the short reason, cannot be <code>null</code>.
    *
    * @param result
    *    the call result, cannot be <code>null</code>.
    *
    * @return
    *    the short reason as given in the argument <code>shortReason</code>,
    *    never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>shortReason == null || result == null</code>
    */
   private static final String checkArguments(String         shortReason,
                                              XINSCallResult result)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("shortReason", shortReason,
                                     "result",      result);

      return shortReason;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------
   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>XINSCallException</code> based on a short reason,
    * the original request, target called, call duration, detail message and
    * cause exception.
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
                     XINSCallRequest  request,
                     TargetDescriptor target,
                     long             duration,
                     String           detail,
                     Throwable        cause)
   throws IllegalArgumentException {

      // Call superconstructor
      super(shortReason, request, target, duration, detail, cause);
   }

   /**
    * Constructs a new <code>XINSCallException</code> based on a short reason,
    * a XINS call result, detail message and cause exception.
    *
    * @param shortReason
    *    the short reason, cannot be <code>null</code>.
    *
    * @param result
    *    the call result, cannot be <code>null</code>.
    *
    * @param detail
    *    a detailed description of the problem, can be <code>null</code> if
    *    there is no more detail.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>shortReason == null || result == null</code>.
    */
   XINSCallException(String           shortReason,
                     XINSCallResult   result,
                     String           detail,
                     Throwable        cause)
   throws IllegalArgumentException {

      // Call superconstructor
      super(checkArguments(shortReason, result),
            (result == null) ? null : result.getRequest(),
            (result == null) ? null : result.getSucceededTarget(),
            (result == null) ?   0L : result.getDuration(),
            detail,
            cause);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
