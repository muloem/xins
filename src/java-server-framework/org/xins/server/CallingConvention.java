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
 *
 * @since XINS 1.1.0
 */
public interface CallingConvention {
   
   /**
    * Converts an HTTP request to a XINS request.
    *
    * @param request
    *    the HTTP request, cannot be <code>null</code>.
    *
    * @return
    *    the XINS request, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    *
    * @throws ParseException
    *    if the request is considerd to be invalid.
    */
   FunctionRequest getFunctionRequest(HttpServletRequest request)
   throws IllegalArgumentException, ParseException;
   // TODO: Distinguish situation where function is not specified
   // TODO: Replace ParseException with more appropriate exception
   
   /**
    * Converts a XINS response to an HTTP response.
    *
    * @param response
    *    the HTTP response object to configure, cannot be <code>null</code>.
    *
    * @param result
    *    the XINS result object that should be converted to an HTTP response,
    *    cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>response == null || result == null</code>.
    */
   void handleResult(HttpServletResponse response, FunctionResult result)
   throws IOException;
   // TODO: Inverse the order of the arguments
   // TODO: Improve the naming of the arguments
   // TODO: Replace IOException with more appropriate exception
}
