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

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.ProtectedPropertyReader;
import org.xins.common.servlet.ServletRequestPropertyReader;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Standard calling convention.
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
   throws IllegalArgumentException, ParseException {

      // Check preconditions
      MandatoryArgumentChecker.check("httpRequest", httpRequest);

      // XXX: What if invalid URL, e.g. query string ends with percent sign?

      ServletRequestPropertyReader parameters = new ServletRequestPropertyReader(httpRequest);
      String functionName = parameters.get("_function");
      if (functionName == null || functionName.length() == 0) {
         // TODO: Throw special exception indicating function is unspecified
         throw new ParseException("No function specified.");
      }
      String dataSectionValue = parameters.get("_data");
      Element dataElement = null;
      if (dataSectionValue != null) {
         try {
            ElementParser parser = new ElementParser();
            dataElement = parser.parse(dataSectionValue.getBytes("UTF-8"));
         } catch (UnsupportedEncodingException uee) {
            // TODO: Throw different kind of exception
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
      httpResponse.setContentType(RESPONSE_CONTENT_TYPE);
      httpResponse.setStatus(HttpServletResponse.SC_OK);
      CallResultOutputter.output(out, RESPONSE_ENCODING, xinsResult, false);
      out.close();
   }
}
