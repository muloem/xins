/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.ProgrammingError;
import org.xins.common.collections.ProtectedPropertyReader;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.ParseException;
import org.xins.common.text.TextUtils;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * XML calling convention.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
final class XMLCallingConvention
extends CallingConvention {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The request encoding format.
    */
   static final String REQUEST_ENCODING = "UTF-8";

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
    * Constructs a new <code>XMLCallingConvention</code> object.
    */
   XMLCallingConvention() {
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

      String contentType = httpRequest.getContentType();
      if (!contentType.startsWith("text/xml;") && !contentType.endsWith("charset=UTF-8")) {
         throw new InvalidRequestException("Incorrect content type.", null);
      }
      
      try {
         BufferedReader reader = httpRequest.getReader();
         FastStringBuffer content = new FastStringBuffer(1024);
         String nextLine;
         while ((nextLine = reader.readLine()) !=null) {
            content.append(nextLine);
            content.append("\n");
         }

         String contentString = content.toString();
         ElementParser parser = new ElementParser();
         Element requestElem = parser.parse(contentString.getBytes(REQUEST_ENCODING));
         
         // Determine function name
         String functionName = determineFunction(
            requestElem.getAttribute("_function"),
            requestElem.getAttribute("function")
         );

         // Determine function parameters
         ProtectedPropertyReader functionParams = new ProtectedPropertyReader(SECRET_KEY);
         Iterator parameters = requestElem.getChildElements("param").iterator();
         while (parameters.hasNext()) {
            Element nextParam = (Element) parameters.next();
            String name  = nextParam.getAttribute("name");
            String value = nextParam.getText();
            functionParams.set(SECRET_KEY, name, value);
         }
         
         // Remove all invalid parameters
         cleanUpParameters(functionParams, SECRET_KEY);

         Element dataElement = null;
         List dataElementList = requestElem.getChildElements("data");
         if (dataElementList.size() == 1) {
            dataElement = (Element)dataElementList.get(0);
         } else if (dataElementList.size() > 1) {
            throw new InvalidRequestException("The request has more than two data section specified.", null);
         }
         
         if (TextUtils.isEmpty(functionName)) {
            throw new FunctionNotSpecifiedException();
         }
         
         return new FunctionRequest(functionName, functionParams, dataElement);
      } catch (UnsupportedEncodingException ex) {
         final String DETAIL = "Encoding \"" + REQUEST_ENCODING + "\" is not supported.";
         Log.log_3050(getClass().getName(), "convertRequestImpl(HttpServletRequest)", DETAIL);
         throw new ProgrammingError(DETAIL);
      } catch (IOException ex) {
         throw new InvalidRequestException("Cannot read the XML request.", ex);
      } catch (ParseException ex) {
         throw new InvalidRequestException("Cannot parse the XML request.", ex);
      }
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
