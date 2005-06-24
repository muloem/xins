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
import java.util.List;
import org.xins.common.text.ParseException;

import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Specification of the error code (also known as result code).
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class ErrorCode {
   
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
    */
   public ErrorCode(String name, Class reference) throws InvalidSpecificationException {
      _errorCodeName = name;
      _reference = reference;
      
      try {
         InputStream in = reference.getResourceAsStream("/specs/" + name + ".rcd");
         InputStreamReader reader = new InputStreamReader(in);
         parseErrorCode(reader);
         reader.close();
         in.close();
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
   private Parameter[] _outputParameters;

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
    * Gets the output parameter specifications defined in the error code.
    *
    * @return
    *    The output parameters, never <code>null</code>.
    */
   public Parameter[] getOutputParameters() {
      
      return _outputParameters;
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
   
   private void parseErrorCode(Reader reader) throws IOException, InvalidSpecificationException {
      ElementParser parser = new ElementParser();
      Element errorCode = null;
      try {
         errorCode = parser.parse(reader);
      } catch (ParseException pe) {
         throw new InvalidSpecificationException(pe.getMessage());
      }
      Element descriptionElement = (Element) errorCode.getChildElements("description").get(0);
      _description = descriptionElement.getText();
      List output = errorCode.getChildElements("output");
      if (output.size() == 0) {
         _outputParameters = new Parameter[0];
         _outputDataSectionElements = new DataSectionElement[0];
      } else {
         
         // Output parameters
         Element outputElement = (Element) output.get(0);
         _outputParameters = Function.parseParameters(outputElement);

         // Data section
         List dataSections = outputElement.getChildElements("data");
         if (dataSections.size() == 0) {
            _outputDataSectionElements = new DataSectionElement[0];
         } else {
            Element dataSection = (Element) dataSections.get(0);
            // TODO String contains = dataSection.getAttribute("contains");
            _outputDataSectionElements = Function.parseDataSectionElements(dataSection, dataSection);
         }
      }
   }
}
