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
import org.xins.common.servlet.ServletRequestPropertyReader;
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
 *
 * @since XINS 1.1.0
 */
public final class StandardCallingConvention
extends CallingConvention {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The response encoding format.
    */
   public static final String RESPONSE_ENCODING = "UTF-8";

   /**
    * The content type of the HTTP response.
    */
   public static final String RESPONSE_CONTENT_TYPE = "text/xml;charset=" + RESPONSE_ENCODING;

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

   public FunctionRequest convertRequest(HttpServletRequest httpRequest)
   throws IllegalArgumentException,
          InvalidRequestException,
          FunctionNotSpecifiedException {

      // Check preconditions
      MandatoryArgumentChecker.check("httpRequest", httpRequest);

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
      final String ENCODING = "UTF-8";
      if (dataSectionValue != null && dataSectionValue.length() > 0) {
         try {
            ElementParser parser = new ElementParser();
            dataElement = parser.parse(dataSectionValue.getBytes(ENCODING));
         } catch (UnsupportedEncodingException ex) {
            // TODO: Log
            throw new ProgrammingError("Encoding \"" + ENCODING + "\" is not supported.");
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

   public void convertResult(FunctionResult      xinsResult,
                             HttpServletResponse httpResponse)
   throws IllegalArgumentException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("xinsResult",   xinsResult,
                                     "httpResponse", httpResponse);

      // Send the XML output to the stream and flush
      PrintWriter out = httpResponse.getWriter();
      // TODO: OutputStream out = httpResponse.getOutputStream();
      httpResponse.setContentType(RESPONSE_CONTENT_TYPE);
      httpResponse.setStatus(HttpServletResponse.SC_OK);
      CallResultOutputter.output(out, RESPONSE_ENCODING, xinsResult, false);
      out.close();
   }
}
