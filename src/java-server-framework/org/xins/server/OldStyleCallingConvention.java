/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.ProtectedPropertyReader;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;

/**
 * Calling convention that aims to be compatible with old, pre-1.0, XINS
 * releases. The output is especially aimed to be fully compatible with XINS
 * 0.168.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
final class OldStyleCallingConvention
extends CallingConvention {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The response encoding format.
    */
   static final String RESPONSE_ENCODING = "UTF-8";

   /**
    * The content type of the HTTP response.
    */
   static final String RESPONSE_CONTENT_TYPE = "text/xml;charset=" + RESPONSE_ENCODING;

   /**
    * Secret key used when accessing <code>ProtectedPropertyReader</code>
    * objects.
    */
   private static final Object SECRET_KEY = new Object();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>OldStyleCallingConvention</code> object.
    */
   OldStyleCallingConvention() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

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
    *    the XINS request object, never <code>null</code>.
    *
    * @throws InvalidRequestException
    *    if the request is considerd to be invalid.
    *
    * @throws FunctionNotSpecifiedException
    *    if the request does not indicate the name of the function to execute.
    */
   protected FunctionRequest convertRequestImpl(HttpServletRequest httpRequest)
   throws InvalidRequestException,
          FunctionNotSpecifiedException {

      // XXX: What if invalid URL, e.g. query string ends with percent sign?

      // Determine function name
      String functionName = determineFunction(httpRequest.getParameter("_function"),
         httpRequest.getParameter("function"));

      // Determine function parameters
      ProtectedPropertyReader functionParams = new ProtectedPropertyReader(SECRET_KEY);
      Enumeration params = httpRequest.getParameterNames();
      while (params.hasMoreElements()) {
         String name = (String) params.nextElement();
         String value = httpRequest.getParameter(name);
         functionParams.set(SECRET_KEY, name, value);
      }

      // Remove all invalid parameters
      cleanUpParameters(functionParams, SECRET_KEY);

      // Construct and return the request object
      return new FunctionRequest(functionName, functionParams, null);
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
   protected void convertResultImpl(FunctionResult      xinsResult,
                                    HttpServletResponse httpResponse)
   throws IOException {

      // Send the XML output to the stream and flush
      PrintWriter out = httpResponse.getWriter();
      // TODO: OutputStream out = httpResponse.getOutputStream();
      httpResponse.setContentType(RESPONSE_CONTENT_TYPE);
      httpResponse.setStatus(HttpServletResponse.SC_OK);
      CallResultOutputter.output(out, RESPONSE_ENCODING, xinsResult, true);
      out.close();
   }

   /**
    * Determines the name of the function to be called based on the specified
    * values for the parameters <code>"_function"</code> and
    * <code>"function"</code>. If the function is not specified at all, then a
    * {@link FunctionNotSpecifiedException} is thrown.
    *
    * @param withUnderScore
    *    the value of the parameter <code>"_function"</code>.
    *
    * @param withoutUnderScore
    *    the value of the parameter <code>"function"</code>.
    *
    * @return
    *    the name of the function, never <code>null</code>.
    *
    * @throws FunctionNotSpecifiedException
    *    if the function name is not specified in either of the parameters.
    *
    * @throws InvalidRequestException
    *    if both the parameter <code>"_function"</code> and the parameter
    *    <code>"function"</code> are specified, but they have different
    *    values.
    */
   static String determineFunction(String withUnderScore,
                                   String withoutUnderScore)
   throws FunctionNotSpecifiedException, InvalidRequestException {

      String functionName;

      // Function name is not specified
      if (TextUtils.isEmpty(withUnderScore)
      && TextUtils.isEmpty(withoutUnderScore)) {

         throw new FunctionNotSpecifiedException();

      // Only "function" is specified
      } else if (TextUtils.isEmpty(withUnderScore)) {
         functionName = withoutUnderScore;

      // Only "_function" is specified
      } else if (TextUtils.isEmpty(withoutUnderScore)) {
         functionName = withUnderScore;

      // Both "function" and "_function" are specified, and they are equal
      } else if (withUnderScore.equals(withoutUnderScore)) {
         functionName = withUnderScore;

      // Both "function" and "_function" are specified, but they are different
      } else {
         final String DETAIL = "_function="
                             + TextUtils.quote(withUnderScore)
                             + "; function="
                             + TextUtils.quote(withoutUnderScore);
         throw new InvalidRequestException(DETAIL, null);
      }

      return functionName;
   }
}
