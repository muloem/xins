/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.ProtectedPropertyReader;
import org.xins.common.manageable.Manageable;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Abstraction of a calling convention. A calling convention determines how an
 * HTTP request is converted to a XINS request and how a XINS response is
 * converted back to an HTTP response.
 *
 * <p>Calling convention implementations are thread-safe. Hence if a calling
 * convention does not have any configuration parameters per instance, then
 * the <em>Singleton</em> pattern can be applied.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
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
    * the server framework.
    */
   private static final String SERVER_HEADER =
      "XINS/Java Server Framework " + Library.getVersion();

   /**
    * Value that can be used to indicate that it is unknown whether a request
    * can be handled by a certain calling convention. See
    * {@link #matchesRequest(HttpServletRequest)}.
    */
   static final int MATCHING_UNKNOWN = -1;

   /**
    * Value that can be used to indicate that a request cannot be handled by a
    * certain calling convention. See
    * {@link #matchesRequest(HttpServletRequest)}.
    */
   static final int NOT_MATCHING = 0;

   /**
    * Value that can be used to indicate that it is expected that a request
    * can be handled by a certain calling convention. See
    * {@link #matchesRequest(HttpServletRequest)}.
    */
   static final int MATCHING = 1;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Removes all parameters that should not be transmitted. A parameter will
    * be removed if it matches any of the following rules:
    *
    * <ul>
    * <li>parameter name is <code>null</code>;
    * <li>parameter name is empty;
    * <li>parameter name equals <code>"function"</code>.
    * </ul>
    *
    * @param parameters
    *    the {@link ProtectedPropertyReader} containing the set of parameters
    *    to investigate, cannot be <code>null</code>.
    *
    * @param secretKey
    *    the secret key required to be able to modify the parameters, cannot
    *    be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>parameters == null || secretKey == null</code>.
    */
   void cleanUpParameters(ProtectedPropertyReader parameters,
                          Object                  secretKey)
   throws IllegalArgumentException {

      // Check arguments
      MandatoryArgumentChecker.check("parameters", parameters,
                                     "secretKey",  secretKey);

      // Get the parameter names
      Iterator names = parameters.getNames();

      // Loop through all parameters
      ArrayList toRemove = null;
      while (names.hasNext()) {

         // Determine parameter name and value
         String name  = (String) names.next();
         String value = parameters.get(name);

         // If the parameter name or value is empty, or if the name is
         // "function", then mark the parameter as 'to be removed'
         if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value) ||
             "function".equals(name)) {
            if (toRemove == null) {
               toRemove = new ArrayList();
            }
            toRemove.add(name);
         }
      }

      // If there is anything to remove, then do so
      if (toRemove != null) {
         for (int i = (toRemove.size() - 1); i >= 0; i--) {
            String name = (String) toRemove.get(i);
            parameters.set(secretKey, name, null);
         }
      }
   }


   //------------------------------------------------------------------------
   // Constructors
   //------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallingConvention</code>, indicating whether it
    * should be considered deprecated.
    *
    * @param deprecated
    *    <code>true</code> if this calling convention is to be considered
    *    deprecated, or <code>false</code> if not.
    */
   protected CallingConvention(boolean deprecated) {
      _deprecated       = deprecated;
      _cachedRequest    = new ThreadLocal();
      _cachedRequestXML = new ThreadLocal();
   }


   //------------------------------------------------------------------------
   // Fields
   //------------------------------------------------------------------------

   /**
    * Flag that indicates whether this calling convention is deprecated.
    */
   private final boolean _deprecated;

   /**
    * Cached <code>HttpServletRequest</code>, local per thread. When
    * any of the <code>parseXMLRequest</code> methods is called, the request
    * is stored in this field, to be able to confirm later that
    * {@link #_cachedRequestXML} should be returned.
    *
    * <p>The value inside this {@link ThreadLocal} is always either
    * <code>null</code> or otherwise an instance of a class that implements
    * {@link HttpServletRequest}.
    */
   private final ThreadLocal _cachedRequest;

   /**
    * Cached XML <code>Element</code>, local per thread. When
    * any of the <code>parseXMLRequest</code> methods is called, the result is
    * stored in this field before returning it.
    *
    * <p>The value inside this {@link ThreadLocal} is always either
    * <code>null</code> or otherwise an instance of class {@link Element}.
    */
   private final ThreadLocal _cachedRequestXML;


   //------------------------------------------------------------------------
   // Methods
   //------------------------------------------------------------------------

   /**
    * Checks if this calling convention is deprecated.
    *
    * <p>A deprecated calling convention will always get a lower priority that
    * non-deprecated ones in situations where a request does not explicitly
    * specify a calling convention and it is attempted to automatically choose
    * one to use.
    *
    * @return
    *    <code>true</code> if this calling convention is deprecated,
    *    <code>false</code> otherwise.
    *
    * @since XINS 1.4.0
    */
   public final boolean isDeprecated() {
      return _deprecated;
   } 

   /**
    * Checks if the specified request can be handled by this calling
    * convention.
    *
    * <p>The return value is as follows:
    *
    * <ul>
    *    <li>a positive value indicates that the request <em>can</em>
    *        be handled;
    *    <li>the value <code>0</code> indicates that the request
    *        <em>cannot</em> be handled;
    *    <li>a negative number indicates that it is <em>unknown</em>
    *        whether the request can be handled by this calling convention.
    * </ul>
    *
    * <p>This method should not throw any exception.
    *
    * @param httpRequest
    *    the HTTP request to investigate, cannot be <code>null</code>.
    *
    * @return
    *    a positive value if the request can be handled; <code>0</code> if the
    *    request cannot be handled or a negative value if it is unknown.
    */
   abstract int matchesRequest(HttpServletRequest httpRequest);

   /**
    * Converts an HTTP request to a XINS request (wrapper method). This method
    * checks the arguments, then calls the implementation method and then
    * checks the return value from that method.
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
    *    if the request is considerd to be invalid.
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
    * method should only be called from class {@link CallingConvention}. Only
    * then it is guaranteed that the <code>httpRequest</code> argument is not
    * <code>null</code>.
    *
    * @param httpRequest
    *    the HTTP request, will not be <code>null</code>.
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
   protected abstract FunctionRequest convertRequestImpl(
      HttpServletRequest httpRequest)
   throws InvalidRequestException,
          FunctionNotSpecifiedException;

   /**
    * Converts a XINS result to an HTTP response (wrapper method). This method
    * checks the arguments, then calls the implementation method and then
    * checks the return value from that method.
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
    * method should only be called from class {@link CallingConvention}. Only
    * then it is guaranteed that none of the arguments is <code>null</code>.
    *
    * @param xinsResult
    *    the XINS result object that should be converted to an HTTP response,
    *    will not be <code>null</code>.
    *
    * @param httpResponse
    *    the HTTP response object to configure, will not be <code>null</code>.
    *
    * @param httpRequest
    *    the HTTP request, will not be <code>null</code>.
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
    * this method is called multiple times for the same request.
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
    */
   protected Element parseXMLRequest(HttpServletRequest httpRequest,
                                     boolean            checkType)
   throws IllegalArgumentException, InvalidRequestException {

      // Check arguments
      MandatoryArgumentChecker.check("httpRequest", httpRequest);

      // Determine if the request matches the cached request and the parsed
      // XML is already cached
      if (_cachedRequest.get() == httpRequest) {
         Object cached = _cachedRequestXML.get();
         if (cached != null) {
System.err.println("Found request XML in cache."); // FIXME TODO
            return (Element) cached;
         }
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
                         + "\" Expected \"text/xml\".";
         }
      }

      // If checking is enabled and the check was unsuccessful, then fail
      if (errorMessage != null && checkType) {
         throw new InvalidRequestException(errorMessage);
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
         _cachedRequest.set(httpRequest);
         _cachedRequestXML.set(element);
      }

      return element;
   }
}
