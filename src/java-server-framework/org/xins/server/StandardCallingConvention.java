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
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.collections.ProtectedPropertyReader;
import org.xins.common.servlet.ServletRequestPropertyReader;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * The standard calling convention.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public final class StandardCallingConvention implements CallingConvention {

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
    * Constructs a new <code>StandardCallingConvention</code>.
    */
   StandardCallingConvention() {
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public FunctionRequest getFunctionRequest(HttpServletRequest request) throws ParseException {
      ServletRequestPropertyReader parameters = new ServletRequestPropertyReader(request);
      String functionName = parameters.get("_function");
      if (functionName == null || functionName.length() == 0) {
         throw new ParseException("No function specified.");
      }
      String dataSectionValue = parameters.get("_data");
      Element dataElement = null;
      if (dataSectionValue != null) {
         try {
            ElementParser parser = new ElementParser();
            dataElement = parser.parse(dataSectionValue.getBytes("UTF-8"));
         } catch (UnsupportedEncodingException uee) {
            throw new ParseException("Cannot parse the data section.", uee, null);
         }
      }

      ProtectedPropertyReader functionParams = new ProtectedPropertyReader(SECRET_KEY);
      Iterator itParams = parameters.getNames();
      while (itParams.hasNext()) {
         String nextName = (String)itParams.next();
         String nextValue = parameters.get(nextName);
         if (nextValue != null && nextValue.charAt(0) != '_' && nextName.length() > 0) {
            functionParams.set(SECRET_KEY, nextName, nextValue);
         }
      }
      return new FunctionRequest(functionName, functionParams, dataElement);
   }

   public void handleResult(HttpServletResponse response, FunctionResult result) throws IOException {
      
      // Send the XML output to the stream and flush
      PrintWriter out = response.getWriter();
      response.setContentType(RESPONSE_CONTENT_TYPE);
      response.setStatus(HttpServletResponse.SC_OK);

      CallResultOutputter.output(out, RESPONSE_ENCODING, result, false);
      out.close();
   }
}
