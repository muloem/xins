/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;

import org.xins.common.text.FastStringBuffer;

import org.xins.common.service.CallRequest;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;

/**
 * A request towards an HTTP service.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public final class HTTPCallRequest extends CallRequest {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = HTTPCallRequest.class.getName();

   /**
    * The number of instances of this class. Initially zero.
    */
   private static int INSTANCE_COUNT;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HTTPCallRequest</code> with the specified HTTP
    * method. No arguments are be passed to the URL. Fail-over is disallowed,
    * unless the request was definitely not processed by the other end.
    *
    * @param method
    *    the HTTP method to use, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>method == null</code>.
    */
   public HTTPCallRequest(HTTPMethod method)
   throws IllegalArgumentException {
      this(method, null, false, null);
   }

   /**
    * Constructs a new <code>HTTPCallRequest</code> with the specified HTTP
    * method and parameters. Fail-over is disallowed, unless the request was
    * definitely not processed by the other end.
    *
    * @param method
    *    the HTTP method to use, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters for the HTTP call, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>method == null</code>.
    */
   public HTTPCallRequest(HTTPMethod     method,
                          PropertyReader parameters)
   throws IllegalArgumentException {
      this(method, parameters, false, null);
   }

   /**
    * Constructs a new <code>HTTPCallRequest</code> with the specified HTTP
    * method, parameters and status code verifier, optionally allowing
    * fail-over in all cases.
    *
    * @param method
    *    the HTTP method to use, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters for the HTTP call, can be <code>null</code>.
    *
    * @param failOverAllowed
    *    flag that indicates whether fail-over is in principle allowed, even
    *    if the request was already sent to the other end.
    *
    * @param statusCodeVerifier
    *    the HTTP status code verifier, or <code>null</code> if all HTTP
    *    status codes are allowed.
    *
    * @throws IllegalArgumentException
    *    if <code>method == null || parameters == null</code>.
    */
   public HTTPCallRequest(HTTPMethod             method,
                          PropertyReader         parameters,
                          boolean                failOverAllowed,
                          HTTPStatusCodeVerifier statusCodeVerifier)
   throws IllegalArgumentException {

      // Determine instance number first
      _instanceNumber = ++INSTANCE_COUNT;
      FastStringBuffer buffer = new FastStringBuffer(137, "HTTPCallRequest #");
      buffer.append(_instanceNumber);
      String asString = buffer.toString();

      // TRACE: Enter constructor
      Log.log_3000(CLASSNAME, "#" + _instanceNumber);

      // Check preconditions
      MandatoryArgumentChecker.check("method", method);

      // Store information
      _method             = method;
      _parameters         = parameters;
      _failOverAllowed    = failOverAllowed;
      _statusCodeVerifier = statusCodeVerifier;

      // Complete the description of this object
      buffer.append(" [method=");
      buffer.append(_method.toString());
      buffer.append(", failOverAllowed=");
      buffer.append(failOverAllowed);
      buffer.append(", parameters=");
      PropertyReaderUtils.serialize(_parameters, buffer, "(null)");
      buffer.append(']');
      asString = buffer.toString();
      _asString = asString;

      // TRACE: Leave constructor
      Log.log_3002(CLASSNAME, asString);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The 1-based sequence number of this instance. Since this number is
    * 1-based, the first instance of this class will have instance number 1
    * assigned to it.
    */
   private final int _instanceNumber;

   /**
    * Description of this HTTP call request. This field cannot be
    * <code>null</code>, it is initialized during construction.
    */
   private String _asString;

   /**
    * The HTTP method to use when executing this call request. This field
    * cannot be <code>null</code>, it is initialized during construction.
    */
   private final HTTPMethod _method;

   /**
    * The parameters for the HTTP call. This field cannot be
    * <code>null</code>, it is initialized during construction.
    */
   private final PropertyReader _parameters;

   /**
    * Flag that indicates whether fail-over is in principle allowed, even if
    * the request was already sent to the other end.
    */
   private final boolean _failOverAllowed;

   /**
    * The HTTP status code verifier, or <code>null</code> if all HTTP status codes are allowed.
    */
   private final HTTPStatusCodeVerifier _statusCodeVerifier;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Describes this request.
    *
    * @return
    *    the description of this request, never <code>null</code>.
    */
   public String describe() {
      return _asString;
   }

   /**
    * Returns the HTTP method associated with this call request.
    *
    * @return
    *    the HTTP method, never <code>null</code>.
    */
   public HTTPMethod getMethod() {
      return _method;
   }

   /**
    * Returns the parameters associated with this call request.
    *
    * @return
    *    the parameters, can be <code>null</code>.
    */
   public PropertyReader getParameters() {
      return _parameters;
   }

   /**
    * Determines whether fail-over is in principle allowed, even if the
    * request was already sent to the other end.
    *
    * @return
    *    <code>true</code> if fail-over is in principle allowed, even if the
    *    request was already sent to the other end, <code>false</code>
    *    otherwise.
    */
   public boolean isFailOverAllowed() {
      return _failOverAllowed;
   }

   /**
    * Returns the HTTP status code verifier. If all HTTP status codes are
    * allowed, then <code>null</code> is returned.
    *
    * @return
    *    the HTTP status code verifier, or <code>null</code>.
    */
   public HTTPStatusCodeVerifier getStatusCodeVerifier() {
      return _statusCodeVerifier;
   }
}
