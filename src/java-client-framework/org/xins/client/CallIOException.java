/*
 * $Id$
 */
package org.xins.client;

import java.io.IOException;

import org.xins.common.ExceptionUtils;

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
    */
   public CallIOException(IOException ioException)
   throws IllegalArgumentException {
      super(ioException, null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
