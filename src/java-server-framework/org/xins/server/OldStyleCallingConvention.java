/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
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
import org.xins.common.servlet.ServletRequestPropertyReader;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Calling convention that aims to be compatible with old, pre-1.0, XINS
 * releases. The output is especially aimed to be fully compatible with XINS
 * 0.168.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
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

      // Determine function parameters
      ProtectedPropertyReader functionParams = new ProtectedPropertyReader(SECRET_KEY);
      Enumeration params = httpRequest.getParameterNames();
      while (params.hasMoreElements()) {
         String name = (String) params.nextElement();

         // If parameter "function" contained function name, then do not pass
         // it down.
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

      return new FunctionRequest(functionName, functionParams, null);
   }

   void convertResult(FunctionResult      xinsResult,
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
      CallResultOutputter.output(out, RESPONSE_ENCODING, xinsResult, true);
      out.close();
   }
}
