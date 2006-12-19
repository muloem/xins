/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderConverter;
import org.xins.common.collections.PropertyReaderUtils;
import org.xins.common.json.JSONArray;
import org.xins.common.json.JSONException;
import org.xins.common.json.JSONObject;
import org.xins.common.json.JSONWriter;
import org.xins.common.json.XML;
import org.xins.common.spec.EntityNotFoundException;
import org.xins.common.spec.ErrorCodeSpec;
import org.xins.common.spec.InvalidSpecificationException;
import org.xins.common.text.ParseException;
import org.xins.common.types.Type;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

import org.znerd.xmlenc.XMLOutputter;

/**
 * The JSON-RPC calling convention.
 *
 * @since XINS 2.0.
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 */
final class JSONRPCCallingConvention extends CallingConvention {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns the JSON-RPC equivalent for the XINS type.
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
    * The response encoding format.
    */
   private static final String RESPONSE_ENCODING = "UTF-8";

   /**
    * The content type of the HTTP response.
    */
   private static final String RESPONSE_CONTENT_TYPE = "text/xml;charset=" + RESPONSE_ENCODING;


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>JSONRPCCallingConvention</code> instance.
    *
    * @param api
    *    the API, needed for the JSON-RPC messages, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    */
   public JSONRPCCallingConvention(API api)
   throws IllegalArgumentException {

      // Check arguments
      MandatoryArgumentChecker.check("api", api);

      // Store the API reference (can be null!)
      _api = api;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API. Never <code>null</code>.
    */
   private final API _api;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected String[] getSupportedMethods() {
      return new String[] { "GET", "POST" };
   }

   protected boolean matches(HttpServletRequest httpRequest)
   throws Exception {

      if (httpRequest.getHeader("User-Agent") == null) {
         return false;
      }
      if (!"application/json".equals(httpRequest.getHeader("Accept"))) {
         return false;
      }
      if ("post".equalsIgnoreCase(httpRequest.getMethod())) {
         return "application/json".equals(httpRequest.getContentType());
      }
      return true;
   }

   protected FunctionRequest convertRequestImpl(HttpServletRequest httpRequest)
   throws InvalidRequestException, FunctionNotSpecifiedException {

      if ("post".equalsIgnoreCase(httpRequest.getMethod())) {
         return parsePostRequest(httpRequest);
      } else if ("get".equalsIgnoreCase(httpRequest.getMethod())) {
         return parseGetRequest(httpRequest);
      } else {
         throw new InvalidRequestException("Incorrect HTTP method: " + httpRequest.getMethod());
      }
   }

   protected void convertResultImpl(FunctionResult      xinsResult,
                                    HttpServletResponse httpResponse,
                                    HttpServletRequest  httpRequest)
   throws IOException {

      // Send the XML output to the stream and flush
      httpResponse.setContentType(RESPONSE_CONTENT_TYPE);
      PrintWriter out = httpResponse.getWriter();
      httpResponse.setStatus(HttpServletResponse.SC_OK);

      JSONObject returnObject = new JSONObject();
      try {
         returnObject.append("version", "1.1");
         if (xinsResult.getErrorCode() != null) {
            JSONObject errorObject = new JSONObject();
            String errorCode = xinsResult.getErrorCode();
            errorObject.append("name", errorCode);
            errorObject.append("code", new Integer(123));
            String functionName = (String) httpRequest.getSession().getAttribute("functionName");
            if (functionName != null) {
               try {
                  ErrorCodeSpec errorSpec = _api.getAPISpecification().getFunction(functionName).getErrorCode(errorCode);
                  String errorDescription = errorSpec.getDescription();
                  if (errorDescription.indexOf(". ") != -1) {
                     errorDescription = errorDescription.substring(0, errorDescription.indexOf(". "));
                  }
                  errorObject.append("message", errorDescription);
               } catch (Exception ex) {
                  errorObject.append("message", "Unknown error: " + ex.getMessage());
               }
            }
            JSONObject paramsObject = createResultObject(xinsResult);
            errorObject.append("error", paramsObject);
            returnObject.append("error", errorObject);
         } else {
            JSONObject paramsObject = createResultObject(xinsResult);
            returnObject.append("result", paramsObject);
         }
         Object requestId = httpRequest.getSession().getAttribute("id");
         if (requestId != null) {
            returnObject.append("id", requestId);
         }

         // Write the result to the servlet response
         out.print(returnObject.toString());
      } catch (JSONException jsonex) {
         throw new IOException(jsonex.getMessage());
      }

      out.close();
   }

   /**
    * Parses the JSON-RPC HTTP GET request according to the specs.
    *
    * @param httpRequest
    *    the HTTP request.
    *
    * @return
    *    the XINS request object, should not be <code>null</code>.
    *
    * @throws InvalidRequestException
    *    if the request is considerd to be invalid.
    *
    * @throws FunctionNotSpecifiedException
    *    if the request does not indicate the name of the function to execute.
    */
   private FunctionRequest parseGetRequest(HttpServletRequest httpRequest)
   throws InvalidRequestException, FunctionNotSpecifiedException {
      String functionName = null;
      PropertyReader functionParams = null;
      Element dataElement = null;

      String query = httpRequest.getQueryString();
      int questionMarkPos = query.indexOf("?");
      if (query.lastIndexOf("/") == query.length()) {
         throw new FunctionNotSpecifiedException();
      } else if (questionMarkPos == -1) {
         functionName = query.substring(query.lastIndexOf("/"));
      } else {
         functionName = query.substring(query.lastIndexOf("/"), questionMarkPos);
      }
      try {
         functionParams = gatherParams(httpRequest);
      } catch (InvalidRequestException ex) {
         throw new FunctionNotSpecifiedException();
      }

      // Get data section
      String dataSectionValue = httpRequest.getParameter("_data");
      if (dataSectionValue != null && dataSectionValue.length() > 0) {
         ElementParser parser = new ElementParser();
         try {
            dataElement = parser.parse(new StringReader(dataSectionValue));

         // I/O error, should never happen on a StringReader
         } catch (IOException exception) {
            throw Utils.logProgrammingError(exception);
         // Parsing error
         } catch (ParseException exception) {
            String detail = "Cannot parse the data section.";
            throw new InvalidRequestException(detail, exception);
         }
      }
      return new FunctionRequest(functionName, functionParams, dataElement);
   }

   /**
    * Parses the JSON-RPC HTTP POST request according to the specs.
    * http://json-rpc.org/wd/JSON-RPC-1-1-WD-20060807.html
    *
    * @param httpRequest
    *    the HTTP request.
    *
    * @return
    *    the XINS request object, should not be <code>null</code>.
    *
    * @throws InvalidRequestException
    *    if the request is considerd to be invalid.
    *
    * @throws FunctionNotSpecifiedException
    *    if the request does not indicate the name of the function to execute.
    */
   private FunctionRequest parsePostRequest(HttpServletRequest httpRequest)
   throws InvalidRequestException, FunctionNotSpecifiedException {
      String functionName = null;
      BasicPropertyReader functionParams = new BasicPropertyReader();
      Element dataElement = null;

      // Read the message
      StringBuffer requestBuffer = new StringBuffer(2048);
      try {
         Reader reader = httpRequest.getReader();
         char[] buffer = new char[2048];
         int length = 0;
         while ((length = reader.read(buffer)) != -1) {
            requestBuffer.append(buffer, 0, length);
         }
      } catch (IOException ioe) {
         throw new InvalidRequestException("I/O Error while reading the request: " + ioe.getMessage());
      }
      String requestString = requestBuffer.toString();

      // Extract the request from the message
      try {
         JSONObject requestObject = new JSONObject(requestString);
         functionName = requestObject.getString("method");
         httpRequest.getSession().setAttribute("functionName", functionName);

         // TODO take the other way to pass parameter into account
         JSONObject paramsObject = requestObject.getJSONObject("params");
         JSONArray paramNames = paramsObject.names();
         for (int i = 0; i < paramNames.length(); i++) {
            String nextName = paramNames.getString(i);
            if (nextName.equals("_data")) {
               JSONObject dataSectionObject = paramsObject.getJSONObject("_data");
               String dataSectionString = XML.toString(dataSectionObject);
               dataElement = ElementParser.parse(dataSectionString);
            } else {
               String value = paramsObject.get(nextName).toString();
               functionParams.set(nextName, value);
            }
         }
         Object id = requestObject.opt("id");
         httpRequest.getSession().setAttribute("id", id);
      } catch (ParseException parseEx) {
         throw new InvalidRequestException(parseEx.getMessage());
      } catch (JSONException jsonex) {
         throw new InvalidRequestException(jsonex.getMessage());
      }
      return new FunctionRequest(functionName, functionParams, dataElement);
   }

   /**
    * Creates the JSON object from the result returned by the function.
    *
    * @param xinsResult
    *    the result returned by the function, cannot be <code>null</code>.
    *
    * @return
    *    the JSON object created from the result of the function, never <code>null</code>.
    *
    * @throws JSONException
    *    if the object cannot be created for any reason.
    */
   private JSONObject createResultObject(FunctionResult xinsResult) throws JSONException {
      Properties params = PropertyReaderConverter.toProperties(xinsResult.getParameters());
      JSONObject paramsObject = new JSONObject(params);
      if (xinsResult.getDataElement() != null) {
         String dataSection = xinsResult.getDataElement().toString();
         JSONObject dataSectionObject = XML.toJSONObject(dataSection);
         paramsObject.accumulate("data", dataSectionObject);
      }
      return paramsObject;
   }
}
