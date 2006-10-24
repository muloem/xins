/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.ProtectedPropertyReader;
import org.xins.common.manageable.InitializationException;
import org.xins.common.manageable.Manageable;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Abstraction of a calling convention. A calling convention determines how an
 * HTTP request is converted to a XINS function invocation request and how a
 * XINS function result is converted back to an HTTP response.
 *
 * <h2>Thread safety</h2>
 *
 * <p>Calling convention implementations must be thread-safe.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 *
 * @see CallingConventionManager
 */
abstract class CallingConvention extends Manageable {

   //------------------------------------------------------------------------
   // Class fields
   //------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = CallingConvention.class.getName();

   /**
    * The default value of the <code>"Server"</code> header sent with an HTTP
    * response. The actual value is
    * <code>"XINS/Java Server Framework "</code>, followed by the version of
    * the framework.
    *
    * <p>TODO: Move this constant and the associated functionality elsewhere,
    * since it does not seem to belong in this class.
    */
   private static final String SERVER_HEADER
      = "XINS/Java Server Framework " + Library.getVersion();

   /**
    * The default set of supported HTTP methods.
    */
   private static final String[] DEFAULT_SUPPORTED_METHODS =
      new String[] { "HEAD", "GET", "POST" };


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Changes a parameter set to remove all parameters that should not be
    * passed to functions.
    *
    * <p>A parameter will be removed if it matches any of the following
    * conditions:
    *
    * <ul>
    *    <li>parameter name is <code>null</code>;
    *    <li>parameter name is empty;
    *    <li>parameter value is <code>null</code>;
    *    <li>parameter value is empty;
    *    <li>parameter name equals <code>"function"</code>.
    * </ul>
    *
    * <p>TODO: Move this method elsewhere, as this behaviour is specific for
    * certain calling conventions, it is considered deprecated and it is
    * likely to be changed in XINS 2.0.
    *
    * @param parameters
    *    the {@link ProtectedPropertyReader} containing the set of parameters
    *    to investigate, cannot be <code>null</code>.
    *
    * @param secretKey
    *    the secret key required to be able to modify the
    *    {@link ProtectedPropertyReader} instance, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>parameters == null || secretKey == null</code>.
    */
   static void cleanUpParameters(ProtectedPropertyReader parameters,
                                 Object                  secretKey)
   throws IllegalArgumentException {

      // Check arguments
      MandatoryArgumentChecker.check("parameters", parameters,
                                     "secretKey",  secretKey);

      // Get the parameter names
      Iterator names = parameters.getNames();

      // Loop through all parameters
      ArrayList toRemove = new ArrayList();
      while (names.hasNext()) {

         // Determine parameter name and value
         String name  = (String) names.next();
         String value = parameters.get(name);

         // If the parameter name or value is empty, or if the name is
         // "function", then mark the parameter as 'to be removed'.
         // Parameters starting with an underscore are reserved for XINS, so
         // mark these as 'to be removed' as well.
         if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value) || "function".equals(name) || name.charAt(0) == '_') {
            toRemove.add(name);
         }
      }

      // If there is anything to remove, then do so
      Iterator itRemove = toRemove.iterator();
      while (itRemove.hasNext()) {
         String name = (String) itRemove.next();
         parameters.set(secretKey, name, null);
      }
   }


   //------------------------------------------------------------------------
   // Constructors
   //------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallingConvention</code>. A
    * <code>CallingConvention</code> instance can only be generated by the
    * XINS/Java Server Framework.
    *
    * @throws IllegalStateException
    *    if this <code>CallingConvention</code> is not constructed by the
    *    XINS/Java Server Framework.
    */
   protected CallingConvention() {
      _cachedRequest    = new ThreadLocal();
      _cachedRequestXML = new ThreadLocal();
      _api              = CallingConventionManager.getCurrent().getAPI();
   }


   //------------------------------------------------------------------------
   // Fields
   //------------------------------------------------------------------------

   /**
    * Cached <code>HttpServletRequest</code>, local per thread. When
    * any of the <code>parseXMLRequest</code> methods is called, the request
    * is stored in this field, to be able to confirm later that
    * {@link #_cachedRequestXML} should be returned.
    *
    * <p>The value inside this {@link ThreadLocal} is always either
    * <code>null</code> or otherwise an {@link HttpServletRequest} instance.
    */
   private final ThreadLocal _cachedRequest;

   /**
    * Cached XML <code>Element</code>, local per thread. When
    * any of the <code>parseXMLRequest</code> methods is called, the result is
    * stored in this field before returning it.
    *
    * <p>The value inside this {@link ThreadLocal} is always either
    * <code>null</code> or otherwise an {@link Element} instance.
    */
   private final ThreadLocal _cachedRequestXML;

   /**
    * The current API. Initialized by the constructor and returned to
    * interested subclasses by {@link #getAPI()}.
    */
   private final API _api;


   //------------------------------------------------------------------------
   // Methods
   //------------------------------------------------------------------------

   /**
    * Determines the current API.
    *
    * @return
    *    the current {@link API}, never <code>null</code>.
    *
    * @since XINS 1.5.0
    */
   protected final API getAPI() {
      return _api;
   }

   /**
    * Determines which HTTP methods are supported for function invocations.
    *
    * <p>Each <code>String</code> in the returned array must be one
    * supported method.
    *
    * <p>The returned array must not be <code>null</code>, it must only
    * contain valid HTTP method names, so they may not contain whitespace, for
    * example. Duplicates will be ignored. HTTP method names must be in uppercase.
    *
    * <p>There must be at least one HTTP method supported for function
    * invocations.
    *
    * <p>Note that <em>OPTIONS</em> must not be returned by this method, as it
    * is not an HTTP method that can ever be used to invoke a XINS function.
    * <p>HTTP <em>OPTIONS</em> requests are treated differently. For the path
    * <code>*</code> the capabilities of the whole server are returned. For other
    * paths, the appropriate calling convention is determined, after which the
    * set of supported HTTP methods is returned to the called.
    *
    * @return
    *    the HTTP methods supported, in a <code>String</code> array, must
    *    not be <code>null</code>.
    *
    * @since XINS 1.5.0
    */
   protected String[] getSupportedMethods() {
      return DEFAULT_SUPPORTED_METHODS;
   }

   /**
    * Determines which HTTP methods are supported for function invocations,
    * for the specified request.
    *
    * <p>Each <code>String</code> in the returned array must be one
    * supported method.
    *
    * <p>The returned array may be <code>null</code>. If it is not, then the
    * returned array must only contain valid HTTP method names, so they may
    * not contain whitespace, for example. HTTP method names must be in uppercase.
    *
    * <p>There must be at least one HTTP method supported for function
    * invocations.
    *
    * <p>Note that <em>OPTIONS</em> must not be returned by this method, as it
    * is not an HTTP method that can ever be used to invoke a XINS function.
    *
    * <p>The set of supported methods must be a subset of the set returned by
    * {@link #getSupportedMethods()}.
    *
    * <p>The default implementation of this method returns the set returned by
    * {@link #getSupportedMethods()}.
    *
    * @param request
    *    the request to determine the supported methods for.
    *
    * @return
    *    the HTTP methods supported for the specified request, in a
    *    <code>String</code> array, can be <code>null</code>.
    *
    * @since XINS 1.5.0
    */
   protected String[] getSupportedMethods(HttpServletRequest request) {
      return getSupportedMethods();
   }

   /**
    * Checks if the specified request can be handled by this calling
    * convention. Assuming this <code>CallingConvention</code> instance is
    * usable and the HTTP method is supported, this method delegates to
    * {@link #matches(HttpServletRequest)}.
    *
    * <p>If this calling convention is not usable (see {@link #isUsable()}),
    * then <code>false</code> is returned, even <em>before</em> calling
    * {@link #matches(HttpServletRequest)}.
    *
    * <p>If this method does not support the HTTP method for function
    * invocations, then <code>false</code> is returned.
    *
    * <p>If {@link #matches(HttpServletRequest)} throws an exception, then
    * this exception is ignored and <code>false</code> is returned.
    *
    * <p>This method is guaranteed not to throw any exception.
    *
    * @param httpRequest
    *    the HTTP request to investigate, cannot be <code>null</code>.
    *
    * @return
    *    <code>true</code> if this calling convention is <em>possibly</em>
    *    able to handle this request, or <code>false</code> if it is
    *    <em>definitely</em> not able to handle this request.
    */
   final boolean matchesRequest(HttpServletRequest httpRequest) {

      // First check if this CallingConvention instance is bootstrapped and
      // initialized
      if (! isUsable()) {
         return false;
      }

      // Make sure the HTTP method is supported
      String method = httpRequest.getMethod();
      if (!Arrays.asList(getSupportedMethods(httpRequest)).contains(method) && !"OPTIONS".equals(method)) {
         return false;
      }

      // Delegate to the 'matches' method
      try {
         return matches(httpRequest);

      // Assume that an exception indicates the request cannot be handled
      //
      // NOTE: We do not log this exception, because it would possibly show up
      //       in the logs on a regular basis, drawing attention to a
      //       non-issue.
      //
      // TODO: There is a conflict with the documentation of the matches
      //       method. The latter indicates an exception thrown by that method
      //       is logged as an ignorable exception
      } catch (Throwable exception) {
         return false;
      }
   }

   /**
    * Checks if the specified request can possibly be handled by this calling
    * convention as a function invocation.
    *
    * <p>Implementations of this method should be optimized for performance,
    * as this method may be called for each incoming request. Also, this
    * method should not have any side-effects except possibly some caching in
    * case there is a match.
    *
    * <p>If this method throws any exception, the exception is logged as an
    * ignorable exception and <code>false</code> is assumed.
    *
    * <p>This method should only be called by the XINS/Java Server Framework.
    *
    * @param httpRequest
    *    the HTTP request to investigate, never <code>null</code>.
    *
    * @return
    *    <code>true</code> if this calling convention is <em>possibly</em>
    *    able to handle this request, or <code>false</code> if it is
    *    <em>definitely</em> not able to handle this request.
    *
    * @throws Exception
    *    if analysis of the request causes an exception; in this case
    *    <code>false</code> will be assumed by the framework.
    *
    * @since XINS 1.4.0
    */
   protected abstract boolean matches(HttpServletRequest httpRequest)
   throws Exception;

   /**
    * Converts an HTTP request to a XINS request (wrapper method). This method
    * checks the arguments, checks that the HTTP method is actually supported,
    * calls the implementation method and then checks the return value from
    * that method.
    *
    * @param httpRequest
    *    the HTTP request, cannot be <code>null</code>.
    *
    * @return
    *    the XINS request object, never <code>null</code>.
    *
    * @throws IllegalStateException
    *    if this calling convention is currently not usable, see
    *    {@link Manageable#assertUsable()}.
    *
    * @throws IllegalArgumentException
    *    if <code>httpRequest == null</code>.
    *
    * @throws InvalidRequestException
    *    if the request is considerd to be invalid, at least for this calling
    *    convention; either because the HTTP method is not supported, or
    *    because {@link #convertRequestImpl(HttpServletRequest)} indicates so.
    *
    * @throws FunctionNotSpecifiedException
    *    if the request does not indicate the name of the function to execute.
    */
   final FunctionRequest convertRequest(HttpServletRequest httpRequest)
   throws IllegalStateException,
          IllegalArgumentException,
          InvalidRequestException,
          FunctionNotSpecifiedException {

      // Make sure the current state is okay
      assertUsable();

      // Check preconditions
      MandatoryArgumentChecker.check("httpRequest", httpRequest);

      // Delegate to the implementation method
      FunctionRequest xinsRequest;
      try {
         xinsRequest = convertRequestImpl(httpRequest);

      // Filter any thrown exceptions
      } catch (Throwable exception) {
         if (exception instanceof InvalidRequestException) {
            throw (InvalidRequestException) exception;
         } else if (exception instanceof FunctionNotSpecifiedException) {
            throw (FunctionNotSpecifiedException) exception;
         } else {
            String thisMethod    = "convertRequest("
                                 + HttpServletRequest.class.getName()
                                 + ')';
            String subjectClass  = getClass().getName();
            String subjectMethod = "convertRequestImpl("
                                 + HttpServletRequest.class.getName()
                                 + ')';

            String detail = null;

            throw Utils.logProgrammingError(CLASSNAME,    thisMethod,
                                            subjectClass, subjectMethod,
                                            detail,       exception);
         }
      }

      // Make sure the returned value is not null
      if (xinsRequest == null) {
         String thisMethod    = "convertRequest("
                              + HttpServletRequest.class.getName()
                              + ')';
         String subjectClass  = getClass().getName();
         String subjectMethod = "convertRequestImpl("
                              + HttpServletRequest.class.getName()
                              + ')';
         String detail = "Method returned null.";
         throw Utils.logProgrammingError(CLASSNAME,    thisMethod,
                                         subjectClass, subjectMethod,
                                         detail);
      }

      return xinsRequest;
   }

   /**
    * Converts an HTTP request to a XINS request (implementation method). This
    * method should only be called from the XINS/Java Server Framework self.
    * Then it is guaranteed that:
    * <ul>
    *    <li>the state is usable;
    *    <li>the <code>httpRequest</code> argument is not <code>null</code>;
    *    <li>the HTTP method is in the set of supported methods, as indicated
    *        by {@link #getSupportedMethods()}.
    * </ul>
    *
    * <p>Note that {@link #getSupportedMethods(HttpServletRequest)} will not
    * have been called prior to this method call.
    *
    * @param httpRequest
    *    the HTTP request.
    *
    * @return
    *    the XINS request object, should not be <code>null</code>.
    *
    * @throws InvalidRequestException
    *    if the request is considerd to be invalid.
    *
    * @throws FunctionNotSpecifiedException
    *    if the request does not indicate the name of the function to execute.
    */
   protected abstract FunctionRequest convertRequestImpl(HttpServletRequest httpRequest)
   throws InvalidRequestException,
          FunctionNotSpecifiedException;

   /**
    * Converts a XINS result to an HTTP response (wrapper method). This method
    * checks the arguments, then calls the implementation method and then
    * checks the return value from that method.
    *
    * <p>Note that this method is not called if there is an error while
    * converting the request.
    *
    * @param xinsResult
    *    the XINS result object that should be converted to an HTTP response,
    *    cannot be <code>null</code>.
    *
    * @param httpResponse
    *    the HTTP response object to configure, cannot be <code>null</code>.
    *
    * @param httpRequest
    *    the HTTP request, cannot be <code>null</code>.
    *
    * @throws IllegalStateException
    *    if this calling convention is currently not usable, see
    *    {@link Manageable#assertUsable()}.
    *
    * @throws IllegalArgumentException
    *    if <code>xinsResult   == null
    *          || httpResponse == null
    *          || httpRequest  == null</code>.
    *
    * @throws IOException
    *    if the invocation of any of the methods in either
    *    <code>httpResponse</code> or <code>httpRequest</code> caused an I/O
    *    error.
    */
   final void convertResult(FunctionResult      xinsResult,
                            HttpServletResponse httpResponse,
                            HttpServletRequest  httpRequest)
   throws IllegalStateException,
          IllegalArgumentException,
          IOException {

      // Make sure the current state is okay
      assertUsable();

      // Check preconditions
      MandatoryArgumentChecker.check("xinsResult",   xinsResult,
                                     "httpResponse", httpResponse,
                                     "httpRequest",  httpRequest);

      // By default, all calling conventions return the same "Server" header.
      // This can be overridden in the convertResultImpl() method.
      httpResponse.addHeader("Server", SERVER_HEADER);

      // Delegate to the implementation method
      try {
         convertResultImpl(xinsResult, httpResponse, httpRequest);

      // Filter any thrown exceptions
      } catch (Throwable exception) {
         if (exception instanceof IOException) {
            Log.log_3506(exception, getClass().getName());
            throw (IOException) exception;
         } else {
            String thisMethod    = "convertResult("
                                 + FunctionResult.class.getName()
                                 + ','
                                 + HttpServletResponse.class.getName()
                                 + ','
                                 + HttpServletRequest.class.getName()
                                 + ')';
            String subjectClass  = getClass().getName();
            String subjectMethod = "convertResultImpl("
                                 + HttpServletRequest.class.getName()
                                 + ')';

            throw Utils.logProgrammingError(CLASSNAME,    thisMethod,
                                            subjectClass, subjectMethod,
                                            null,         exception);
         }
      }
   }

   /**
    * Converts a XINS result to an HTTP response (implementation method). This
    * method should only be called from the XINS/Java Server Framework self.
    * Then it is guaranteed that none of the arguments is <code>null</code>.
    *
    * @param xinsResult
    *    the XINS result object that should be converted to an HTTP response,
    *    will not be <code>null</code>.
    *
    * @param httpResponse
    *    the HTTP response object to configure.
    *
    * @param httpRequest
    *    the HTTP request.
    *
    * @throws IOException
    *    if the invocation of any of the methods in either
    *    <code>httpResponse</code> or <code>httpRequest</code> caused an I/O
    *    error.
    */
   protected abstract void convertResultImpl(FunctionResult      xinsResult,
                                             HttpServletResponse httpResponse,
                                             HttpServletRequest  httpRequest)
   throws IOException;
   // XXX: Replace IOException with more appropriate exception?

   /**
    * Parses XML from the specified HTTP request and checks that the content
    * type is correct.
    *
    * <p>This method uses a cache to optimize performance if either of the
    * <code>parseXMLRequest</code> methods is called multiple times for the
    * same request.
    *
    * <p>Calling this method is equivalent with calling
    * {@link #parseXMLRequest(HttpServletRequest,boolean)} with the
    * <code>checkType</code> argument set to <code>true</code>.
    *
    * @param httpRequest
    *    the HTTP request, cannot be <code>null</code>.
    *
    * @return
    *    the parsed element, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>httpRequest == null</code>.
    *
    * @throws InvalidRequestException
    *    if the HTTP request cannot be read or cannot be parsed correctly.
    *
    * @since XINS 1.4.0
    */
   protected Element parseXMLRequest(HttpServletRequest httpRequest)
   throws IllegalArgumentException, InvalidRequestException {
      return parseXMLRequest(httpRequest, true);
   }

   /**
    * Parses XML from the specified HTTP request and optionally checks that
    * the content type is correct.
    *
    * <p>Since XINS 1.4.0, this method uses a cache to optimize performance if
    * either of the <code>parseXMLRequest</code> methods is called multiple
    * times for the same request.
    *
    * @param httpRequest
    *    the HTTP request, cannot be <code>null</code>.
    *
    * @param checkType
    *    flag indicating whether this method should check that the content
    *    type of the request is <em>text/xml</em>.
    *
    * @return
    *    the parsed element, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>httpRequest == null</code>.
    *
    * @throws InvalidRequestException
    *    if the HTTP request cannot be read or cannot be parsed correctly.
    *
    * @since XINS 1.3.0
    */
   protected Element parseXMLRequest(HttpServletRequest httpRequest,
                                     boolean            checkType)
   throws IllegalArgumentException, InvalidRequestException {

      // Check arguments
      MandatoryArgumentChecker.check("httpRequest", httpRequest);

      // Determine if the request matches the cached request and the parsed
      // XML is already cached
      Object cached = null;
      if (_cachedRequest.get() == httpRequest) {
         cached = _cachedRequestXML.get();
      }

      // Cache miss
      if (cached == null) {
         Log.log_3512();

      // Cache hit
      } else {
         Log.log_3513();
         return (Element) cached;
      }

      // Always first check the content type, even if checking is enabled. We
      // do this because the parsed request will only be stored if the content
      // type was OK.
      String contentType = httpRequest.getContentType();
      String errorMessage = null;
      if (contentType == null || contentType.trim().length() < 1) {
         errorMessage = "No content type set.";
      } else {
         String contentTypeLC = contentType.toLowerCase();
         if (! ("text/xml".equals(contentTypeLC) ||
                contentTypeLC.startsWith("text/xml;"))) {
            errorMessage = "Invalid content type \""
                         + contentType
                         + "\". Expected \"text/xml\" (case-insensitive) or a variant of it.";
         }
      }

      // The content-type check was unsuccessful
      if (errorMessage != null) {

         // Log: Not caching XML since the content type is not "text/xml"
         Log.log_3515();

         // If checking is enabled
         if (checkType) {
            throw new InvalidRequestException(errorMessage);
         }
      }

      // Parse the content in the HTTP request
      ElementParser parser = new ElementParser();
      Element element;
      try {
         element = parser.parse(httpRequest.getReader());

      // I/O error
      } catch (IOException ex) {
         String message = "Failed to read XML request.";
         throw new InvalidRequestException(message, ex);

      // Parsing error
      } catch (ParseException ex) {
         String message = "Failed to parse XML request.";
         throw new InvalidRequestException(message, ex);
      }

      // Only store in the cache if the content type was OK
      if (errorMessage == null) {
         _cachedRequestXML.set(null);
         _cachedRequest.set(httpRequest);
         _cachedRequestXML.set(element);
         Log.log_3514();
      }

      return element;
   }

   /**
    * Gathers all parameters from the specified request. The parameters are
    * returned as a {@link ProtectedPropertyReader} instance with the
    * specified secret key. If no parameters are found, then <code>null</code>
    * is returned.
    *
    * <p>If a parameter is found to have multiple values, then an
    * {@link InvalidRequestException} is thrown.
    *
    * @param httpRequest
    *    the HTTP request to get the parameters from, cannot be
    *    <code>null</code>.
    *
    * @param secretKey
    *    the secret key to use if and when constructing the
    *    {@link ProtectedPropertyReader} instance, should not be
    *    <code>null</code>.
    *
    * @return
    *    the properties found, or <code>null</code> if none were found.
    *
    * @throws NullPointerException
    *    if <code>httpRequest == null</code>.
    *
    * @throws InvalidRequestException
    *    if a parameter is found that has multiple values.
    */
   final ProtectedPropertyReader gatherParams(HttpServletRequest httpRequest,
                                              Object             secretKey)
   throws NullPointerException, InvalidRequestException {

      // Get the parameters from the HTTP request
      Enumeration params = httpRequest.getParameterNames();

      // The property set to return from this method
      ProtectedPropertyReader pr;

      // If there are no parameters, then return null
      if (! params.hasMoreElements()) {
         pr = null;

      // There seem to be some parameters
      } else {
         pr = new ProtectedPropertyReader(secretKey);

         do {
            // Get the parameter name
            String name = (String) params.nextElement();

            // Get all parameter values (can be multiple)
            String[] values = httpRequest.getParameterValues(name);

            // Be gentle, allow nulls and zero-sized arrays
            if (values != null && values.length != 0) {

               // Get the parameter value, allowing duplicate values, but not
               // different ones; this may throw an InvalidRequestException
               String value = getParamValue(name, values);

               // Associate the name with the one and only value
               pr.set(secretKey, name, value);
            }
         } while (params.hasMoreElements());
      }
      return pr;
   }

   /**
    * Determines a single value for a parameter based on an array of values.
    * If there is only one value, then that value is returned. If there are
    * multiple equal values, then the value is returned as well. However, if
    * there are multiple values and at least one of them is different, then an
    * {@link InvalidRequestException} is thrown.
    *
    * @param name
    *    the name of the parameter, only used when throwing an
    *    {@link InvalidRequestException}, should not be <code>null</code>.
    *
    * @param values
    *    the values, should not be <code>null</code> and should not have a
    *    size of zero.
    *
    * @return
    *    the single value of the parameter, if any.
    *
    * @throws NullPointerException
    *    if <code>values == null || values[<em>n</em>] == null</code>, where
    *    <code>0 &lt;= <em>n</em> &lt; values.length</code>.
    *
    * @throws IndexOutOfBoundsException
    *    if <code>values.length &lt; 1</code>.
    *
    * @throws InvalidRequestException
    *    if the parameter is found to have multiple different values.
    */
   private final String getParamValue(String name, String[] values)
   throws NullPointerException,
          IndexOutOfBoundsException,
          InvalidRequestException {

      // XXX: Should we avoid the NullPointerException ?

      String value = values[0];

      // We only need to do crunching if there is more than one value
      if (values.length > 1) {
         for (int i = 1; i < values.length; i++) {
            String other = values[i];
            if (! value.equals(other)) {
               String error = "Found multiple values for the parameter "
                            + "named \""
                            + name
                            + "\".";
               throw new InvalidRequestException(error);
            }
         }
      }

      return value;
   }
}
