/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.service.CallRequest;
import org.xins.common.service.http.HTTPCallRequest;
import org.xins.common.service.http.HTTPServiceCaller;
import org.xins.common.text.FastStringBuffer;

/**
 * Abstraction of a XINS request.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.207
 */
public final class XINSCallRequest extends CallRequest {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * HTTP status code verifier that will only approve 2xx codes.
    */
   private static final org.xins.common.service.http.HTTPStatusCodeVerifier HTTP_STATUS_CODE_VERIFIER = new HTTPStatusCodeVerifier();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>XINSCallRequest</code> for the specified function
    * and parameters, disallowing fail-over unless the request was definitely
    * not (yet) accepted by the service.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code> and should not
    *    be modifiable.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public XINSCallRequest(String functionName, PropertyReader parameters)
   throws IllegalArgumentException {
      this(functionName, parameters, false, null);
   }

   /**
    * Constructs a new <code>CallRequest</code> for the specified function and
    * parameters, possibly allowing fail-over.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code> and should not
    *    be modifiable.
    *
    * @param failOverAllowed
    *    flag that indicates whether fail-over is in principle allowed, even
    *    if the request was already sent to the other end.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public XINSCallRequest(String         functionName,
                          PropertyReader parameters,
                          boolean        failOverAllowed)
   throws IllegalArgumentException {
      this(functionName, parameters, failOverAllowed, null);
   }

   /**
    * Constructs a new <code>CallRequest</code> for the specified function and
    * parameters, possibly allowing fail-over, optionally specifying the HTTP
    * method to use.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code> and should not
    *    be modifiable.
    *
    * @param failOverAllowed
    *    flag that indicates whether fail-over is in principle allowed, even
    *    if the request was already sent to the other end.
    *
    * @param method
    *    the HTTP method to use, or <code>null</code> if the default HTTP
    *    method (POST) should be used.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public XINSCallRequest(String                   functionName,
                          PropertyReader           parameters,
                          boolean                  failOverAllowed,
                          HTTPServiceCaller.Method method)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName);

      // HTTP method defaults to POST
      if (method == null) {
         method = HTTPServiceCaller.POST;
      }

      // TODO: Check the parameters and throw an exception if an entry is
      //       invalid.

      // TODO: Add the function to the parameter list

      // TODO: Add the diagnostic context ID to the parameter list

      // Store the information
      _functionName    = functionName;
      _parameters      = parameters; // XXX: Make unmodifiable and change @param?
      _failOverAllowed = failOverAllowed;
      _httpRequest     = new HTTPCallRequest(method, httpParams, HTTP_STATUS_CODE_VERIFIER);

      // Construct a textual representation of this object
      FastStringBuffer buffer = new FastStringBuffer(149, "XINS HTTP ");
      buffer.append(method.toString());
      buffer.append(" request with parameters");

      // TODO: Fill buffer

      _asString = buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Description of this XINS call request. This field cannot be
    * <code>null</code>, it is initialized during construction.
    */
   private final String _asString;

   /**
    * The name of the function to call. This field cannot be
    * <code>null</code>.
    */
   private final String _functionName;

   /**
    * The parameters to pass in the request, and their respective values. This
    * field can be <code>null</code>.
    */
   private final PropertyReader _parameters;

   /**
    * Flag that indicates whether fail-over is in principle allowed, even if
    * the request was already sent to the other end.
    */
   private final boolean _failOverAllowed;

   /**
    * The HTTP service caller used to execute the request to a XINS service
    * over HTTP. This field is never <code>null</code>.
    */
   private final HTTPCallRequest _httpRequest;


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
    * Returns the name of the function to call.
    *
    * @return
    *    the name of the function to call, never <code>null</code>.
    */
   public String getFunctionName() {
      return _functionName;
   }

   /**
    * Gets all parameters to pass with the call, with their respective values.
    *
    * @return
    *    the parameters, or <code>null</code> if there are none.
    */
   public PropertyReader getParameters() {
      return _parameters;
   }

   /**
    * Gets the value of the specified parameter.
    *
    * @param name
    *    the parameter name, not <code>null</code>.
    *
    * @return
    *    string containing the value of the parameter, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public String getParameter(String name)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      return (_parameters == null) ? null : _parameters.get(name);
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
    * Returns the underlying <code>HTTPCallRequest</code>.
    *
    * @return
    *    the underlying {@link HTTPCallRequest}, never <code>null</code>.
    */
   HTTPCallRequest getHTTPCallRequest() {
      return _httpRequest;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * HTTP status code verifier that will only approve 2xx codes.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 0.207
    */
   private static final class HTTPStatusCodeVerifier
   extends Object
   implements org.xins.common.service.http.HTTPStatusCodeVerifier {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>HTTPStatusCodeVerifier</code>.
       */
      private HTTPStatusCodeVerifier() {
         // empty
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Checks if the specified HTTP status code is considered acceptable or
       * unacceptable.
       *
       * <p>The implementation of this method in class
       * {@link XINSCallRequest.HTTPStatusCodeVerifier} returns
       * <code>true</code> only for 2xx status codes.
       *
       * @param code
       *    the HTTP status code to check.
       *
       * @return
       *    <code>true</code> if <code>code &gt;= 200 &amp;&amp; code &lt;=
       *    299</code>.
       */
      public boolean isAcceptable(int code) {
         return (code >= 200) && (code <= 299);
      }
   }
}
