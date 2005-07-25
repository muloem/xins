/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Specification of a error code (also known as result code).
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class ErrorCode extends Object {
   
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
   public ErrorCode(String name, Class reference, String baseURL) throws InvalidSpecificationException {
      _errorCodeName = name;
      _reference = reference;
      _baseURL = baseURL;
      
      try {
         Reader reader = API.getReader(baseURL, name + ".rcd");
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
   private Map _outputParameters = new HashMap();

   /**
    * The output data section elements of the function.
    */
   private DataSectionElement[] _outputDataSectionElements;

   
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
    * @throws IllegalArgumentException
    *    if the error code does not contain any output parameter with the specified name.
    */
   public Parameter getOutputParameter(String parameterName) throws IllegalArgumentException {
      Parameter parameter = (Parameter) _outputParameters.get(parameterName);
      
      if (parameter == null) {
         throw new IllegalArgumentException("Output parameter \"" + parameterName + "\" not found.");
      }
      
      return parameter;
   }
   
   /**
    * Gets the output parameter specifications defined in the error code.
    *
    * @return
    *    The output parameters, never <code>null</code>.
    */
   public Parameter[] getOutputParameters() {
      
      int parametersCount = _outputParameters.size();
      Parameter[] result = new Parameter[parametersCount];
      
      Iterator itParameters = _outputParameters.values().iterator();
      int i = 0;
      while (itParameters.hasNext()) {
         Parameter nextParameter = (Parameter) itParameters.next();
         result[i++] = nextParameter;
      }
      return result;
   }
   
   /**
    * Gets the specification of the elements of the output data section.
    *
    * @return
    *   The specification of the output data section, never <code>null</code>.
    */
   public DataSectionElement[] getOutputDataSection() {
      
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
      if (output.size() == 0) {
         _outputDataSectionElements = new DataSectionElement[0];
      } else {
         
         // Output parameters
         Element outputElement = (Element) output.get(0);
         _outputParameters = Function.parseParameters(_reference, outputElement);

         // Data section
         List dataSections = outputElement.getChildElements("data");
         if (dataSections.size() == 0) {
            _outputDataSectionElements = new DataSectionElement[0];
         } else {
            Element dataSection = (Element) dataSections.get(0);
            // TODO String contains = dataSection.getAttribute("contains");
            _outputDataSectionElements = Function.parseDataSectionElements(_reference, dataSection, dataSection);
         }
      }
   }
}
