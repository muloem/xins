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
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
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
            // TODO: Log
            String actualClass = getClass().getName(); // XXX: Cache
            throw new ProgrammingError(actualClass + ".convertRequestImpl(HttpServletRequest) has thrown an unexpected " + t.getClass().getName() + '.', t);
         }
      }

      // Make sure the returned value is not null
      if (xinsRequest == null) {
         // TODO: Log
         String actualClass = getClass().getName(); // XXX: Cache
         throw new ProgrammingError(actualClass + ".convertRequestImpl(HttpServletRequest) returned null.");
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
    * Converts a XINS result to an HTTP response.
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
    */
   abstract void convertResult(FunctionResult      xinsResult,
                               HttpServletResponse httpResponse)
   throws IOException;
   // TODO: Use "Wrapper/Implementation Method" pattern
   // XXX: Replace IOException with more appropriate exception?
}
