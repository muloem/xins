/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.collections.PropertyReader;

import org.xins.common.http.HTTPMethod;

import org.xins.common.service.Descriptor;
import org.xins.common.service.UnsupportedProtocolException;

/**
 * Base class for client-side calling interface classes.
 *
 * <p>The constructors of this class are considered internal to XINS and
 * should not be used directly. The behavior of the constructors may be
 * changed in later releases of XINS or they may even be removed.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public abstract class AbstractCAPI extends Object {

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
    * Creates a new <code>AbstractCAPI</code> object, using the specified
    * <code>XINSServiceCaller</code>.
    *
    * <p>This constructor is considered internal to XINS. Do not use it
    * directly.
    *
    * @param descriptor
    *    the descriptor for the service(s), cannot be <code>null</code>.
    *
    * @param callConfig
    *    fallback configuration for the calls, or <code>null</code> if a
    *    default should be used.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    *
    * @throws UnsupportedProtocolException
    *    if any of the target descriptors specifies an unsupported protocol.
    *
    * @since XINS 1.1.0
    */
   protected AbstractCAPI(Descriptor descriptor, XINSCallConfig callConfig)
   throws IllegalArgumentException, UnsupportedProtocolException {

      // Check preconditions
      MandatoryArgumentChecker.check("descriptor", descriptor);

      // Create and store service caller
      _caller = new XINSServiceCaller(descriptor, callConfig);
   }

   /**
    * Creates a new <code>AbstractCAPI</code> object, using the specified
    * <code>Descriptor</code>.
    *
    * <p>A default XINS call configuration will be used.
    *
    * <p>This constructor is considered internal to XINS. Do not use it
    * directly.
    *
    * @param descriptor
    *    the descriptor for the service(s), cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    *
    * @throws UnsupportedProtocolException
    *    if any of the target descriptors specifies an unsupported protocol
    *    (<em>since XINS 1.1.0</em>).
    *
    * @deprecated
    *    Deprecated since XINS 1.1.0. This constructor is expected to be
    *    removed in a later version of XINS. It is not removed yet to remain
    *    fully compatible with XINS 1.0.
    */
   protected AbstractCAPI(Descriptor descriptor)
   throws IllegalArgumentException, UnsupportedProtocolException {
      this(descriptor, null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The XINS service caller to use. This field cannot be <code>null</code>.
    */
   private XINSServiceCaller _caller;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the XINS service caller to use.
    *
    * <p>This method is considered internal to XINS. It should not be called
    * directly, nor overridden.
    *
    * <p>This method is expected to be marked <code>final</code> in a future
    * release of XINS. This is not done yet to remain fully compatible with
    * XINS 1.0.
    *
    * @return
    *    the XINS service caller to use, never <code>null</code>.
    */
   protected XINSServiceCaller getCaller() {
      // TODO for XINS 2.0.0: Mark this method as final
      return _caller;
   }

   /**
    * Returns the version of XINS used to build this CAPI class.
    *
    * @return
    *    the version as a {@link String}, cannot be <code>null</code>.
    */
   public abstract String getXINSVersion();
}
