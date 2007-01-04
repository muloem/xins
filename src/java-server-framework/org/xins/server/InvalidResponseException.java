/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

/**
 * Exception that indicates that a response is considered invalid.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class InvalidResponseException
extends RuntimeException {

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>InvalidResponseException</code> with the
    * specified detail message.
    *
    * @param message
    *    the detail message, can be <code>null</code>.
    */
   InvalidResponseException(String message) {
      super(message);
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
