/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.CollectionUtils;

import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Specification of a error code (also known as result code).
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class ErrorCodeSpec extends Object {
   
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
    * Creates a new instance of ErrorCode
    *
    * @param name
    *    the name of the error code, cannot be <code>null</code>.
    *
    * @param reference
    *    the reference class used to get the type of the parameters, cannot be <code>null</code>.
    *
    * @param baseURL
    *    the reference class used to located the specifications, cannot be <code>null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the result code file cannot be found or is incorrect.
    */
   public ErrorCodeSpec(String name, Class reference, String baseURL) throws InvalidSpecificationException {
      _errorCodeName = name;
      _reference = reference;
      _baseURL = baseURL;
      
      try {
         Reader reader = APISpec.getReader(baseURL, name + ".rcd");
         parseErrorCode(reader);
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
   private final String _errorCodeName;
   
   /**
    * Description of the function.
    */
   private String _description;
   
   /**
    * The output parameters of the function.
    */
   private Map _outputParameters = new TreeMap(CollectionUtils.GREATER_COMPARATOR);

   /**
    * The output data section elements of the function.
    */
   private Map _outputDataSectionElements;

   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Gets the name of the error code.
    *
    * @return
    *    The name of the error code, never <code>null</code>.
    */
   public String getName() {
      
      return _errorCodeName;
   }
   
   /**
    * Gets the description of the error code.
    *
    * @return
    *    The description of the error code, never <code>null</code>.
    */
   public String getDescription() {
      
      return _description;
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
    *    if the error code does not contain any output parameter with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>parameterName == null</code>.
    */
   public ParameterSpec getOutputParameter(String parameterName)
   throws EntityNotFoundException, IllegalArgumentException {
      
      MandatoryArgumentChecker.check("parameterName", parameterName);
      ParameterSpec parameter = (ParameterSpec) _outputParameters.get(parameterName);
      
      if (parameter == null) {
         throw new EntityNotFoundException("Output parameter \"" + parameterName 
               + "\" not found in the error code \"" + _errorCodeName +"\".");
      }
      
      return parameter;
   }
   
   /**
    * Gets the output parameter specifications defined in the error code.
    * The key is the name of the parameter, the value is the {@link ParameterSpec} object.
    *
    * @return
    *    The output parameters specifications, never <code>null</code>.
    */
   public Map getOutputParameters() {

      return _outputParameters;
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
    *    if the error code does not define any output data element with the specified name.
    *
    * @throws IllegalArgumentException
    *    if <code>elementName == null</code>.
    */
   public DataSectionElementSpec getOutputDataSectionElement(String elementName) 
   throws EntityNotFoundException, IllegalArgumentException {
      
      MandatoryArgumentChecker.check("elementName", elementName);
      
      DataSectionElementSpec element = (DataSectionElementSpec) _outputDataSectionElements.get(elementName);
      
      if (element == null) {
         throw new EntityNotFoundException("Output data section element \"" + elementName
               + "\" not found in the error code \"" + _errorCodeName +"\".");
      }
      
      return element;
   }

   /**
    * Gets the specification of the elements of the output data section.
    * The key is the name of the element, the value is the {@link DataSectionElementSpec} object.
    *
    * @return
    *   The specification of the output data section, never <code>null</code>.
    */
   public Map getOutputDataSectionElements() {
      
      return _outputDataSectionElements;
   }
   
   /**
    * Parses the result code file.
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
   private void parseErrorCode(Reader reader) throws IOException, InvalidSpecificationException {
      ElementParser parser = new ElementParser();
      Element errorCode = null;
      try {
         errorCode = parser.parse(reader);
      } catch (ParseException pe) {
         throw new InvalidSpecificationException("[ErrorCode:" + _errorCodeName + "] " + pe.getMessage());
      }
      Element descriptionElement = (Element) errorCode.getChildElements("description").get(0);
      _description = descriptionElement.getText();
      List output = errorCode.getChildElements("output");
      if (output.size() > 0) {
         
         // Output parameters
         Element outputElement = (Element) output.get(0);
         _outputParameters = FunctionSpec.parseParameters(_reference, outputElement);

         // Data section
         List dataSections = outputElement.getChildElements("data");
         if (dataSections.size() > 0) {
            Element dataSection = (Element) dataSections.get(0);
            // TODO String contains = dataSection.getAttribute("contains");
            _outputDataSectionElements = FunctionSpec.parseDataSectionElements(_reference, dataSection, dataSection);
         }
      }
   }
}
