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
 * The calling convention interface.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public interface CallingConvention {
   
   /**
    * Gets the function request based on the query passed to the Servlet.
    *
    * @param request
    *    the Servlet request.
    *
    * @throws ParseException
    *    a ParseException is thrown if the request is not in the expected format.
    */
   FunctionRequest getFunctionRequest(HttpServletRequest request) throws ParseException;
   
   /**
    * Returns the function result in the expected format.
    *
    * @param response
    *    the Servlet response where the response should be send.
    * @param result
    *    the function result.
    */
   void handleResult(HttpServletResponse response, FunctionResult result) throws IOException;
   
}
