/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.io.FastStringWriter;

import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;
import org.znerd.xmlenc.XMLOutputter;

/**
 * The SOAP calling convention.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
final class SOAPCallingConvention extends CallingConvention {
   
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
   static final String RESPONSE_CONTENT_TYPE = "application/soap+xml;charset=" + RESPONSE_ENCODING;

   
   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   
   protected FunctionRequest convertRequestImpl(HttpServletRequest httpRequest)
   throws InvalidRequestException,
          FunctionNotSpecifiedException {

      // Check content type
      // TODO: Support other character sets as well
      String contentType = httpRequest.getContentType();
      /*if (!contentType.startsWith("application/soap+xml;")) {
         final String DETAIL = "Incorrect content type \""
                             + contentType
                             + "\".";
         throw new InvalidRequestException(DETAIL);
      }*/

      try {

         // Convert the Reader to a string buffer
         BufferedReader reader = httpRequest.getReader();
         FastStringBuffer content = new FastStringBuffer(1024);
         String nextLine;
         while ((nextLine = reader.readLine()) != null) {
            content.append(nextLine);
            content.append("\n");
         }

         String contentString = content.toString().trim();
         ElementParser parser = new ElementParser();
         Element envelopElem = parser.parse(new StringReader(contentString));
         
         if (!envelopElem.getLocalName().equals("Envelope")) {
            throw new ParseException("Root element is not a SOAP envelop.");
         }
         
         List bodiesElem = envelopElem.getChildElements("Body");
         if (bodiesElem.size() == 0) {
            throw new ParseException("No body specified in the SOAP envelop.");
         } else if (bodiesElem.size() > 1) {
            throw new ParseException("More than one body specified in the SOAP envelop.");
         }
         Element bodyElem = (Element) bodiesElem.get(0);
         List functionsElem = bodyElem.getChildElements();
         if (functionsElem.size() == 0) {
            throw new ParseException("No function specified in the SOAP body.");
         } else if (bodiesElem.size() > 1) {
            throw new ParseException("More than one function specified in the SOAP body.");
         }
         Element functionElem = (Element) functionsElem.get(0);
         String functionName = functionElem.getLocalName();
         
         BasicPropertyReader parameters = new BasicPropertyReader();
         Iterator parametersElem = functionElem.getChildElements().iterator();
         while (parametersElem.hasNext()) {
            Element parameterElem = (Element) parametersElem.next();
            String parameterName = parameterElem.getLocalName();
            String parameterValue = parameterElem.getText();
            parameters.set(parameterName, parameterValue);
         }
         return new FunctionRequest(functionName, parameters, null);
         
      // I/O error
      } catch (IOException ex) {
         throw new InvalidRequestException("Cannot read the XML request.", ex);

      // Parsing error
      } catch (ParseException ex) {
         throw new InvalidRequestException("Cannot parse the XML request.", ex);
      }
   }
   
   protected void convertResultImpl(FunctionResult      xinsResult,
                                    HttpServletResponse httpResponse,
                                    HttpServletRequest  httpRequest)
   throws IOException {

      // Send the XML output to the stream and flush
      PrintWriter out = httpResponse.getWriter();
      // TODO: OutputStream out = httpResponse.getOutputStream();
      httpResponse.setContentType(RESPONSE_CONTENT_TYPE);
      if (xinsResult.getErrorCode() != null) {
         httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      } else {
         httpResponse.setStatus(HttpServletResponse.SC_OK);
      }
      
      // Store the result in a StringWriter before sending it.
      Writer buffer = new FastStringWriter();

      // Create an XMLOutputter
      XMLOutputter xmlout = new XMLOutputter(buffer, RESPONSE_ENCODING);

      // Output the declaration
      // XXX: Make it configurable whether the declaration is output or not?
      xmlout.declaration();

      // Write the envelop start tag
      xmlout.startTag("soap:Envelop");
      xmlout.attribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
      
      // Write the body start tag
      xmlout.startTag("soap:Body");
      
      if (xinsResult.getErrorCode() != null) {
         
         // Write the false start tag
         xmlout.startTag("soap:Fault");
         xmlout.attribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
         xmlout.startTag("faultcode");
         if (xinsResult.getErrorCode().equals("_InvalidRequest")) {
            xmlout.pcdata("soap:Client");
         } else {
            xmlout.pcdata("soap:Server");
         }
         xmlout.endTag(); // faultcode
         xmlout.startTag("faultstring");
         xmlout.pcdata(xinsResult.getErrorCode());
         xmlout.endTag(); // faultstring
         xmlout.endTag(); // fault
      } else {
         
         // Write the response start tag
         // XXX : Use the function name and the xmlns
         xmlout.startTag("m:Response");

         // Write the output parameters
         Iterator outputParameterNames = xinsResult.getParameters().getNames();
         while (outputParameterNames.hasNext()) {
            String parameterName = (String) outputParameterNames.next();
            String parameterValue = xinsResult.getParameter(parameterName);
            xmlout.startTag(parameterName);
            xmlout.pcdata(parameterValue);
            xmlout.endTag();
         }

         xmlout.endTag(); // response
      }
      
      xmlout.endTag(); // body
      xmlout.endTag(); // envelop

      // Write the result to the servlet response
      out.write(buffer.toString());
      
      out.close();
   }
}
