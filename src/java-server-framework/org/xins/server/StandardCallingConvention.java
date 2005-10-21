/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.Utils;
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
class StandardCallingConvention
extends CallingConvention {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The fully-qualified name of this class.
    */
   static final String CLASSNAME = StandardCallingConvention.class.getName();

   /**
    * The enconding for the data section.
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

      final String THIS_METHOD = "convertRequestImpl("
                               + HttpServletRequest.class.getName()
                               + ')';

      // Determine function name
      String functionName = decodeParameter(httpRequest, "_function");
      if (TextUtils.isEmpty(functionName)) {
         throw new FunctionNotSpecifiedException();
      }

      // Determine function parameters
      ProtectedPropertyReader functionParams =
         new ProtectedPropertyReader(SECRET_KEY);
      Enumeration params = httpRequest.getParameterNames();
      while (params.hasMoreElements()) {
         String name  = (String) params.nextElement();
         String value = decodeParameter(httpRequest, name);
         functionParams.set(SECRET_KEY, name, value);
      }

      // Remove all invalid parameters
      cleanUpParameters(functionParams, SECRET_KEY);

      // Get data section
      String dataSectionValue = decodeParameter(httpRequest, "_data");
      Element dataElement = null;
      if (dataSectionValue != null && dataSectionValue.length() > 0) {
         ElementParser parser = new ElementParser();

         // Parse the data section
         try {
            dataElement = parser.parse(new StringReader(dataSectionValue));

         // I/O error, should never happen on a StringReader
         } catch (IOException ex) {
            final String SUBJECT_CLASS  = "java.lang.String";
            final String SUBJECT_METHOD = "getBytes(java.lang.String)";
            final String DETAIL         = "Encoding \"" + DATA_ENCODING + "\" is not supported.";
            throw Utils.logProgrammingError(CLASSNAME,     THIS_METHOD,
                                            SUBJECT_CLASS, SUBJECT_METHOD,
                                            DETAIL,        ex);
         // Parsing error
         } catch (ParseException ex) {
            throw new InvalidRequestException("Cannot parse the data section.", ex);
         }
      }

      // Construct and return the request object
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
    * @param httpRequest
    *    the HTTP request, will not be <code>null</code>.
    *
    * @throws IOException
    *    if calling any of the methods in <code>httpResponse</code> causes an
    *    I/O error.
    */
   protected void convertResultImpl(FunctionResult      xinsResult,
                                    HttpServletResponse httpResponse,
                                    HttpServletRequest  httpRequest)
   throws IOException {

      // Send the XML output to the stream and flush
      PrintWriter out = httpResponse.getWriter();
      httpResponse.setContentType(RESPONSE_CONTENT_TYPE);
      httpResponse.setStatus(HttpServletResponse.SC_OK);
      CallResultOutputter.output(out, xinsResult, false);
      out.close();
   }
}
