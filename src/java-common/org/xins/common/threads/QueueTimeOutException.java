/*
 * $Id$
 */
package org.xins.common.threads;

/**
 * Exception thrown to indicates a thread was waiting in the queue for too
 * long.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class QueueTimeOutException
extends RuntimeException {

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
    * Constructs a new <code>QueueTimeOutException</code>.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    */
   public QueueTimeOutException(String message) {
      super(message);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
