/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.service.TargetDescriptor;

/**
 * Abstract base class for exceptions that indicate a specific error code was
 * received.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public abstract class AbstractCAPIErrorCodeException
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
    * Constructs a new <code>AbstractCAPIErrorCodeException</code> based on an
    * <code>UnsuccessfulXINSCallException</code>.
    *
    * @param e
    *    the {@link UnsuccessfulXINSCallException} to base this
    *    <code>AbstractCAPIErrorCodeException</code> on, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>e == null</code>.
    */
   protected AbstractCAPIErrorCodeException(UnsuccessfulXINSCallException e)
   throws IllegalArgumentException {
      super(e);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
