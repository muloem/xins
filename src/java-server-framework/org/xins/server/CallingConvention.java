/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.ProgrammingError;

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
abstract class CallingConvention
extends Object {

   //------------------------------------------------------------------------
   // Class fields
   //------------------------------------------------------------------------

   //------------------------------------------------------------------------
   // Class functions
   //------------------------------------------------------------------------

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
            Log.log_3052(t, SUBJECT_CLASS, SUBJECT_METHOD);
            // FIXME: Log everything like in 1052
            throw new ProgrammingError(SUBJECT_CLASS + '.' + SUBJECT_METHOD + " has thrown an unexpected " + t.getClass().getName() + '.', t);
         }
      }

      // Make sure the returned value is not null
      if (xinsRequest == null) {
         Log.log_3050(SUBJECT_CLASS, METHOD_NAME, "Method returned null.");
         throw new ProgrammingError(SUBJECT_CLASS + '.' + METHOD_NAME + " returned null.");
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
    * @throws IllegalArgumentException
    *    if <code>xinsResult == null || httpResponse == null</code>.
    *
    * @throws IOException
    *    if calling any of the methods in <code>httpResponse</code> causes an
    *    I/O error.
    */
   final void convertResult(FunctionResult      xinsResult,
                            HttpServletResponse httpResponse)
   throws IllegalArgumentException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("xinsResult",   xinsResult,
                                     "httpResponse", httpResponse);

      final String SUBJECT_CLASS  = getClass().getName(); // XXX: Cache?
      final String SUBJECT_METHOD = "convertResultImpl("
                                  + HttpServletRequest.class.getName()
                                  + ')'; // XXX: Cache?

      // Delegate to the implementation method
      try {
         convertResultImpl(xinsResult, httpResponse);

      // Filter any thrown exceptions
      } catch (Throwable t) {
         if (t instanceof IOException) {
            throw (IOException) t;
         } else {
            Log.log_3052(t, SUBJECT_CLASS, SUBJECT_METHOD);
            throw new ProgrammingError(SUBJECT_CLASS + '.' + METHOD_NAME + " has thrown an unexpected " + t.getClass().getName() + '.', t);
            // TODO: Log everything as in 1052
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
    * @throws IOException
    *    if calling any of the methods in <code>httpResponse</code> causes an
    *    I/O error.
    */
   protected abstract void convertResultImpl(FunctionResult      xinsResult,
                                             HttpServletResponse httpResponse)
   throws IOException;
   // XXX: Replace IOException with more appropriate exception?
}
