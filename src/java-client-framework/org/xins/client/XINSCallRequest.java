/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.NDC;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;

import org.xins.common.xml.Element;
import org.xins.common.xml.ElementSerializer;

import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;
import org.xins.common.collections.ProtectedPropertyReader;

import org.xins.common.http.HTTPCallConfig;
import org.xins.common.http.HTTPCallRequest;
import org.xins.common.http.HTTPMethod;

import org.xins.common.service.CallRequest;
import org.xins.common.service.TargetDescriptor;

import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.TextUtils;

/**
 * Abstraction of a XINS request.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 *
 * @see XINSServiceCaller
 */
public final class XINSCallRequest extends CallRequest {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = XINSCallRequest.class.getName();

   /**
    * HTTP status code verifier that will only approve 2xx codes.
    */
   private static final HTTPStatusCodeVerifier HTTP_STATUS_CODE_VERIFIER = new HTTPStatusCodeVerifier();

   /**
    * Perl 5 pattern compiler.
    */
   private static final Perl5Compiler PATTERN_COMPILER = new Perl5Compiler();

   /**
    * Pattern matcher.
    */
   private static final Perl5Matcher PATTERN_MATCHER = new Perl5Matcher();

   /**
    * The pattern for a parameter name, as a character string.
    */
   public static final String PARAMETER_NAME_PATTERN_STRING = "[a-zA-Z][a-zA-Z0-9_]*";

   /**
    * The pattern for a parameter name.
    */
   private static final Pattern PARAMETER_NAME_PATTERN;

   /**
    * The name of the HTTP parameter that specifies the diagnostic context
    * identifier.
    */
   private static final String CONTEXT_ID_HTTP_PARAMETER_NAME = "_context";

   /**
    * The number of instances of this class. Initially zero.
    */
   private static int INSTANCE_COUNT;

   /**
    * Secret key used to set the HTTP parameters.
    */
   private static final Object SECRET_KEY = new Object();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Initializes this class. This function compiles
    * {@link #PARAMETER_NAME_PATTERN_STRING} to a {@link Pattern} and then
    * stores that in {@link #PARAMETER_NAME_PATTERN}.
    */
   static {

      final String THIS_METHOD = "<clinit>()";

      try {
         PARAMETER_NAME_PATTERN = PATTERN_COMPILER.compile(PARAMETER_NAME_PATTERN_STRING, Perl5Compiler.READ_ONLY_MASK);
      } catch (MalformedPatternException mpe) {
         final String DETAIL = "The pattern \""
                             + PARAMETER_NAME_PATTERN_STRING
                             + "\" is malformed.";

         throw Utils.logProgrammingError(
            CLASSNAME, THIS_METHOD,
            CLASSNAME, THIS_METHOD,
            DETAIL,    mpe);

      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>XINSCallRequest</code> for the specified function
    * with no parameters, disallowing fail-over unless the request was
    * definitely not (yet) accepted by the service.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public XINSCallRequest(String functionName)
   throws IllegalArgumentException {
      this(functionName, null, null);
   }

   /**
    * Constructs a new <code>XINSCallRequest</code> for the specified function
    * and parameters, disallowing fail-over unless the request was definitely
    * not (yet) accepted by the service.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code> if there are
    *    none.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public XINSCallRequest(String functionName, PropertyReader parameters)
   throws IllegalArgumentException {
      this(functionName, parameters, null);
   }

   /**
    * Constructs a new <code>XINSCallRequest</code> for the specified function
    * and parameters, disallowing fail-over unless the request was definitely
    * not (yet) accepted by the service.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code> if there are
    *    none.
    *
    * @param dataSection
    *    the data section for the input, if any, can be <code>null</code> if
    *    there are none.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    *
    * @since XINS 1.1.0
    */
   public XINSCallRequest(String         functionName,
                          PropertyReader parameters,
                          Element        dataSection)
   throws IllegalArgumentException {

      // Determine instance number first
      _instanceNumber = ++INSTANCE_COUNT;

      final String CONSTRUCTOR_DETAIL = "#"
                                      + _instanceNumber;

/* FIXME:
                                      + " [functionName="
                                      + TextUtils.quote(functionName)
                                      + "; parameters="
                                      + TextUtils.quote
*/

      // TRACE: Enter constructor
      Log.log_2000(CLASSNAME, CONSTRUCTOR_DETAIL);

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName);

      // Store function name, parameters and data section
      _functionName = functionName;
      setParameters(parameters);
      setDataSection(dataSection);

      // Initialize the UnsuccessfulXINSCallExceptionFactory
      _uxceFactory = new BasicUnsuccessfulXINSCallExceptionFactory();
      // TODO: Use shared BasicUnsuccessfulXINSCallExceptionFactory instance

      // TRACE: Leave constructor
      Log.log_2002(CLASSNAME, CONSTRUCTOR_DETAIL);

      // Note that _asString is lazily initialized.
   }

   /**
    * Constructs a new <code>XINSCallRequest</code> for the specified function
    * and parameters, possibly allowing fail-over even if the request was
    * possibly already received by a target service.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code> if there are
    *    none.
    *
    * @param failOverAllowed
    *    flag that indicates whether fail-over is in principle allowed, even
    *    if the request was already sent to the other end.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    *
    * @deprecated
    *    Deprecated since XINS 1.1.0.
    *    Use {@link #XINSCallRequest(String,PropertyReader)} in combination
    *    with {@link #setXINSCallConfig(XINSCallConfig)} instead.
    *    This constructor is guaranteed not to be removed before XINS 2.0.0.
    */
   public XINSCallRequest(String         functionName,
                          PropertyReader parameters,
                          boolean        failOverAllowed)
   throws IllegalArgumentException {
      this(functionName, parameters, failOverAllowed, null);
   }

   /**
    * Constructs a new <code>XINSCallRequest</code> for the specified function
    * and parameters, possibly allowing fail-over, optionally specifying the
    * HTTP method to use.
    *
    * @param functionName
    *    the name of the function to call, cannot be <code>null</code>.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code> if there are
    *    none.
    *
    * @param failOverAllowed
    *    flag that indicates whether fail-over is in principle allowed, even
    *    if the request was already sent to the other end.
    *
    * @param method
    *    the HTTP method to use, or <code>null</code> if the used
    *    <code>XINSServiceCaller</code> should determine what HTTP method to
    *    use.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code> or if <code>parameters</code>
    *    contains a name that does not match the constraints for a parameter
    *    name, see {@link #PARAMETER_NAME_PATTERN_STRING} or if it equals
    *    <code>"function"</code>, which is currently still reserved.
    *
    * @deprecated
    *    Deprecated since XINS 1.1.0.
    *    Use {@link #XINSCallRequest(String,PropertyReader)} in combination
    *    with {@link #setXINSCallConfig(XINSCallConfig)} instead.
    *    This constructor is guaranteed not to be removed before XINS 2.0.0.
    */
   public XINSCallRequest(String         functionName,
                          PropertyReader parameters,
                          boolean        failOverAllowed,
                          HTTPMethod     method)
   throws IllegalArgumentException {

      this(functionName, parameters);

      // Create an associated XINSCallConfig object
      XINSCallConfig callConfig = new XINSCallConfig();
      callConfig.setFailOverAllowed(failOverAllowed);
      callConfig.setHTTPMethod(method);
      setXINSCallConfig(callConfig);

      // Initialize the UnsuccessfulXINSCallExceptionFactory
      _uxceFactory = new BasicUnsuccessfulXINSCallExceptionFactory();
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
    * Description of this XINS call request. This field cannot be
    * <code>null</code>, it is initialized during construction.
    */
   private String _asString;

   /**
    * The name of the function to call. This field cannot be
    * <code>null</code>.
    */
   private final String _functionName;

   /**
    * The parameters to pass in the request, and their respective values. This
    * field can be <code>null</code>.
    */
   private PropertyReader _parameters;

   /**
    * The data section to pass in the request. This field can be
    * <code>null</code>.
    */
   private Element _dataSection;

   /**
    * The parameters to send with the HTTP request. Cannot be
    * <code>null</code>.
    */
   private ProtectedPropertyReader _httpParams;

   /**
    * The <code>UnsuccessfulXINSCallExceptionFactory</code> used for creating
    * <code>UnsuccessfulXINSCallException</code> instances. Never
    * <code>null</code>.
    */
   private UnsuccessfulXINSCallExceptionFactory _uxceFactory;


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

      // Lazily initialize the description of this call request object
      if (_asString == null) {
         FastStringBuffer buffer = new FastStringBuffer(208, "XINS HTTP request #");

         // Request number
         buffer.append(_instanceNumber);

         // HTTP method
         buffer.append(" [config=");
         buffer.append(TextUtils.quote(getCallConfig()));

         // Function name
         buffer.append("; function=\"");
         buffer.append(_functionName);

         // Parameters
         if (_parameters == null || _parameters.size() < 1) {
            buffer.append("\"; parameters=(null); contextID=");
         } else {
            buffer.append("\"; parameters=\"");
            PropertyReaderUtils.serialize(_parameters, buffer, "-");
            buffer.append("\"; contextID=");
         }

         // Diagnostic context identifier
         String contextID = _httpParams.get(CONTEXT_ID_HTTP_PARAMETER_NAME);
         if (contextID == null || contextID.length() < 1) {
            buffer.append("(null)]");
         } else {
            buffer.append('"');
            buffer.append(contextID);
            buffer.append("\"]");
         }

         _asString = buffer.toString();
      }

      return _asString;
   }

   /**
    * Returns the XINS call configuration.
    *
    * @return
    *    the XINS call configuration object, or <code>null</code>.
    *
    * @since XINS 1.1.0
    */
   public XINSCallConfig getXINSCallConfig() {
      return (XINSCallConfig) getCallConfig();
   }

   /**
    * Sets the associated XINS call configuration.
    *
    * @param callConfig
    *    the XINS call configuration object to associate with this request, or
    *    <code>null</code>.
    *
    * @since XINS 1.1.0
    */
   public void setXINSCallConfig(XINSCallConfig callConfig) {
      setCallConfig(callConfig);
   }

   /**
    * Creates an appropriate <code>UnsuccessfulXINSCallException</code> for
    * the specified target, duration and result data.
    *
    * @param target
    *    the target on which the request was executed, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration, must be &gt;= <code>0L</code>.
    *
    * @param resultData
    *    the data returned from the call, cannot be <code>null</code> and must
    *    have an error code set.
    *
    * @return
    *    a new {@link UnsuccessfulXINSCallException} instance, never
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>target                    ==   null
    *          || duration                  &lt; 0
    *          || resultData                ==   null
    *          || resultData.getErrorCode() ==   null</code>.
    */
   UnsuccessfulXINSCallException
   createUnsuccessfulXINSCallException(TargetDescriptor   target,
                                       long               duration,
                                       XINSCallResultData resultData)
   throws IllegalArgumentException {
      return _uxceFactory.create(this, target, duration, resultData);
   }

   /**
    * Sets the <code>UnsuccessfulXINSCallException</code> factory to use. If
    * <code>null</code> is passed, then a default one is used, which always
    * returns an instance of class {@link UnsuccessfulXINSCallException} self,
    * not of a subclass.
    *
    * @param factory
    *    the {@link UnsuccessfulXINSCallExceptionFactory} to use when creating
    *    {@link UnsuccessfulXINSCallException} instances, or <code>null</code>
    *    if a default one should be used.
    *
    * @since XINS 1.1.0
    */
   public void setUnsuccessfulXINSCallExceptionFactory(
   UnsuccessfulXINSCallExceptionFactory factory) {

      _uxceFactory = (factory != null)
                   ? factory
                   : new BasicUnsuccessfulXINSCallExceptionFactory();
   }

   /**
    * Retrieves the current <code>UnsuccessfulXINSCallException</code> factory
    * in use.
    *
    * @return
    *    the {@link UnsuccessfulXINSCallExceptionFactory} used for creating
    *    {@link UnsuccessfulXINSCallException} instances, never
    *    <code>null</code>.
    *
    * @since XINS 1.1.0
    */
   public UnsuccessfulXINSCallExceptionFactory
   getUnsuccessfulXINSCallExceptionFactory() {
      return _uxceFactory;
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
    * Sets the parameters for this function.
    *
    * @param parameters
    *    the input parameters, if any, can be <code>null</code> if there are
    *    none.
    *
    * @throws IllegalArgumentException
    *    if <code>parameters</code> contains a name that does not match the
    *    constraints for a parameter name, see
    *    {@link #PARAMETER_NAME_PATTERN_STRING} or if it equals
    *    <code>"function"</code>, which is currently still reserved.
    *
    * @since XINS 1.1.0
    */
   public void setParameters(PropertyReader parameters)
   throws IllegalArgumentException {

      // TODO: Optimize this method. Do not create ProtectedPropertyReader
      //       objects unless necessary.

      // Create PropertyReader for the HTTP parameters
      ProtectedPropertyReader httpParams = new ProtectedPropertyReader(SECRET_KEY);
      ProtectedPropertyReader xinsParams = new ProtectedPropertyReader(SECRET_KEY);

      // Since XINS 1.0.1: Use XINS 1.0 standard calling convention
      // TODO: Get convention parameter name from a class in XINS/Java Common Library
      // TODO: Get convention name from a class in XINS/Java Common Library
      httpParams.set(SECRET_KEY, "_convention", "_xins-std");

      // Check and copy all parameters to XINS and HTTP parameters
      if (parameters != null) {
         Iterator names = parameters.getNames();
         while (names.hasNext()) {

            // Get the name and value
            String name  = (String) names.next();
            String value = parameters.get(name);

            // Name cannot violate the pattern
            if (! PATTERN_MATCHER.matches(name, PARAMETER_NAME_PATTERN)) {
               // XXX: Consider using a different kind of exception for this
               //      specific case. For backwards compatibility, this
               //      exception class must be converted to an
               //      IllegalArgumentException by the constructor.

               FastStringBuffer buffer = new FastStringBuffer(121, "The parameter name \"");
               buffer.append(name);
               buffer.append("\" does not match the pattern \"");
               buffer.append(PARAMETER_NAME_PATTERN_STRING);
               buffer.append("\".");
               throw new IllegalArgumentException(buffer.toString());

            // Name cannot be "function"
            } else if ("function".equals(name)) {
               throw new IllegalArgumentException("Parameter name \"function\" is reserved.");

            // Name is considered valid, store it
            } else {
               xinsParams.set(SECRET_KEY, name, value);
               httpParams.set(SECRET_KEY, name, value);
            }
         }
      }

      // Add the function to the parameter list
      httpParams.set(SECRET_KEY, "_function", _functionName);

      // XXX: For backwards compatibility, also add the parameter "function"
      //      to the list of HTTP parameters. This is, however, very likely to
      //      change in the future.
      httpParams.set(SECRET_KEY, "function", _functionName);

      // Add the diagnostic context ID to the parameter list, if there is one
      String contextID = NDC.peek();
      if (contextID != null) {
         httpParams.set(SECRET_KEY, CONTEXT_ID_HTTP_PARAMETER_NAME, contextID);
      }

      // Initialize fields
      _parameters = xinsParams;
      _httpParams = httpParams;
      _asString   = null;
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
    * Sets the data section for the input.
    *
    * @param dataSection
    *    the data section for the input, or <code>null</code> if there is
    *    none.
    *
    * @since XINS 1.1.0
    */
   public void setDataSection(Element dataSection) {

      // Store the data section
      _dataSection = dataSection;

      // Add the data section to the HTTP parameter list
      if (dataSection == null) {
         _httpParams.set(SECRET_KEY, "_data", null);
      } else {
         // TODO: Do not recreate ElementSerializer each time
         ElementSerializer serializer = new ElementSerializer();
         String xmlDataSection = serializer.serialize(dataSection);
         _httpParams.set(SECRET_KEY, "_data", xmlDataSection);
      }
   }

   /**
    * Retrieves the data section for the input.
    *
    * @return
    *    the data section for the input, or <code>null</code> if there is
    *    none.
    *
    * @since XINS 1.1.0
    */
   public Element getDataSection() {
      return _dataSection;
   }

   /**
    * Determines whether fail-over is unconditionally allowed.
    *
    * @return
    *    <code>true</code> if fail-over is unconditionally allowed, even if the
    *    request was already received or even processed by the other end,
    *    <code>false</code> otherwise.
    *
    * @deprecated
    *    Deprecated since XINS 1.1.0.
    *    Call {@link #getXINSCallConfig()} instead and then call
    *    {@link XINSCallConfig#isFailOverAllowed() isFailOverAllowed()} on the
    *    returned call configuration object.
    *    This method is guaranteed not to be removed before XINS 2.0.0.
    */
   public boolean isFailOverAllowed() {
      XINSCallConfig callConfig = getXINSCallConfig();
      if (callConfig == null) {
         return false;
      } else {
         return getXINSCallConfig().isFailOverAllowed();
      }
   }

   /**
    * Returns an <code>HTTPCallRequest</code> that can be used to execute this
    * XINS request.
    *
    * @return
    *    this request converted to an {@link HTTPCallRequest}, never
    *    <code>null</code>.
    */
   HTTPCallRequest getHTTPCallRequest() {

      // Construct an HTTP call request
      HTTPCallRequest httpRequest = new HTTPCallRequest(_httpParams,
                                                        HTTP_STATUS_CODE_VERIFIER);

      // If there is a XINS call config, create an HTTP call config
      XINSCallConfig xinsConfig = getXINSCallConfig();
      if (xinsConfig != null) {
         HTTPCallConfig httpConfig = new HTTPCallConfig();
         httpConfig.setFailOverAllowed(xinsConfig.isFailOverAllowed());
         httpConfig.setMethod(xinsConfig.getHTTPMethod());
         httpRequest.setHTTPCallConfig(httpConfig);
      }

      return httpRequest;
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
    * @since XINS 1.0.0
    */
   private static final class HTTPStatusCodeVerifier
   extends Object
   implements org.xins.common.http.HTTPStatusCodeVerifier {

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
