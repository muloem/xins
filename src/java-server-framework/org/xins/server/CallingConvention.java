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

import org.xins.common.text.ParseException;

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
    * Converts an HTTP request to a XINS request.
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
    * @throws ParseException
    *    if the request is considerd to be invalid.
    */
   abstract FunctionRequest convertRequest(HttpServletRequest httpRequest)
   throws IllegalArgumentException,
          InvalidRequestException,
          FunctionNotSpecifiedException;
   // TODO: Use "Wrapper/Implementation Method" pattern
   
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
