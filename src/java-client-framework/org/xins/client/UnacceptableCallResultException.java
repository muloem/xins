/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception that indicates that an API call returned a result that was
 * considered unacceptable by the application layer.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.136
 */
public final class UnacceptableCallResultException extends Exception {

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
    * Constructs a new <code>UnacceptableCallResultException</code>.
    *
    * @param result
    *    the (successful) {@link XINSServiceCaller.Result} that is considered
    *    unacceptable, never <code>null</code>.
    *
    * @param detail
    *    a detailed description of why the result is considered unacceptable,
    *    or <code>null</code> if such a description is not available.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null</code>.
    *
    * @since XINS 0.202
    */
   public UnacceptableCallResultException(XINSServiceCaller.Result result,
                                          String                   detail,
                                          Throwable                cause)
   throws IllegalArgumentException {

      super(detail, cause);

      // Check preconditions
      MandatoryArgumentChecker.check("result", result);

      // TODO: Do something with result
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
