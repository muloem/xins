/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

/**
 * Base class for CAPI call request classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public abstract class AbstractCAPICallRequest
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = AbstractCAPICallRequest.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>AbstractCAPICallRequest</code> object.
    */
   protected AbstractCAPICallRequest() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Validates whether this request is considered acceptable (wrapper
    * method). If required parameters are missing or if certain parameter
    * values are out of bounds, then an exception is thrown.
    *
    * <p>This method is called when the request is executed, but it may also
    * be called in advance.
    *
    * @throws UnacceptableRequestException
    *    if this request is considered unacceptable.
    */
   public final void validate()
   throws UnacceptableRequestException {
      validateImpl();
   }

   /**
    * Validates whether this request is considered acceptable (implementation
    * method). If required parameters are missing or if certain parameter
    * values are out of bounds, then an exception is thrown.
    *
    * <p>This method is called by {@link #validate()}. It should not be called
    * from anywhere else.
    *
    * @throws UnacceptableRequestException
    *    if this request is considered unacceptable.
    */
   protected abstract void validateImpl();
}
