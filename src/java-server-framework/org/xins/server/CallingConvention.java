/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.CollectionUtils;
import org.xins.common.collections.ProtectedPropertyReader;
import org.xins.common.manageable.Manageable;
import org.xins.common.text.FastStringBuffer;
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
   private static final String SERVER_HEADER;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   static {
      SERVER_HEADER = "XINS/Java Server Framework " + Library.getVersion();
   }

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

      // TODO: Should we not let the diagnostic context ID through?

      // TODO: Improve the performance and memory usage by not always copying
      //       the PropertyReader names to a list

      // Get an list of the parameter names
      ArrayList names = CollectionUtils.list(parameters.getNames());

      // Loop through all parameters
      for (int i = 0; i < names.size(); i++) {

         // Determine parameter name and value
         String name  = (String) names.get(i);
         String value = parameters.get(name);

         // If the name or value is empty, then remove the parameter
         if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)) {
            parameters.set(secretKey, name, null);

         // XXX: If the parameter name is "function", then remove it
         // FIXME: Add this to the TODO list for XINS 2.0.0
         } else if ("function".equals(name)) {
            parameters.set(secretKey, name, null);
         }
      }
   }


   //------------------------------------------------------------------------
   // Constructors
   //------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallingConvention</code>.
    */
   protected CallingConvention() {
      // empty
   }


   //------------------------------------------------------------------------
   // Fields
   //------------------------------------------------------------------------

   //------------------------------------------------------------------------
   // Methods
   //------------------------------------------------------------------------

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
    *    if <code>request == null</code>.
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
      } catch (Throwable t) {
         if (t instanceof InvalidRequestException) {
            throw (InvalidRequestException) t;
         } else if (t instanceof FunctionNotSpecifiedException) {
            throw (FunctionNotSpecifiedException) t;
         } else {
            String thisMethod    = "convertRequest("
                                 + HttpServletRequest.class.getName()
                                 + ')';
            String subjectClass  = getClass().getName();
            String subjectMethod = "convertRequestImpl("
                                 + HttpServletRequest.class.getName()
                                 + ')';

            throw Utils.logProgrammingError(CLASSNAME,
                                            thisMethod,
                                            subjectClass,
                                            subjectMethod,
                                            null,
                                            t);
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
         throw Utils.logProgrammingError(CLASSNAME,
                                         thisMethod,
                                         subjectClass,
                                         subjectMethod,
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
    *    the HTTP request, will not be <code>null</code>.
    *
    * @throws IllegalStateException
    *    if this calling convention is currently not usable, see
    *    {@link Manageable#assertUsable()}.
    *
    * @throws IllegalArgumentException
    *    if <code>xinsResult == null
    *          || httpResponse == null
    *          || httpRequest == null</code>.
    *
    * @throws IOException
    *    if calling any of the methods in <code>httpResponse</code> causes an
    *    I/O error.
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
    * Converts a XINS result to an HTTP response (implementation method).
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
    *    if calling any of the methods in <code>httpResponse</code> causes an
    *    I/O error.
    */
   protected abstract void convertResultImpl(FunctionResult      xinsResult,
                                             HttpServletResponse httpResponse,
                                             HttpServletRequest  httpRequest)
   throws IOException;
   // XXX: Replace IOException with more appropriate exception?

   /**
    * Reads the HTTP request and parses it to an {@link org.xins.common.xml.Element}.
    *
    * @param httpRequest
    *    the HTTP request, cannot be <code>null</code>.
    *
    * @param checkType
    *    flag indicating whether this method should check that the content type
    *    of the request is text/xml.
    *
    * @return
    *    the parsed element, never <code>null</code>.
    *
    * @throws InvalidRequestException
    *    if the HTTP request cannot be read or cannot be parsed correctly.
    */
   protected Element parseXMLRequest(HttpServletRequest httpRequest,
                                     boolean            checkType)
   throws InvalidRequestException {

      // Check content type
      String contentType = httpRequest.getContentType();
      if (checkType && (contentType == null || !contentType.startsWith("text/xml"))) {
         throw new InvalidRequestException("Incorrect content type \"" +
                                           contentType + "\".");
      }

      try {

         // Convert the Reader to a string buffer
         BufferedReader reader = httpRequest.getReader();
         FastStringBuffer content = new FastStringBuffer(1024);
         String nextLine;
         while ((nextLine = reader.readLine()) != null) {
            content.append(nextLine);
            content.append("\n");
         }

         String contentString = content.toString().trim();
         ElementParser parser = new ElementParser();
         Element parsedElem = parser.parse(new StringReader(contentString));
         return parsedElem;

      // I/O error
      } catch (IOException ex) {
         String message = "Failed to read XML request.";
         throw new InvalidRequestException(message, ex);

      // Parsing error
      } catch (ParseException ex) {
         String message = "Failed to parse XML request.";
         throw new InvalidRequestException(message, ex);
      }
   }
}
