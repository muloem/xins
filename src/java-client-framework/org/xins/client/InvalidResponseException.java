/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.service.TargetDescriptor;

/**
 * Exception thrown to indicate an <code>_InvalidResponse</code> error code
 * was received.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public abstract class InvalidResponseException
extends UnsuccessfulXINSCallException {

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
    * Constructs a new <code>InvalidResponseException</code> based on an
    * <code>UnsuccessfulXINSCallException</code>.
    *
    * @param e
    *    the {@link UnsuccessfulXINSCallException} to base this
    *    <code>InvalidResponseException</code> on, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>e == null</code>.
    */
   protected InvalidResponseException(UnsuccessfulXINSCallException e)
   throws IllegalArgumentException {
      super(e);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   // TODO: Add methods for retrieval of details
}
