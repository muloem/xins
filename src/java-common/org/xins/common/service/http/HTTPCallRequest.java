/*
 * $Id$
 */
package org.xins.common.service.http;

import org.xins.common.collections.PropertyReader;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.service.CallRequest;
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

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HTTPCallRequest</code> with the specified HTTP
    * method and parameters.
    *
    * @param method
    *    the HTTP method to use, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters for the HTTP call, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>method == null || parameters == null</code>.
    */
   public HTTPCallRequest(HTTPMethod     method,
                          PropertyReader parameters)
   throws IllegalArgumentException {
      this(method, parameters, null);
   }

   /**
    * Constructs a new <code>HTTPCallRequest</code> with the specified HTTP
    * method, parameters and status code verifier.
    *
    * @param method
    *    the HTTP method to use, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters for the HTTP call, cannot be <code>null</code>.
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
                          HTTPStatusCodeVerifier statusCodeVerifier)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("method",     method,
                                     "parameters", parameters);

      // Store information
      _method             = method;
      _parameters         = parameters;
      _statusCodeVerifier = statusCodeVerifier;

      // Construct a textual representation of this object
      FastStringBuffer buffer = new FastStringBuffer(137, "HTTP ");
      buffer.append(method.toString());
      buffer.append(" request with parameters");

      // TODO: Fill buffer

      _asString = buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Description of this HTTP call request. This field cannot be
    * <code>null</code>, it is initialized during construction.
    */
   private final String _asString;

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
    *    the parameters, never <code>null</code>.
    */
   public PropertyReader getParameters() {
      return _parameters;
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
