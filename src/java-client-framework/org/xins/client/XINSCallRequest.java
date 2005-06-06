/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

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

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName);

      // Store function name, parameters and data section
      _functionName = functionName;
      _parameters   = new ProtectedPropertyReader(SECRET_KEY);
      _httpParams   = new ProtectedPropertyReader(SECRET_KEY);
      setParameters(parameters);
      setDataSection(dataSection);

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
   private final ProtectedPropertyReader _parameters;

   /**
    * The data section to pass in the request. This field can be
    * <code>null</code>.
    */
   private Element _dataSection;

   /**
    * The parameters to send with the HTTP request. Cannot be
    * <code>null</code>.
    */
   private final ProtectedPropertyReader _httpParams;

   /**
    * Pattern matcher.
    */
   private final Perl5Matcher _patternMatcher = new Perl5Matcher();


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
    * Returns the name of the function to call.
    *
    * @return
    *    the name of the function to call, never <code>null</code>.
    */
   public String getFunctionName() {
      return _functionName;
   }

   /**
    * Initializes the set of parameters. The implementation of this method
    * first removes all parameters and then adds the standard parameters.
    */
   private void initParameters() {

      // Remove all existing parameters
      _parameters.clear(SECRET_KEY);
      _httpParams.clear(SECRET_KEY);

      // Since XINS 1.0.1: Use XINS 1.0 standard calling convention
      _httpParams.set(SECRET_KEY, "_convention", "_xins-std");

      // TODO: Get convention parameter name from a class in XINS/Java Common Library
      // TODO: Get convention name from a class in XINS/Java Common Library

      // Add the diagnostic context ID to the parameter list, if there is one
      String contextID = NDC.peek();
      if (contextID != null) {
         _httpParams.set(SECRET_KEY, CONTEXT_ID_HTTP_PARAMETER_NAME, contextID);
      }

      // Add the function to the parameter list
      _httpParams.set(SECRET_KEY, "_function", _functionName);

      // XXX: For backwards compatibility, also add the parameter "function"
      //      to the list of HTTP parameters. This is, however, very likely to
      //      change in the future.
      _httpParams.set(SECRET_KEY, "function", _functionName);

      // Reset _asString so it will be re-initialized as necessary
      _asString = null;
   }

   /**
    * Sets the parameters for this function, replacing any existing
    * parameters. First the existing parameters are cleaned and then all
    * the specified parameters are copied to the internal set one-by-one. If
    * any of the parameters has an invalid name, then the internal parameter
    * set is cleaned and then an exception is thrown.
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

      // Clear the parameters
      initParameters();

      // Check and copy all parameters
      if (parameters != null) {
         Iterator names = parameters.getNames();
         while (names.hasNext()) {

            // Get the name and value
            String name  = (String) names.next();
            String value = parameters.get(name);

            // Set the combination (this may fail)
            setParameter(name, value);
         }
      }

      // Add the function to the parameter list
      _httpParams.set(SECRET_KEY, "_function", _functionName);

      // XXX: For backwards compatibility, also add the parameter "function"
      //      to the list of HTTP parameters. This is, however, very likely to
      //      change in the future.
      _httpParams.set(SECRET_KEY, "function", _functionName);

      // Reset _asString so it will be re-initialized as necessary
      _asString = null;
   }

   /**
    * Sets the parameter with the specified name.
    *
    * @param name
    *    the parameter name, cannot be <code>null</code>.
    *
    * @param value
    *    the new value for the parameter, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name</code> does not match the constraints for a parameter
    *    name, see {@link #PARAMETER_NAME_PATTERN_STRING} or if it equals
    *    <code>"function"</code>, which is currently still reserved.
    *
    * @since XINS 1.2.0
    */
   public void setParameter(String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      // Name cannot violate the pattern
      if (! _patternMatcher.matches(name, PARAMETER_NAME_PATTERN)) {
         // XXX: Consider using a different kind of exception for this
         //      specific case. For backwards compatibility, this exception
         //      class must be converted to an IllegalArgumentException in
         //      some cases or otherwise it should subclass
         //      IllegalArgumentException.

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
         _parameters.set(SECRET_KEY, name, value);
         _httpParams.set(SECRET_KEY, name, value);
      }
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
