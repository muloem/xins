/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.xins.common.collections.ProtectedPropertyReader;
import org.xins.common.io.FastStringWriter;
import org.xins.common.spec.EntityNotFoundException;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.InvalidSpecificationException;
import org.xins.common.types.Type;
import org.xins.common.xml.Element;
import org.znerd.xmlenc.XMLOutputter;

/**
 * The XML-RPC calling convention.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
class XMLRPCCallingConvention extends CallingConvention {
   
   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------
   
   /**
    * Returns the XML-RPC equivalent for the XINS type.
    *
    * @param parameterType
    *    the XINS type, cannot be <code>null</code>.
    *
    * @return
    *    the XML-RPC type, never <code>null</code>.
    */
   private static String convertType(Type parameterType) {
      if (parameterType instanceof org.xins.common.types.standard.Boolean) {
         return "boolean";
      } else if (parameterType instanceof org.xins.common.types.standard.Int8 
            || parameterType instanceof org.xins.common.types.standard.Int16
            || parameterType instanceof org.xins.common.types.standard.Int32) {
         return "int";
      } else if (parameterType instanceof org.xins.common.types.standard.Float32 
            || parameterType instanceof org.xins.common.types.standard.Float64) {
         return "double";
      } else if (parameterType instanceof org.xins.common.types.standard.Date 
            || parameterType instanceof org.xins.common.types.standard.Timestamp) {
         return "dateTime.iso8601";
      } else if (parameterType instanceof org.xins.common.types.standard.Base64) {
         return "base64";
      } else {
         return "string";
      }
   }
   
   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------
   
   /**
    * Secret key used when accessing <code>ProtectedPropertyReader</code>
    * objects.
    */
   private static final Object SECRET_KEY = new Object();

   /**
    * The formatter for XINS Date type.
    */
   private static final DateFormat XINS_DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
   
   /**
    * The formatter for XINS Timestamp type.
    */
   private static final DateFormat XINS_TIMESTAMP_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");
   
   /**
    * The formatter for XML-RPC dateTime.iso8601 type.
    */
   private static final DateFormat XML_RPC_TIMESTAMP_FORMATTER = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");

   /**
    * The key used to store the name of the function in the request attributes.
    */
   private static final String FUNCTION_NAME = "_function";

   /**
    * The response encoding format.
    */
   private static final String RESPONSE_ENCODING = "UTF-8";

   /**
    * The content type of the HTTP response.
    */
   private static final String RESPONSE_CONTENT_TYPE = "text/xml; charset=" + RESPONSE_ENCODING;


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------
   
   /**
    * Creates a new <code>XMLRPCCallingConvention</code>
    *
    * @param api
    *    the API, needed for the XML-RPC messages.
    */
   XMLRPCCallingConvention(API api) {
      _api = api;
   }

   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   /**
    * The API, never <code>null</code>.
    */
   private final API _api;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   protected FunctionRequest convertRequestImpl(HttpServletRequest httpRequest)
   throws InvalidRequestException,
          FunctionNotSpecifiedException {
      
      Element xmlRequest = parseXMLRequest(httpRequest, true);
      if (!xmlRequest.getLocalName().equals("methodCall")) {
         throw new InvalidRequestException("Root element is not \"methodCall\" but \"" + 
               xmlRequest.getLocalName() + "\".");
      }

      Element methodNameElem = getUniqueChild(xmlRequest, "methodName");
      String functionName = methodNameElem.getText();
      httpRequest.setAttribute(FUNCTION_NAME, functionName);
      
      // Determine function parameters
      ProtectedPropertyReader functionParams = new ProtectedPropertyReader(SECRET_KEY);
      
      List params = xmlRequest.getChildElements("params");
      if (params.size() == 0) {
         return new FunctionRequest(functionName, functionParams, null);
      } else if (params.size() > 1) {
         throw new InvalidRequestException("More than one params specified in the XML-RPC request.");
      }
      Element paramsElem = (Element) params.get(0);
      Iterator itParam = paramsElem.getChildElements("param").iterator();
      while (itParam.hasNext()) {
         Element nextParam = (Element) itParam.next();
         Element valueElem = getUniqueChild(nextParam, "value");
         Element structElem = getUniqueChild(valueElem, "struct");
         Element memberElem = getUniqueChild(structElem, "member");
         Element memberNameElem = getUniqueChild(memberElem, "name");
         Element memberValueElem = getUniqueChild(memberElem, "value");
         Element typeElem = getUniqueChild(memberValueElem, null);
         String parameterName = memberNameElem.getText();
         String parameterValue = typeElem.getText();
         try {
            FunctionSpec functionSpec = _api.getAPISpecification().getFunction(functionName);
            Type parameterType = functionSpec.getInputParameter(parameterName).getType();
            parameterValue = convertInput(parameterType, typeElem);
            System.err.println("niput: " + parameterValue);
         } catch (InvalidSpecificationException ise) {

            // keep the old value
         } catch (EntityNotFoundException enfe) {

            // keep the old value
         } catch (java.text.ParseException pex) {

            throw new InvalidRequestException("Invalid value for parameter \"" +
                  parameterName + "\".", pex);
         }
         functionParams.set(SECRET_KEY, parameterName, parameterValue);
      }
      
      // TODO: The data section is not supported at the moment.
      
      return new FunctionRequest(functionName, functionParams, null);
   }

   protected void convertResultImpl(FunctionResult      xinsResult,
                                    HttpServletResponse httpResponse,
                                    HttpServletRequest  httpRequest)
   throws IOException {
      
      // Send the XML output to the stream and flush
      PrintWriter out = httpResponse.getWriter();
      // TODO: OutputStream out = httpResponse.getOutputStream();
      httpResponse.setContentType(RESPONSE_CONTENT_TYPE);
      httpResponse.setStatus(HttpServletResponse.SC_OK);
      httpResponse.addHeader("Server", "XINS/Java Server Framework " + Library.getVersion());
      
      // Store the result in a StringWriter before sending it.
      Writer buffer = new FastStringWriter();

      // Create an XMLOutputter
      XMLOutputter xmlout = new XMLOutputter(buffer, RESPONSE_ENCODING);

      // Output the declaration
      xmlout.declaration();

      xmlout.startTag("methodResponse");
      
      String errorCode = xinsResult.getErrorCode();
      if (errorCode != null) {
         xmlout.startTag("fault");
         xmlout.startTag("value");
         xmlout.startTag("struct");
         
         xmlout.startTag("member");
         xmlout.startTag("name");
         xmlout.pcdata("faultCode");
         xmlout.endTag(); // name
         xmlout.startTag("value");
         xmlout.startTag("int");
         xmlout.pcdata(String.valueOf(getErrorCodeNumber(errorCode)));
         xmlout.endTag(); // int
         xmlout.endTag(); // value
         xmlout.endTag(); // member
         
         xmlout.startTag("member");
         xmlout.startTag("name");
         xmlout.pcdata("faultString");
         xmlout.endTag(); // name
         xmlout.startTag("value");
         xmlout.startTag("string");
         xmlout.pcdata(errorCode);
         xmlout.endTag(); // string
         xmlout.endTag(); // value
         xmlout.endTag(); // member
         
         xmlout.endTag(); // struct
         xmlout.endTag(); // value
         xmlout.endTag(); // fault
      } else {
         
         String functionName = (String) httpRequest.getAttribute(FUNCTION_NAME);
         
         xmlout.startTag("params");
         xmlout.startTag("param");
         xmlout.startTag("value");
         xmlout.startTag("struct");
         
         // Write the output parameters
         Iterator outputParameterNames = xinsResult.getParameters().getNames();
         while (outputParameterNames.hasNext()) {
            String parameterName = (String) outputParameterNames.next();
            String parameterValue = xinsResult.getParameter(parameterName);
            String parameterTag = "string";
            try {
               FunctionSpec functionSpec = _api.getAPISpecification().getFunction(functionName);
               Type parameterType = functionSpec.getOutputParameter(parameterName).getType();
               parameterValue = convertOutput(parameterType, parameterValue);
               parameterTag = convertType(parameterType);
            } catch (InvalidSpecificationException ise) {
               
               // keep the old value
            } catch (EntityNotFoundException enfe) {
               
               // keep the old value
            } catch (java.text.ParseException pex) {

               throw new IOException("Invalid value for parameter \"" + parameterName + "\".");
            }
            
            // Write the member element
            xmlout.startTag("member");
            xmlout.startTag("name");
            xmlout.pcdata(parameterName);
            xmlout.endTag();
            xmlout.startTag("value");
            xmlout.startTag(parameterTag);
            xmlout.pcdata(parameterValue);
            xmlout.endTag(); // type tag
            xmlout.endTag(); // value
            xmlout.endTag(); // member
         }
         
         xmlout.endTag(); // struct
         xmlout.endTag(); // value
         xmlout.endTag(); // param
         xmlout.endTag(); // params
      }
      
      xmlout.endTag(); // methodResponse

      // Write the result to the servlet response
      out.write(buffer.toString());

      out.close();
   }
   
   /**
    * Gets the unique child of the element.
    *
    * @param parentElement
    *    the parent element, cannot be <code>null</code>.
    *
    * @param elementName
    *    the name of the child element to get, or <code>null</code> if the
    *    parent have a unique child.
    *
    * @throws InvalidRequestException
    *    if no child was found or more than one child was found.
    */
   private Element getUniqueChild(Element parentElement, String elementName)
   throws InvalidRequestException {
      List childList = null;
      if (elementName == null) {
         childList = parentElement.getChildElements();
      } else {
         childList = parentElement.getChildElements(elementName);
      }
      if (childList.size() == 0) {
         throw new InvalidRequestException("No \"" + elementName + 
               "\" children found in the \"" + parentElement.getLocalName() + 
               "\" element of the XML-RPC request.");
      } else if (childList.size() > 1) {
         throw new InvalidRequestException("More than one \"" + elementName + 
               "\" children found in the \"" + parentElement.getLocalName() + 
               "\" element of the XML-RPC request.");
      }
      return (Element) childList.get(0);
   }
   
   /**
    * Converts the XML-RPC input values to XINS input values.
    *
    * @param parameterType
    *    the type of the XINS parameter, cannot be <code>null</code>.
    *
    * @param typeElem
    *    the content of the XML-RPC value, cannot be <code>null</code>.
    *
    * @return
    *    the XINS value, never <code>null</code>.
    *
    * @throws java.text.ParseException
    *    if the parameterValue is incorrect for the type.
    */
   private String convertInput(Type parameterType, Element typeElem) throws java.text.ParseException {
      String xmlRpcType = typeElem.getLocalName();
      String parameterValue = typeElem.getText();
      if (parameterType instanceof org.xins.common.types.standard.Boolean) {
         if (parameterValue.equals("1")) {
            return "true";
         } else if (parameterValue.equals("0")) {
            return "false";
         } else {
            throw new java.text.ParseException("Incorrect value for boolean: " + parameterValue, 0);
         }
      }
      //System.err.println("type: " + xmlRpcType + " ; value: " + parameterValue);
      if (xmlRpcType.equals("dateTime.iso8601")) {
         Date date = XML_RPC_TIMESTAMP_FORMATTER.parse(parameterValue);
         if (parameterType instanceof org.xins.common.types.standard.Date) {
            return XINS_DATE_FORMATTER.format(date);
         } else if (parameterType instanceof org.xins.common.types.standard.Timestamp) {
            return XINS_TIMESTAMP_FORMATTER.format(date);
         }
      }
      return parameterValue;
   }
   
   /**
    * Converts the XINS output values to XML-RPC output values.
    *
    * @param parameterType
    *    the type of the XINS parameter, cannot be <code>null</code>.
    *
    * @param parameterValue
    *    the XINS parameter value to convert, cannot be <code>null</code>.
    *
    * @return
    *    the XML-RPC value, never <code>null</code>.
    *
    * @throws java.text.ParseException
    *    if the parameterValue is incorrect for the type.
    */
   private String convertOutput(Type parameterType, String parameterValue) throws java.text.ParseException {
      if (parameterType instanceof org.xins.common.types.standard.Boolean) {
         if (parameterValue.equals("true")) {
            return "1";
         } else if (parameterValue.equals("false")) {
            return "0";
         } else {
            throw new java.text.ParseException("Incorrect value for boolean: " + parameterValue, 0);
         }
      } else if (parameterType instanceof org.xins.common.types.standard.Date) {
         Date date = XINS_DATE_FORMATTER.parse(parameterValue);
         return XML_RPC_TIMESTAMP_FORMATTER.format(date);
      } else if (parameterType instanceof org.xins.common.types.standard.Timestamp) {
         Date date = XINS_TIMESTAMP_FORMATTER.parse(parameterValue);
         return XML_RPC_TIMESTAMP_FORMATTER.format(date);
      }
      return parameterValue;
   }
   
   /**
    * Attribute a number for the error code.
    *
    * @param errorCode
    *    the error code, cannot be <code>null</code>.
    *
    * @return
    *    the error code number, always > 0;
    */
   private int getErrorCodeNumber(String errorCode) {
      if (errorCode.equals("_DisabledFunction")) {
         return 1;
      } else if (errorCode.equals("_InternalError")) {
         return 2;
      } else if (errorCode.equals("_InvalidRequest")) {
         return 3;
      } else if (errorCode.equals("_InvalidResponse")) {
         return 4;
      } else {
         
         // Defined error code returned. For more information, see the 
         // faultString element.
         return 99;
      }
   }
}
