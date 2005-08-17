/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Specification of a function.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class FunctionSpec extends Object {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>Function</code> by parsing the .fnc file.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @param reference
    *    the reference class used to get the type of the parameters, cannot be <code>null</code>.
    *
    * @param baseURL
    *    the reference class used to located the specifications, cannot be <code>null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect or cannot be found.
    */
   FunctionSpec(String functionName,
            Class  reference,
            String baseURL)
   throws InvalidSpecificationException {

      _reference    = reference;
      _baseURL      = baseURL;
      _functionName = functionName;

      try {
         Reader reader = APISpec.getReader(baseURL, functionName + ".fnc");
         parseFunction(reader);
      } catch (IOException ioe) {
         throw new InvalidSpecificationException(ioe.getMessage());
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   /**
    * The class used as reference.
    */
   private final Class _reference;
   
   /**
    * The base URL used to locate the specifications.
    */
   private final String _baseURL;
   
   /**
    * Name of the function.
    */
   private final String _functionName;
   
   /**
    * Description of the function.
    */
   private String _description;
   
   /**
    * The input parameters of the function.
    * The key is the name of the parameter, the value is the {@link FunctionSpec} object.
    */
   private Map _inputParameters = new LinkedHashMap();

   /**
    * The input param combos of the function.
    */
   private List _inputParamCombos = new ArrayList();

   /**
    * The input data section elements of the function.
    */
   private Map _inputDataSectionElements = new LinkedHashMap();

   /**
    * The defined error code that the function can return.
    */
   private Map _errorCodes = new LinkedHashMap();

   /**
    * The output parameters of the function.
    * The key is the name of the parameter, the value is the <code>Parameter</code> object.
    */
   private Map _outputParameters = new LinkedHashMap();

   /**
    * The output param combos of the function.
    */
   private List _outputParamCombos = new ArrayList();

   /**
    * The output data section elements of the function.
    */
   private Map _outputDataSectionElements = new LinkedHashMap();

   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the name of the function.
    *
    * @return
    *    The name of the function, never <code>null</code>.
    */
   public String getName() {

      return _functionName;
   }

   /**
    * Gets the description of the function.
    *
    * @return
    *    The description of the function, never <code>null</code>.
    */
   public String getDescription() {
      
      return _description;
   }

   /**
    * Gets the input parameter for the specified name.
    *
    * @param parameterName
    *    the name of the parameter, cannot be <code>null</code>.
    *
    * @return
    *    the parameter, never <code>null</code>.
    *
    * @throws EntityNotFoundException
    *    if the function does not contain any input parameter with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>parameterName == null</code>.
    */
   public ParameterSpec getInputParameter(String parameterName)
   throws EntityNotFoundException, IllegalArgumentException {
       
      MandatoryArgumentChecker.check("parameterName", parameterName);
      
      ParameterSpec parameter = (ParameterSpec) _inputParameters.get(parameterName);
      
      if (parameter == null) {
         throw new EntityNotFoundException("Input parameter \"" + parameterName + "\" not found.");
      }
      
      return parameter;
   }
   
   /**
    * Gets the input parameter specifications defined in the function.
    * The key is the name of the parameter, the value is the {@link ParameterSpec} object.
    *
    * @return
    *    The input parameters, never <code>null</code>.
    */
   public Map getInputParameters() {
      
      return _inputParameters;
   }

   /**
    * Gets the output parameter for the specified name.
    *
    * @param parameterName
    *    the name of the parameter, cannot be <code>null</code>.
    *
    * @return
    *    the parameter, never <code>null</code>.
    *
    * @throws EntityNotFoundException
    *    if the function does not contain any output parameter with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>parameterName == null</code>.
    */
   public ParameterSpec getOutputParameter(String parameterName)
   throws EntityNotFoundException, IllegalArgumentException {
       
      MandatoryArgumentChecker.check("parameterName", parameterName);
      
      ParameterSpec parameter = (ParameterSpec) _outputParameters.get(parameterName);
      
      if (parameter == null) {
         throw new EntityNotFoundException("Output parameter \"" + parameterName + "\" not found.");
      }
      
      return parameter;
   }
   
   /**
    * Gets the output parameter specifications defined in the function.
    * The key is the name of the parameter, the value is the {@link ParameterSpec} object.
    *
    * @return
    *    The output parameters, never <code>null</code>.
    */
   public Map getOutputParameters() {
      
      return _outputParameters;
   }

   /**
    * Gets the error code specification for the specified error code.
    *
    * @param errorCodeName
    *    the name of the error code, cannot be <code>null</code>.
    *
    * @return
    *    The error code specifications, never <code>null</code>.
    *
    * @throws EntityNotFoundException
    *    if the function does not define any error code with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>errorCodeName == null</code>.
    */
   public ErrorCodeSpec getErrorCode(String errorCodeName)
   throws EntityNotFoundException, IllegalArgumentException {
      
      MandatoryArgumentChecker.check("errorCodeName", errorCodeName);
      ErrorCodeSpec errorCode = (ErrorCodeSpec) _errorCodes.get(errorCodeName);
      
      if (errorCode == null) {
         throw new EntityNotFoundException("Error code \"" + errorCodeName + "\" not found.");
      }
      
      return errorCode;
   }

   /**
    * Gets the error code specifications defined in the function.
    * The standard error code are not included.
    * The key is the name of the error code, the value is the {@link ErrorCodeSpec} object.
    *
    * @return
    *    The error code specifications, never <code>null</code>.
    */
   public Map getErrorCodes() {
      
      return _errorCodes;
   }

   /**
    * Gets the specification of the element of the input data section with the
    * specified name.
    *
    * @param elementName
    *    the name of the element, cannot be <code>null</code>.
    *
    * @return
    *   The specification of the input data section element, never <code>null</code>.
    *
    * @throws EntityNotFoundException
    *    if the function does not define any input data element with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>elementName == null</code>.
    */
   public DataSectionElementSpec getInputDataSectionElement(String elementName)
   throws EntityNotFoundException, IllegalArgumentException {
      
      MandatoryArgumentChecker.check("elementName", elementName);
      
      DataSectionElementSpec element = (DataSectionElementSpec) _inputDataSectionElements.get(elementName);
      
      if (element == null) {
         throw new EntityNotFoundException("Input data section element \"" + elementName + "\" not found.");
      }
      
      return element;
   }

   /**
    * Gets the specification of the elements of the input data section.
    * The key is the name of the element, the value is the {@link DataSectionElementSpec} object.
    *
    * @return
    *   The input data section elements, never <code>null</code>.
    */
   public Map getInputDataSectionElements() {
      
      return _inputDataSectionElements;
   }

   /**
    * Gets the specification of the element of the output data section with the
    * specified name.
    *
    * @param elementName
    *    the name of the element, cannot be <code>null</code>.
    *
    * @return
    *   The specification of the output data section element, never <code>null</code>.
    *
    * @throws EntityNotFoundException
    *    if the function does not define any output data element with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>elementName == null</code>.
    */
   public DataSectionElementSpec getOutputDataSectionElement(String elementName) 
   throws EntityNotFoundException, IllegalArgumentException {
      
      MandatoryArgumentChecker.check("elementName", elementName);
      
      DataSectionElementSpec element = (DataSectionElementSpec) _outputDataSectionElements.get(elementName);
      
      if (element == null) {
         throw new EntityNotFoundException("Output data section element \"" + elementName + "\" not found.");
      }
      
      return element;
   }

   /**
    * Gets the specification of the elements of the output data section.
    * The key is the name of the element, the value is the {@link DataSectionElementSpec} object.
    *
    * @return
    *   The output data section elements, never <code>null</code>.
    */
   public Map getOutputDataSectionElements() {
      
      return _outputDataSectionElements;
   }

   /**
    * Gets the input param combo specifications.
    *
    * @return
    *    The list of the input param combos specification 
    *    ({@link ParamComboSpec}), never <code>null</code>.
    */
   public List getInputParamCombos() {
      
      return _inputParamCombos;
   }

   /**
    * Gets the output param combo specifications.
    *
    * @return
    *    The list of the output param combos specification 
    *    ({@link ParamComboSpec}), never <code>null</code>.
    */
   public List getOutputParamCombos() {
      
      return _outputParamCombos;
   }
   
   /**
    * Parses the function specification file.
    *
    * @param reader
    *    the reader that contains the content of the result code file, cannot be <code>null</code>.
    *
    * @throws IOException
    *    if the parser cannot read the content.
    *
    * @throws InvalidSpecificationException
    *    if the result code file is incorrect.
    */
   private void parseFunction(Reader reader) throws IOException, InvalidSpecificationException {
      ElementParser parser = new ElementParser();
      Element function = null;
      try {
         function = parser.parse(reader);
      } catch (ParseException pe) {
         throw new InvalidSpecificationException("[Function:" + _functionName + "] " + pe.getMessage());
      }
      Element descriptionElement = (Element) function.getChildElements("description").get(0);
      _description = descriptionElement.getText();
      List input = function.getChildElements("input");
      if (input.size() > 0) {
         
         // Input parameters
         Element inputElement = (Element) input.get(0);
         _inputParameters = parseParameters(_reference, inputElement);

         // Param combos
         _inputParamCombos = parseParamCombos(_reference, inputElement, _inputParameters);
         
         // Data section
         List dataSections = inputElement.getChildElements("data");
         if (dataSections.size() > 0) {
            Element dataSection = (Element) dataSections.get(0);
            _inputDataSectionElements = parseDataSectionElements(_reference, dataSection, dataSection);
         }
      }
      
      List output = function.getChildElements("output");
      if (output.size() > 0) {
         Element outputElement = (Element) output.get(0);
         
         // Error codes
         List errorCodesList = outputElement.getChildElements("resultcode-ref");
         Iterator itErrorCodes = errorCodesList.iterator();
         while (itErrorCodes.hasNext()) {
            Element nextErrorCode = (Element) itErrorCodes.next();
            String errorCodeName = nextErrorCode.getAttribute("name");
            ErrorCodeSpec errorCodeSpec = new ErrorCodeSpec(errorCodeName, _reference, _baseURL);
            _errorCodes.put(errorCodeName, errorCodeSpec);
         }
         
         // Output parameters
         _outputParameters = parseParameters(_reference, outputElement);

         // Param combos
         _outputParamCombos = parseParamCombos(_reference, outputElement, _outputParameters);
         
         // Data section
         List dataSections = outputElement.getChildElements("data");
         if (dataSections.size() > 0) {
            Element dataSection = (Element) dataSections.get(0);
            _outputDataSectionElements = parseDataSectionElements(_reference, dataSection, dataSection);
         }
      }
   }
   
   /**
    * Parse an element in the data section.
    *
    * @param reference
    *    the reference class used to locate the files.
    *
    * @param topElement
    *    the element to parse, cannot be <code>null</code>.
    *
    * @param dataSection
    *    the data section, cannot be <code>null</code>.
    *
    * @return
    *    the top elements of the data section, or an empty array there is no
    *    data section.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect.
    */
   static Map parseDataSectionElements(Class reference, Element topElement, Element dataSection)
   throws InvalidSpecificationException {

      Map dataSectionElements = new LinkedHashMap();
      
      // The <data> may have a "contains" attribute.
      String dataContainsAttr = topElement.getAttribute("contains");
      if (dataContainsAttr != null) {
         DataSectionElementSpec dataSectionElement = getDataSectionElement(reference, dataContainsAttr, dataSection);
         dataSectionElements.put(dataContainsAttr, dataSectionElement);
      }
      
      // Gets the sub elements of this element
      List dataSectionContains = topElement.getChildElements("contains");
      if (!dataSectionContains.isEmpty()) {
         Element containsElement = (Element) dataSectionContains.get(0);
         List contained = containsElement.getChildElements("contained");
         Iterator itContained = contained.iterator();
         while (itContained.hasNext()) {
            Element containedElement = (Element) itContained.next();
            String name = containedElement.getAttribute("element");
            DataSectionElementSpec dataSectionElement = getDataSectionElement(reference, name, dataSection);
            dataSectionElements.put(name, dataSectionElement);
         }
      }
      return dataSectionElements;
   }

   /**
    * Gets the specified element in the data section.
    *
    * @param reference
    *    the reference class used to locate the files, cannot be <code>null</code>.
    *
    * @param name
    *    the name of the element to retreive, cannot be <code>null</code>.
    *
    * @param dataSection
    *    the data section, cannot be <code>null</code>.
    *
    * @return
    *    the data section element.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect.
    */
   static DataSectionElementSpec getDataSectionElement(Class reference, String name, Element dataSection) throws InvalidSpecificationException {
      Iterator itElements = dataSection.getChildElements("element").iterator();
      while (itElements.hasNext()) {
         Element nextElement = (Element) itElements.next();
         String nextName = nextElement.getAttribute("name");
         if (nextName.equals(name)) {
            
            String description = ((Element) nextElement.getChildElements("description").get(0)).getText();
            
            Map subElements = parseDataSectionElements(reference, nextElement, dataSection);
            
            boolean isPcdataEnable = false;
            List dataSectionContains = nextElement.getChildElements("contains");
            if (!dataSectionContains.isEmpty()) {
               Element containsElement = (Element) dataSectionContains.get(0);
               List pcdata = containsElement.getChildElements("pcdata");
               if (!pcdata.isEmpty()) {
                  isPcdataEnable = true;
               }
            }
            
            List attributesList = nextElement.getChildElements("attribute");
            Map attributes = new LinkedHashMap();
            Iterator itAttributes = attributesList.iterator();
            while (itAttributes.hasNext()) {
               ParameterSpec attribute = parseParameter(reference, (Element) itAttributes.next());
               attributes.put(attribute.getName(), attribute);
            }
            
            DataSectionElementSpec result = new DataSectionElementSpec(nextName, description, isPcdataEnable, subElements, attributes);
            return result;
         }
      }
      return null;
   }
   
   /**
    * Parses a function parameter or an attribute of a data section element.
    *
    * @param reference
    *    the reference class used to locate the files, cannot be <code>null</code>.
    *
    * @param paramElement
    *    the element that contains the specification of the parameter, cannot be <code>null</code>.
    *
    * @return
    *    the parameter.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect.
    */
   static ParameterSpec parseParameter(Class reference, Element paramElement) throws InvalidSpecificationException {
      String parameterName = paramElement.getAttribute("name");
      String parameterTypeName = paramElement.getAttribute("type");
      boolean requiredParameter = "true".equals(paramElement.getAttribute("required"));
      String parameterDescription = ((Element) paramElement.getChildElements("description").get(0)).getText();
      ParameterSpec parameter = new ParameterSpec(reference ,parameterName, parameterTypeName, requiredParameter, parameterDescription);
      return parameter;
   }
   
   /**
    * Parses the input or output parameters.
    *
    * @param reference
    *    the reference class used to locate the files, cannot be <code>null</code>.
    *
    * @param topElement
    *    the input or output element, cannot be <code>null</code>.
    *
    * @return
    *    a map containing the parameter names as keys, and the 
    *    <code>Parameter</code> objects as value.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect.
    */
   static Map parseParameters(Class reference, Element topElement) throws InvalidSpecificationException {
      List parametersList = topElement.getChildElements("param");
      Map parameters = new LinkedHashMap();
      Iterator itParameters = parametersList.iterator();
      while (itParameters.hasNext()) {
         Element nextParameter = (Element) itParameters.next();
         ParameterSpec parameter = parseParameter(reference, nextParameter);
         parameters.put(parameter.getName(), parameter);
      }
      return parameters;
   }
   
   /**
    * Parses the param-combo element.
    *
    * @param reference
    *    the reference class used to locate the files, cannot be <code>null</code>.
    *
    * @param topElement
    *    the input or output element, cannot be <code>null</code>.
    *
    * @param parameters
    *    the list of the input or output parameters, cannot be <code>null</code>.
    *
    * @return
    *    the param-combo elements or an empty array if no param-combo is defined.
    */
   static List parseParamCombos(Class   reference,
                                Element topElement,
                                Map     parameters) {
      
      List paramCombosList = topElement.getChildElements("param-combo");
      List paramCombos = new ArrayList(paramCombosList.size());
      Iterator itParamCombos = paramCombosList.iterator();
      while (itParamCombos.hasNext()) {
         Element nextParamCombo = (Element) itParamCombos.next();
         String type = nextParamCombo.getAttribute("type");
         List paramDefs = nextParamCombo.getChildElements("param-ref");
         Iterator itParamDefs = paramDefs.iterator();
         Map paramComboParameters = new LinkedHashMap(paramDefs.size());
         while (itParamDefs.hasNext()) {
            Element paramDef = (Element) itParamDefs.next();
            String parameterName = paramDef.getAttribute("name");
            ParameterSpec parameter = (ParameterSpec) parameters.get(parameterName);
            paramComboParameters.put(parameterName, parameter);
         }
         ParamComboSpec paramCombo = new ParamComboSpec(type, paramComboParameters);
         paramCombos.add(paramCombo);
      }
      return paramCombos;
   }
}
