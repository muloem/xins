/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.ProgrammingError;
import org.xins.common.collections.ProtectedPropertyReader;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Standard calling convention.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
final class StandardCallingConvention
extends CallingConvention {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The enconding for the data section
    */
   static final String DATA_ENCODING = "UTF-8";

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
    * Constructs a new <code>StandardCallingConvention</code> object.
    */
   StandardCallingConvention() {
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
      String functionName = httpRequest.getParameter("_function");
      if (TextUtils.isEmpty(functionName)) {
         functionName = httpRequest.getParameter("function");
         if (TextUtils.isEmpty(functionName)) {
            throw new FunctionNotSpecifiedException();
         }
      }

      // Get data section
      String dataSectionValue = httpRequest.getParameter("_data");
      Element dataElement;
      if (dataSectionValue != null && dataSectionValue.length() > 0) {
         try {
            ElementParser parser = new ElementParser();
            dataElement = parser.parse(dataSectionValue.getBytes(DATA_ENCODING));
         } catch (UnsupportedEncodingException ex) {
            final String DETAIL = "Encoding \"" + DATA_ENCODING + "\" is not supported.";
            Log.log_3050(getClass().getName(), "convertRequestImpl(HttpServletRequest)", DETAIL);
            throw new ProgrammingError(DETAIL);
            // TODO: Log everything, as in 1050
         } catch (ParseException ex) {
            throw new InvalidRequestException("Cannot parse the data section.", ex);
         }
      } else {
         dataElement = null;
      }

      // Determine function parameters
      ProtectedPropertyReader functionParams = new ProtectedPropertyReader(SECRET_KEY);
      Enumeration params = httpRequest.getParameterNames();
      while (params.hasMoreElements()) {
         String name = (String) params.nextElement();

         // XXX: If parameter "function" contained function name, then do not
         //      pass it down. This should be changed in the future.
         if ("function".equals(name) && TextUtils.isEmpty(httpRequest.getParameter("_function"))) {
            // ignore

         // Pass parameters if the name is not empty and does not start with
         // an underscore
         } else if (! TextUtils.isEmpty(name) && name.charAt(0) != '_') {
            String value = httpRequest.getParameter(name);
            if (! TextUtils.isEmpty(value)) {
               functionParams.set(SECRET_KEY, name, value);
            }
         }
         // TODO: Decide: Just ignore invalid parameter names?
      }

      return new FunctionRequest(functionName, functionParams, dataElement);
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
      CallResultOutputter.output(out, RESPONSE_ENCODING, xinsResult, false);
      out.close();
   }
}
