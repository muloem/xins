/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.ProtectedPropertyReader;
import org.xins.common.servlet.ServletRequestPropertyReader;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Calling convention that aims to be compatible with old, pre-1.0, XINS
 * releases. The output is especially aimed to be fully compatible with XINS
 * 0.168.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public final class OldStyleCallingConvention
extends Object
implements CallingConvention {

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

   public FunctionRequest getFunctionRequest(HttpServletRequest request)
   throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request);

      // XXX: What if invalid URL, e.g. query string ends with percent sign?

      ServletRequestPropertyReader parameters = new ServletRequestPropertyReader(request);
      String functionName = parameters.get("_function");
      if (functionName == null || functionName.length() == 0) {
         functionName = parameters.get("function");
      }
      if (functionName == null || functionName.length() == 0) {
         // TODO: Throw special exception indicating function is unspecified
         throw new ParseException("No function specified.");
      }

      ProtectedPropertyReader functionParams = new ProtectedPropertyReader(SECRET_KEY);
      Iterator itParams = parameters.getNames();
      while (itParams.hasNext()) {
         String nextName = (String)itParams.next();
         String nextValue = parameters.get(nextName);
         if (nextValue != null && nextValue.charAt(0) != '_' && nextName.length() > 0) {
            functionParams.set(SECRET_KEY, nextName, nextValue);
         }
         // TODO: Decide: Just ignore invalid parameter names?
      }
      return new FunctionRequest(functionName, functionParams, null);
   }

   public void handleResult(HttpServletResponse response, FunctionResult result)
   throws IllegalArgumentException, IOException {

      // Check preconditions
      MandatoryArgumentChecker.check("response", response, "result", result);

      // Send the XML output to the stream and flush
      PrintWriter out = response.getWriter();
      response.setContentType(RESPONSE_CONTENT_TYPE);
      response.setStatus(HttpServletResponse.SC_OK);
      CallResultOutputter.output(out, RESPONSE_ENCODING, result, true);
      out.close();
   }
}
