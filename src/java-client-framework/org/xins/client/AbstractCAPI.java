/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.collections.PropertyReader;

import org.xins.common.http.HTTPCallException;
import org.xins.common.http.HTTPMethod;

import org.xins.common.service.Descriptor;
import org.xins.common.service.GenericCallException;
import org.xins.common.service.UnsupportedProtocolException;

/**
 * Base class for generated Client-side Application Programming Interface
 * (CAPI) classes.
 *
 * <p>This class should not be derived from manually. This class is only
 * intended to be used as a superclass of <code>CAPI</code> classes generated
 * by the XINS framework.
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
    * service descriptor and optional call configuration.
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
      _caller.setCAPI(this);
   }

   /**
    * Creates a new <code>AbstractCAPI</code> object, using the specified
    * service descriptor.
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
    * Assigns the specified call configuration to this CAPI object.
    *
    * @param config
    *    the call configuration to apply when executing a call with this CAPI
    *    object, or <code>null</code> if no specific call configuration should be
    *    associated with CAPI object; note that the call configuration can be
    *    overridden by the request, see
    *    {@link AbstractCAPICallRequest#configure(XINSCallConfig)}.
    *
    * @since XINS 1.2.0
    */
   public final void setXINSCallConfig(XINSCallConfig config) {
      _caller.setXINSCallConfig(config);
   }

   /**
    * Retrieves the call configuration currently associated with this CAPI
    * object.
    *
    * @return
    *    the call configuration currently associated with this CAPI object, or
    *    <code>null</code> if no specific call configuration is associated
    *    with this cAPI object; note that the call configuration can be
    *    overridden by the request, see
    *    {@link AbstractCAPICallRequest#configuration()}.
    *
    * @since XINS 1.2.0
    */
   public final XINSCallConfig getXINSCallConfig() {
      return _caller.getXINSCallConfig();
   }

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

   /**
    * Executes the specified call request.
    *
    * <p>This method is provided for CAPI subclasses.
    *
    * @param request
    *    the call request to execute, cannot be <code>null</code>.
    *
    * @return
    *    the result, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    *
    * @throws UnacceptableRequestException
    *    if the request is considered to be unacceptable; this is determined
    *    by calling
    *    <code>request.</code>{@link AbstractCAPICallRequest#validate() validate()}.
    *
    * @throws GenericCallException
    *    if the first call attempt failed due to a generic reason and all the
    *    other call attempts (if any) failed as well.
    *
    * @throws HTTPCallException
    *    if the first call attempt failed due to an HTTP-related reason and
    *    all the other call attempts (if any) failed as well.
    *
    * @throws XINSCallException
    *    if the first call attempt failed due to a XINS-related reason and
    *    all the other call attempts (if any) failed as well.
    *
    * @since XINS 1.2.0
    */
   protected final XINSCallResult callImpl(AbstractCAPICallRequest request)
   throws IllegalArgumentException,
          UnacceptableRequestException,
          GenericCallException,
          HTTPCallException,
          XINSCallException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request);

      // Check whether request is acceptable
      request.validate();

      // Execute the call request
      return _caller.call(request.getXINSCallRequest());
   }

   /**
    * Creates an <code>AbstractCAPIErrorCodeException</code> for the specified
    * error code. If the specified error code is not recognized, then
    * <code>null</code> is returned.
    *
    * @param errorCode
    *    the error code, never <code>null</code>.
    *
    * @return
    *    if the error code is recognized, then a matching
    *    {@link AbstractCAPIErrorCodeException} instance, otherwise
    *    <code>null</code>.
    *
    * @since XINS 1.2.0
    */
   protected abstract AbstractCAPIErrorCodeException
   convertErrorCode(String errorCode);
}
