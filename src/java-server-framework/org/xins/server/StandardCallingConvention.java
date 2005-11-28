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
   static final String RESPONSE_CONTENT_TYPE = "text/xml; charset="
                                             + RESPONSE_ENCODING;

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

      // This calling convention is not deprecated, so pass 'false' up
      super(false);
   }

   /**
    * Constructs a new <code>StandardCallingConvention</code>, indicating
    * whether it should be considered deprecated.
    *
    * @param deprecated
    *    <code>true</code> if this calling convention is to be considered
    *    deprecated, or <code>false</code> if not.
    */
   protected StandardCallingConvention(boolean deprecated) {
      super(deprecated);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Checks if the specified request can be handled by this calling
    * convention.
    *
    * <p>The return value is as follows:
    *
    * <ul>
    *    <li>a positive value indicates that the request <em>can</em>
    *        be handled;
    *    <li>the value <code>0</code> indicates that the request
    *        <em>cannot</em> be handled;
    *    <li>a negative number indicates that it is <em>unknown</em>
    *        whether the request can be handled by this calling convention.
    * </ul>
    *
    * <p>This method will not throw any exception.
    *
    * @param httpRequest
    *    the HTTP request to investigate, cannot be <code>null</code>.
    *
    * @return
    *    a positive value if the request can be handled; <code>0</code> if the
    *    request cannot be handled or a negative value if it is unknown.
    */
   int matchesRequest(HttpServletRequest httpRequest) {

      int match;

      // If no _function parameter is specified, then there is no match
      if (TextUtils.isEmpty(httpRequest.getParameter("_function"))) {
         match = NOT_MATCHING;

      // If the _function parameter is set, then there is a match
      } else {
         match = MATCHING;
      }

      return match;
   }

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


      // Determine function name
      String functionName = httpRequest.getParameter("_function");
      if (TextUtils.isEmpty(functionName)) {
         throw new FunctionNotSpecifiedException();
      }

      // Determine function parameters
      ProtectedPropertyReader functionParams =
         new ProtectedPropertyReader(SECRET_KEY);
      Enumeration params = httpRequest.getParameterNames();
      while (params.hasMoreElements()) {
         String name  = (String) params.nextElement();
         String value = httpRequest.getParameter(name);
         functionParams.set(SECRET_KEY, name, value);
      }

      // Remove all invalid parameters
      cleanUpParameters(functionParams, SECRET_KEY);

      // Get data section
      String dataSectionValue = httpRequest.getParameter("_data");
      Element dataElement = null;
      if (dataSectionValue != null && dataSectionValue.length() > 0) {
         ElementParser parser = new ElementParser();

         // Parse the data section
         try {
            dataElement = parser.parse(new StringReader(dataSectionValue));

         // I/O error, should never happen on a StringReader
         } catch (IOException exception) {
            String thisMethod = "convertRequestImpl("
                              + HttpServletRequest.class.getName()
                              + ')';
            String subjectClass  = "java.lang.String";
            String subjectMethod = "getBytes(java.lang.String)";
            String detail        = "Encoding \""
                                 + DATA_ENCODING
                                 + "\" is not supported.";
            throw Utils.logProgrammingError(CLASSNAME,    thisMethod,
                                            subjectClass, subjectMethod,
                                            detail,       exception);
         // Parsing error
         } catch (ParseException exception) {
            String detail = "Cannot parse the data section.";
            throw new InvalidRequestException(detail, exception);
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
      httpResponse.setContentType(RESPONSE_CONTENT_TYPE);
      PrintWriter out = httpResponse.getWriter();
      httpResponse.setStatus(HttpServletResponse.SC_OK);
      CallResultOutputter.output(out, xinsResult, false);
      out.close();
   }
}
