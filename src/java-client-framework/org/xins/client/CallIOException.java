/*
 * $Id$
 */
package org.xins.client;

import java.io.IOException;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception thrown to indicate that a XINS API call failed due to an I/O
 * error.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
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
    */
   public CallIOException(IOException ioException)
   throws IllegalArgumentException {
      super(ioException.getMessage(), ioException);
      MandatoryArgumentChecker.check("ioException", ioException);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
