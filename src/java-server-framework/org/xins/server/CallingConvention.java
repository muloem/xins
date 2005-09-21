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


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------


   /**
    * Removes all parameters that should not be passed to a function. If the
    * set of parameters passed is <code>null</code>, then nothing is done.
    *
    * @param parameters
    *    the {@link ProtectedPropertyReader} containing the set of parameters
    *    to investigate, or <code>null</code>.
    *
    * @param secretKey
    *    the secret key required to be able to modify the parameters, can be
    *    <code>null</code>.
    */
   void cleanUpParameters(ProtectedPropertyReader parameters,
                          Object                  secretKey) {

      // TODO: Should we not let the diagnostic context ID through?

      // If the set of parameters passed is null, then nothing is done.
      if (parameters == null) {
         return;
      }

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
   throws IllegalArgumentException,
          InvalidRequestException,
          FunctionNotSpecifiedException {

      final String THIS_METHOD = "convertRequest("
                               + HttpServletRequest.class.getName()
                               + ')';

      // Check preconditions
      MandatoryArgumentChecker.check("httpRequest", httpRequest);

      final String SUBJECT_CLASS  = getClass().getName(); // XXX: Cache?
      final String SUBJECT_METHOD = "convertRequestImpl("
                                  + HttpServletRequest.class.getName()
                                  + ')'; // XXX: Cache?

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
            throw Utils.logProgrammingError(CLASSNAME,
                                            THIS_METHOD,
                                            SUBJECT_CLASS,
                                            SUBJECT_METHOD,
                                            null,
                                            t);
         }
      }

      // Make sure the returned value is not null
      if (xinsRequest == null) {
         final String DETAIL = "Method returned null.";
         throw Utils.logProgrammingError(CLASSNAME,
                                         THIS_METHOD,
                                         SUBJECT_CLASS,
                                         SUBJECT_METHOD,
                                         DETAIL);
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
   protected abstract FunctionRequest convertRequestImpl(HttpServletRequest httpRequest)
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
    * @throws IllegalArgumentException
    *    if <code>xinsResult == null || httpResponse == null || httpRequest == null</code>.
    *
    * @throws IOException
    *    if calling any of the methods in <code>httpResponse</code> causes an
    *    I/O error.
    */
   final void convertResult(FunctionResult      xinsResult,
                            HttpServletResponse httpResponse,
                            HttpServletRequest  httpRequest)
   throws IllegalArgumentException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("xinsResult",   xinsResult,
                                     "httpResponse", httpResponse,
                                     "httpRequest",  httpRequest);

      // By default, all calling convention return the same Server header.
      // This can be overridden in the convertResultImpl() method.
      httpResponse.addHeader("Server", "XINS/Java Server Framework " + Library.getVersion());

      // Delegate to the implementation method
      try {
         convertResultImpl(xinsResult, httpResponse, httpRequest);

      // Filter any thrown exceptions
      } catch (Throwable exception) {
         if (exception instanceof IOException) {
            throw (IOException) exception;
         } else {
            final String THIS_METHOD    = "convertResult("
                                        + FunctionResult.class.getName()
                                        + ','
                                        + HttpServletResponse.class.getName()
                                        + ','
                                        + HttpServletRequest.class.getName()
                                        + ')';
            final String SUBJECT_CLASS  = getClass().getName();
            final String SUBJECT_METHOD = "convertResultImpl("
                                        + HttpServletRequest.class.getName()
                                        + ')';

            throw Utils.logProgrammingError(CLASSNAME,
                                            THIS_METHOD,
                                            SUBJECT_CLASS,
                                            SUBJECT_METHOD,
                                            null,
                                            exception);
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
   protected Element parseXMLRequest(HttpServletRequest httpRequest, boolean checkType)
   throws InvalidRequestException {

      // Check content type
      String contentType = httpRequest.getContentType();
      if (checkType && (contentType == null || !contentType.startsWith("text/xml"))) {
         throw new InvalidRequestException("Incorrect content type \"" + contentType + "\".");
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
         throw new InvalidRequestException("Cannot read the XML request.", ex);

      // Parsing error
      } catch (ParseException ex) {
         throw new InvalidRequestException("Cannot parse the XML request.", ex);
      }
   }
}
