/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.spec.DataSectionElementSpec;
import org.xins.common.spec.EntityNotFoundException;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.InvalidSpecificationException;
import org.xins.common.spec.ParameterSpec;
import org.xins.common.text.ParseException;
import org.xins.common.types.Type;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementBuilder;
import org.xins.common.xml.ElementBuilder;
import org.xins.common.xml.ElementSerializer;
import org.xins.server.FunctionRequest;

import org.znerd.xmlenc.XMLOutputter;

/**
 * The SOAP calling convention that tries to map the SOAP request to the 
 * parameters of the function. The rules applied for the mapping are the same
 * as for the command wsdl-to-api.
 * <p/>
 * Note that by default any SOAP message will be handled by the _xins_soap
 * calling convention. If you want to use this calling convention you will
 * need to explicitly have _convention=_xins_soap_map in the URL parameters.
 * <p/>
 * This calling convention is easily extendable in order to adapt to the
 * specificity of your SOAP requests.
 * <p/>
 * Here is the mapping for the input:
 * <ul>
 * <li>If the element in the Body ends with 'Request', the function name is
 * considered to be what is specified before</li>
 * <li>Otherwise the name of the element is used for the name of the function</li>
 * <li>Elements in the request are mapped to input parameters if available.</li>
 * <li>Elements with sub-elements are mapped to input parameters element1.sub-element1... if available.</li>
 * <li>If no parameter is found, try to find an input data element with the name.</li>
 * <li>If not found, go to the sub-elements and try to find an input data element with the name.</li>
 * <li>If not found, skip it. Here it's up to you to override this convention and provide a mapping.</li>
 * </ul>
 * <p/>
 * Here is the mapping for the output:
 * <ul>
 * <li>Response name = function name + "Response"</li>
 * <li>Output parameters with dots are transformed to XML. 
 * e.g. element1.element2 -&gt; &lt;element1&gt;&lt;element2&gt;value&lt;/element2&gt;&lt;/element1&gt;</li>
 * <li>The data section is not put in the returned XML, only the elements it contains.</li>
 * <li>Data section element attributes are changed to sub-elements with the 
 * same rule as for output parameters.</li>
 * </ul>
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 */
public class SOAPMapCallingConvention extends SOAPCallingConvention {

   /**
    * Creates a new <code>SOAPCallingConvention</code> instance.
    *
    * @param api
    *    the API, needed for the SOAP messages, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    */
   public SOAPMapCallingConvention(API api) throws IllegalArgumentException {
      super(api);
   }

   /**
    * This calling convention should be specified explicitly.
    *
    * <p>This method will not throw any exception.
    *
    * @param httpRequest
    *    the HTTP request to investigate, cannot be <code>null</code>.
    *
    * @return
    *    <code>true</code> if this calling convention is <em>possibly</em>
    *    able to handle this request, or <code>false</code> if it
    *    <em>definitely</em> not able to handle this request.
    *
    * @throws Exception
    *    if analysis of the request causes an exception;
    *    <code>false</code> will be assumed.
    */
   protected boolean matches(HttpServletRequest httpRequest)
   throws Exception {

      return false;
   }

   protected FunctionRequest convertRequestImpl(HttpServletRequest httpRequest)
   throws InvalidRequestException,
          FunctionNotSpecifiedException {

      Element envelopeElem = parseXMLRequest(httpRequest);

      if (! envelopeElem.getLocalName().equals("Envelope")) {
         throw new InvalidRequestException("Root element is not a SOAP envelope but \"" +
               envelopeElem.getLocalName() + "\".");
      }
      
      String functionName;
      Element functionElem;
      try {
         Element bodyElem = envelopeElem.getUniqueChildElement("Body");
         functionElem = bodyElem.getUniqueChildElement(null);
      } catch (ParseException pex) {
         throw new InvalidRequestException("Incorrect SOAP message.", pex);
      }
      String requestName = functionElem.getLocalName();
      if (!requestName.endsWith("Request")) {
         functionName = requestName;
      } else {
         functionName = requestName.substring(0, requestName.lastIndexOf("Request"));
      }

      httpRequest.setAttribute(FUNCTION_NAME, functionName);
      httpRequest.setAttribute(REQUEST_NAMESPACE, functionElem.getNamespaceURI());

      // Parse the input parameters
      FunctionRequest functionRequest = readInput(functionElem, functionName);

      return functionRequest;
   }

   /**
    * Generates the function request based the the SOAP request.
    * This function will get the XML element in the SOAP request and associate
    * the values with the input parameter or data section element of the function.
    * 
    * @param functionElem
    *    the SOAP element of the function request, cannot be <code>null</code>.
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    * 
    * @return
    *    the function request that will be passed to the XINS function, cannot be <code>null</code>.
    */
   protected FunctionRequest readInput(Element functionElem, String functionName) {
      BasicPropertyReader inputParams = new BasicPropertyReader();
      ElementBuilder dataSectionBuilder = new ElementBuilder("data");
      Iterator itParameters = functionElem.getChildElements().iterator();
      while (itParameters.hasNext()) {
         Element parameterElem = (Element) itParameters.next();
         try {
            Element dataElement = readInputElem(parameterElem, functionName, null, null, inputParams);
            if (dataElement != null) {
               dataSectionBuilder.addChild(dataElement);
            }
         } catch (Exception ex) {
            // Log and skip this parameter
         }
      }
      return new FunctionRequest(functionName, inputParams, dataSectionBuilder.createElement());
   }

   /**
    * TODO
    */
   protected Element readInputElem(Element inputElem, String functionName, String parent, 
         Element parentElement, BasicPropertyReader inputParams) throws Exception {
      FunctionSpec functionSpec = getAPI().getAPISpecification().getFunction(functionName);
      Map inputParamsSpec = functionSpec.getInputParameters();
      Map inputDataSectionSpec = functionSpec.getInputDataSectionElements();
      String parameterName = inputElem.getLocalName();
      String fullName = parent == null ? parameterName : parent + "." + parameterName;

      // Fill the attribute of the input data section with the SOAP sub-elements
      if (parentElement != null) {
         DataSectionElementSpec elementSpec = (DataSectionElementSpec) inputDataSectionSpec.get(parentElement.getLocalName());
         if (elementSpec != null && elementSpec.getAttributes().containsKey(fullName) && inputElem.getChildElements().size() == 0) {
            String parameterValue = inputElem.getText();
            Type parameterType = elementSpec.getAttribute(fullName).getType();
            parameterValue = soapInputValueTransformation(parameterType, parameterValue);
            parentElement.setAttribute(fullName, parameterValue);
         } else if (elementSpec != null && inputElem.getChildElements().size() > 0) {
            Iterator itParameters = inputElem.getChildElements().iterator();
            while (itParameters.hasNext()) {
               Element parameterElem = (Element) itParameters.next();
               readInputElem(parameterElem, functionName, fullName, parentElement, inputParams);
            }
         }

      // Simple input parameter that maps
      } else if (inputParamsSpec.containsKey(fullName) && inputElem.getChildElements().size() == 0) {
         String parameterValue = inputElem.getText();
         Type parameterType = ((ParameterSpec) inputParamsSpec.get(fullName)).getType();
         parameterValue = soapInputValueTransformation(parameterType, parameterValue);
         inputParams.set(fullName, parameterValue);

      // Element with sub-elements
      } else if (inputElem.getChildElements().size() > 0) {

         // It can be in the parameters or in the data section
         Iterator itParamNames = inputParamsSpec.keySet().iterator();
         boolean found = false;
         while (itParamNames.hasNext() && !found) {
            String nextParamName = (String) itParamNames.next();
            if (nextParamName.startsWith(fullName + ".")) {
               found = true;
            }
         }

         // The sub element match a input parameter
         if (found) {
            Iterator itParameters = inputElem.getChildElements().iterator();
            while (itParameters.hasNext()) {
               Element parameterElem = (Element) itParameters.next();
               readInputElem(parameterElem, functionName, fullName, null, inputParams);
            }

         // The sub element match a input data element
         } else if (inputDataSectionSpec.containsKey(parameterName)) {
            Element dataElement = new Element(parameterName);
            Iterator itParameters = inputElem.getChildElements().iterator();
            while (itParameters.hasNext()) {
               Element parameterElem = (Element) itParameters.next();
               readInputElem(parameterElem, functionName, null, dataElement, inputParams);
            }
            return dataElement;

         // Ignore this element and go throw the sub-elements
         } else {
            Iterator itParameters = inputElem.getChildElements().iterator();
            while (itParameters.hasNext()) {
               Element parameterElem = (Element) itParameters.next();
               readInputElem(parameterElem, functionName, parent, null, inputParams);
            }
         }
      } else {
         // not found (log?)
      }
      return null;
   }

   protected void convertResultImpl(FunctionResult      xinsResult,
                                    HttpServletResponse httpResponse,
                                    HttpServletRequest  httpRequest)
   throws IOException {

      // Send the XML output to the stream and flush
      httpResponse.setContentType(RESPONSE_CONTENT_TYPE);
      PrintWriter out = httpResponse.getWriter();
      if (xinsResult.getErrorCode() != null) {
         httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      } else {
         httpResponse.setStatus(HttpServletResponse.SC_OK);
      }

      Element envelope = writeResponse(httpRequest, xinsResult);

      // Write the result to the servlet response
      out.write(envelope.toString());

      out.close();
   }

   protected Element writeResponse(HttpServletRequest httpRequest, FunctionResult xinsResult) 
   throws IOException {

      Element envelope = new Element("Envelope", "http://schemas.xmlsoap.org/soap/envelope/");
      Element body = new Element("Body", envelope.getNamespaceURI());
      envelope.addChild(body);

      String functionName = (String) httpRequest.getAttribute(FUNCTION_NAME);
      String namespaceURI = (String) httpRequest.getAttribute(REQUEST_NAMESPACE);

      if (xinsResult.getErrorCode() != null) {
         //writeFaultSection(functionName, namespaceURI, xinsResult, xmlout);
      } else {

         // Write the response start tag
         Element response = new Element(functionName + "Response", namespaceURI);

         writeOutputParameters(functionName, xinsResult, response);
         writeOutputDataSection(functionName, xinsResult, response);
         body.addChild(response);
      }
      return envelope;
   }

   /**
    * Writes the output parameters to the SOAP XML.
    *
    * @param functionName
    *    the name of the function called.
    *
    * @param xinsResult
    *    the result of the call to the function.
    *
    * @param xmlout
    *    the XML outputter to write the parameters in.
    *
    * @throws IOException
    *    if the data cannot be written to the XML outputter for any reason.
    */
   protected void writeOutputParameters(String functionName, FunctionResult xinsResult, Element response)
   throws IOException {
      Iterator outputParameterNames = xinsResult.getParameters().getNames();
      while (outputParameterNames.hasNext()) {
         String parameterName = (String) outputParameterNames.next();
         String parameterValue = xinsResult.getParameter(parameterName);
         try {
            FunctionSpec functionSpec = getAPI().getAPISpecification().getFunction(functionName);
            Type parameterType = functionSpec.getOutputParameter(parameterName).getType();
            parameterValue = soapOutputValueTransformation(parameterType, parameterValue);
         } catch (InvalidSpecificationException ise) {

            // keep the old value
         } catch (EntityNotFoundException enfe) {

            // keep the old value
         }
         writeOutputParameter(parameterName, parameterValue, response);
      }
   }

   protected void writeOutputParameter(String parameterName, String parameterValue, Element parent) {
      if (parameterName.indexOf(".") == -1) {
         Element paramElem = new Element(parameterName, parent.getNamespaceURI());
         paramElem.setText(parameterValue);
         parent.addChild(paramElem);
      } else {
         String elementName = parameterName.substring(0, parameterName.indexOf("."));
         String rest = parameterName.substring(parameterName.indexOf(".") + 1);
         Element paramElem = null;
         if (parent.getChildElements(elementName).size() > 0) {
            paramElem = (Element) parent.getChildElements(elementName).get(0);
            writeOutputParameter(rest, parameterValue, paramElem);
         } else {
            paramElem = new Element(elementName, parent.getNamespaceURI());
            writeOutputParameter(rest, parameterValue, paramElem);
            parent.addChild(paramElem);
         }
      }
   }

   /**
    * Writes the output data section to the SOAP XML.
    *
    * @param functionName
    *    the name of the function called.
    *
    * @param xinsResult
    *    the result of the call to the function.
    *
    * @param response
    *    the SOAP response element, cannot be <code>null</code>.
    *
    * @throws IOException
    *    if the data cannot be written to the XML outputter for any reason.
    */
   protected void writeOutputDataSection(String functionName, FunctionResult xinsResult, Element response)
   throws IOException {
      Map dataSectionSpec = null;
      try {
         FunctionSpec functionSpec = getAPI().getAPISpecification().getFunction(functionName);
         dataSectionSpec = functionSpec.getOutputDataSectionElements();
      } catch (InvalidSpecificationException ise) {
      } catch (EntityNotFoundException enfe) {
      }
      Element dataElement = xinsResult.getDataElement();
      if (dataElement != null) {

         Iterator itDataElements = dataElement.getChildElements().iterator();
         while (itDataElements.hasNext()) {
            Element nextDataElement = (Element) itDataElements.next();
            writeOutputDataElement(dataSectionSpec, nextDataElement, response);
         }
      }
   }
   
   protected void writeOutputDataElement(Map dataSectionSpec, Element dataElement, Element parent) {
      Element transformedDataElement = soapElementTransformation(dataSectionSpec, false, dataElement, false);
      parent.addChild(transformedDataElement);
   }

   protected void setDataElementAttribute(ElementBuilder builder, String attributeName, String attributeValue) {
      if (attributeName.indexOf(".") == -1) {
         Element dataElement = new Element(attributeName);
         dataElement.setText(attributeValue);
         builder.addChild(dataElement);
      } else {
         String elementName = attributeName.substring(0, attributeName.indexOf("."));
         String rest = attributeName.substring(attributeName.indexOf(".") + 1);
         Element paramElem = new Element(attributeName);
         writeOutputParameter(rest, attributeValue, paramElem);
         builder.addChild(paramElem);
      }
   }
}
