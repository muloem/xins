/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.threads;

/**
 * Exception thrown to indicates a thread was waiting in the queue for too
 * long.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class QueueTimeOutException
extends RuntimeException {

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
   // Methods
   //-------------------------------------------------------------------------
}
