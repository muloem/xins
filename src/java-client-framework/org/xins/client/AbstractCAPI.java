/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.http.HTTPMethod;
import org.xins.common.service.Descriptor;

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
public abstract class AbstractCAPI {

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
    * <code>Descriptor</code>, with the specified HTTP method.
    *
    * <p>This constructor is considered internal to XINS. Do not use it
    * directly.
    *
    * @param descriptor
    *    the descriptor for the service(s), cannot be <code>null</code>.
    *
    * @param httpMethod
    *    the HTTP method to use when doing a call, or <code>null</code> if the
    *    default should be used.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    *
    * @since XINS 1.1.0
    */
   protected AbstractCAPI(Descriptor descriptor, HTTPMethod httpMethod)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("descriptor", descriptor);

      // Store service caller
      _caller     = new XINSServiceCaller(descriptor);
      _httpMethod = (httpMethod == null) ? HTTPMethod.POST : httpMethod;
   }

   /**
    * Creates a new <code>AbstractCAPI</code> object, using the specified
    * <code>Descriptor</code>.
    *
    * <p>The default HTTP method will be used when doing a call.
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
    * @deprecated
    *    Deprecated since XINS 1.1.0. This constructor is expected to be
    *    removed in a later version of XINS. It is not removed yet to remain
    *    fully compatible with XINS 1.0.
    */
   protected AbstractCAPI(Descriptor descriptor)
   throws IllegalArgumentException {
      this(descriptor, null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The XINS service caller to use. This field cannot be <code>null</code>.
    */
   private XINSServiceCaller _caller;

   /**
    * The HTTP method to use when making a call. This field cannot be
    * <code>null</code>.
    */
   private HTTPMethod _httpMethod;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>XINSCallRequest</code>.
    *
    * <p>This method is considered internal to XINS. Do not use it
    * directly.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code> and should not
    *    be modifiable.
    *
    * @since XINS 1.1.0
    */
   protected final XINSCallRequest createXINSCallRequest(
      String         functionName,
      PropertyReader parameters)
   throws IllegalArgumentException {
      return new XINSCallRequest(functionName,
                                 parameters,
                                 false,        // unconditional fail-over: no
                                 _httpMethod);
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
