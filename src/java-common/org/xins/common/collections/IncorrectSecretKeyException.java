/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

/**
 * Exception that indicates a secret key argument did not match the actual
 * secret key.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.2.0
 */
public final class IncorrectSecretKeyException
extends IllegalArgumentException {

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>IncorrectSecretKeyException</code>.
    */
   IncorrectSecretKeyException() {
      super("Incorrect secret key.");
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
