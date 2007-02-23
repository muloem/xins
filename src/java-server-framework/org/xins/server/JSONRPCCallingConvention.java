/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderConverter;
import org.xins.common.collections.PropertyReaderUtils;
import org.xins.common.spec.APISpec;
import org.xins.common.spec.EntityNotFoundException;
import org.xins.common.spec.ErrorCodeSpec;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.InvalidSpecificationException;
import org.xins.common.spec.ParameterSpec;
import org.xins.common.text.ParseException;
import org.xins.common.types.Type;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;
import org.xins.logdoc.ExceptionUtils;

/**
 * The JSON-RPC calling convention.
 * Version <a href='http://json-rpc.org/wiki/specification'>1.0</a>
 * and <a href='http://json-rpc.org/wd/JSON-RPC-1-1-WD-20060807.html'>1.1</a> are supported.
 * The service description is also returned on request when calling the
 * <em>system.describe</em> function.
 * The returned object is a JSON Object with a similar structure as the input
 * parameters when HTTP POST is used.
 *
 * @since XINS 2.0.
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 */
public class JSONRPCCallingConvention extends CallingConvention {

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
         return "bit";
      } else if (parameterType instanceof org.xins.common.types.standard.Int8
            || parameterType instanceof org.xins.common.types.standard.Int16
            || parameterType instanceof org.xins.common.types.standard.Int32
            || parameterType instanceof org.xins.common.types.standard.Int64
            || parameterType instanceof org.xins.common.types.standard.Float32
            || parameterType instanceof org.xins.common.types.standard.Float64) {
         return "num";
      } else {
         return "str";
      }
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The content type of the HTTP response.
    */
   public static final String RESPONSE_CONTENT_TYPE = "application/json";


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>JSONRPCCallingConvention</code> instance.
    *
    * @param api
    *    the API, needed for the JSON-RPC messages, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    */
   public JSONRPCCallingConvention(API api) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("api", api);
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

      // Return the service description when asked.
      String functionName = (String) httpRequest.getSession().getAttribute("functionName");
      if ("system.describe".equals(functionName)) {
         String uri = httpRequest.getRequestURI();
         if (uri.indexOf("system.describe") != -1) {
            uri = uri.substring(0, uri.indexOf("system.describe"));
         }
         try {
            JSONObject serviceDescriptionObject = createServiceDescriptionObject(uri);
            out.print(serviceDescriptionObject.toString());
            out.close();
            return;
         } catch (JSONException jsonex) {
            throw new IOException(jsonex.getMessage());
         }
      }

      // Transform the XINS result to a JSON object
      JSONObject returnObject = new JSONObject();
      try {
         String version = (String) httpRequest.getSession().getAttribute("version");
         if (version != null) {
            returnObject.append("version", version);
         }
         if (xinsResult.getErrorCode() != null) {
            if (version == null) {
               returnObject.append("result", null);
            }
            JSONObject errorObject = new JSONObject();
            String errorCode = xinsResult.getErrorCode();
            errorObject.append("name", errorCode);
            errorObject.append("code", new Integer(123));
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
            if (version == null) {
               returnObject.append("error", null);
            }
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
      httpRequest.getSession().setAttribute("functionName", functionName);
      if (functionName.equals("system.describe")) {
         return new FunctionRequest("_NoOp", PropertyReaderUtils.EMPTY_PROPERTY_READER, null);
      }
      httpRequest.getSession().setAttribute("version", "1.1");
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

         String version = requestObject.optString("version");
         if (version != null) {
            httpRequest.getSession().setAttribute("version", version);
         }

         functionName = requestObject.getString("method");
         httpRequest.getSession().setAttribute("functionName", functionName);
         if (functionName.equals("system.describe")) {
            return new FunctionRequest("_NoOp", PropertyReaderUtils.EMPTY_PROPERTY_READER, null);
         }

         // TODO take the other way to pass parameter into account
         Object paramsParam = requestObject.get("params");
         if (paramsParam instanceof JSONArray) {
            JSONArray paramsArray = (JSONArray) paramsParam;
            Iterator itInputParams = _api.getAPISpecification().getFunction(functionName).getInputParameters().keySet().iterator();
            int paramPos = 0;
            while (itInputParams.hasNext() && paramPos < paramsArray.length()) {
               String nextParamName = (String) itInputParams.next();
               Object nextParamValue = paramsArray.get(paramPos);
               functionParams.set(nextParamName, String.valueOf(nextParamValue));
               paramPos++;
            }
         } else if(paramsParam instanceof JSONArray) {
            JSONObject paramsObject = (JSONObject) paramsParam;
            JSONArray paramNames = paramsObject.names();
            for (int i = 0; i < paramNames.length(); i++) {
               String nextName = paramNames.getString(i);
               if (nextName.equals("_data")) {
                  JSONObject dataSectionObject = paramsObject.getJSONObject("_data");
                  String dataSectionString = XML.toString(dataSectionObject);
                  dataElement = new ElementParser().parse(dataSectionString);
               } else {
                  String value = paramsObject.get(nextName).toString();
                  functionParams.set(nextName, value);
               }
            }
         }
         Object id = requestObject.opt("id");
         httpRequest.getSession().setAttribute("id", id);
      } catch (ParseException parseEx) {
         throw new InvalidRequestException(parseEx.getMessage());
      } catch (JSONException jsonex) {
         throw new InvalidRequestException(jsonex.getMessage());
      } catch (InvalidSpecificationException isex) {
         RuntimeException exception = new RuntimeException();
         ExceptionUtils.setCause(exception, isex);
         throw exception;
      } catch (EntityNotFoundException enfex) {
         RuntimeException exception = new RuntimeException();
         ExceptionUtils.setCause(exception, enfex);
         throw exception;
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

   /**
    * Creates the JSON object containing the description of the API.
    * Specifications are available at http://json-rpc.org/wd/JSON-RPC-1-1-WD-20060807.html
    *
    * @param address
    *    the URL address of the service, cannot be <code>null</code>.
    *
    * @return
    *    the JSON object containing the description of the API, or <code>null</code>
    *    if an error occured.
    *
    * @throws JSONException
    *    if the object cannot be created for any reason.
    */
   private JSONObject createServiceDescriptionObject(String address) throws JSONException {
      JSONObject serviceObject = new JSONObject();
      serviceObject.append("sdversion", "1.0");
      serviceObject.append("name", _api.getName());
      String apiClassName = _api.getClass().getName();
      serviceObject.append("id", "xins:" + apiClassName.substring(0, apiClassName.indexOf(".api.API")));
      serviceObject.append("version", _api.getBootstrapProperties().get(API.API_VERSION_PROPERTY));
      try {
         APISpec apiSpec = _api.getAPISpecification();
         String description = apiSpec.getDescription();
         serviceObject.append("summary", description);
         serviceObject.append("address", address);

         // Add the functions
         JSONArray procs = new JSONArray();
         Iterator itFunctions = apiSpec.getFunctions().entrySet().iterator();
         while (itFunctions.hasNext()) {
            Map.Entry nextFunction = (Map.Entry) itFunctions.next();
            JSONObject functionObject = new JSONObject();
            functionObject.append("name", (String) nextFunction.getKey());
            FunctionSpec functionSpec = (FunctionSpec) nextFunction.getValue();
            functionObject.append("summary", functionSpec.getDescription());
            JSONArray params = getParamsDescription(functionSpec.getInputParameters(), functionSpec.getInputDataSectionElements());
            functionObject.append("params", params);
            JSONArray result = getParamsDescription(functionSpec.getOutputParameters(), functionSpec.getOutputDataSectionElements());
            functionObject.append("return", result);
         }
         serviceObject.append("procs", procs);
      } catch (InvalidSpecificationException ex) {
         // TODO log
         System.err.println(ex.getMessage());
         return serviceObject;
      }
      return serviceObject;
   }

   /**
    * Returns the description of the input or output parameters.
    *
    * @param paramsSpecs
    *    the specification of the input of output parameters, cannot be <code>null</code>.
    *
    * @param dataSectionSpecs
    *    the specification of the input of output data section, cannot be <code>null</code>.
    *
    * @return
    *    the JSON array containing the description of the input or output parameters, never <code>null</code>.
    *
    * @throws JSONException
    *    if the JSON object cannot be created.
    */
   private JSONArray getParamsDescription(Map paramsSpecs, Map dataSectionSpecs) throws JSONException {
      JSONArray params = new JSONArray();
      Iterator itParams = paramsSpecs.entrySet().iterator();
      while (itParams.hasNext()) {
         Map.Entry nextParam = (Map.Entry) itParams.next();
         JSONObject paramObject = new JSONObject();
         paramObject.append("name", (String) nextParam.getKey());
         String jsonType = convertType(((ParameterSpec) nextParam.getValue()).getType());
         paramObject.append("type", jsonType);
         params.put(paramObject);
         // TODO data section
      }
      return params;
   }
}
