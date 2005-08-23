/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.FastStringBuffer;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Specification of an API.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class APISpec extends Object {
   
   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------
   
   /**
    * Gets the content of the file without the DTD declaration.
    *
    * @param baseURL
    *    the base URL used to located the specifications, cannot be <code>null</code>.
    *
    * @param fileName
    *    the name of the file that contains the specifications, cannot be <code>null</code>.
    *
    * @return
    *    the content of the file, never <code>null</code>.
    *
    * @throws InvalidSpecificationException
    *    if the specified file cannot be found.
    *
    * @throws IOException
    *    if the specification cannot be read.
    */
   static Reader getReader(String baseURL, String fileName)
   throws InvalidSpecificationException, IOException {
      
      URL fileURL = new URL(baseURL + fileName);
      InputStream in = fileURL.openStream();
      if (in == null) {
         throw new InvalidSpecificationException("File \"" + fileName +"\" not found in the specifications.");
      }
      
      // Return the XML file without the DTD declaration
      BufferedReader contentReader = new BufferedReader(new InputStreamReader(in));
      FastStringBuffer content = new FastStringBuffer(1024);
      String nextLine = "";
      while (nextLine != null) {
         nextLine = contentReader.readLine();
         if (nextLine != null) {
            content.append(nextLine);
            content.append("\n");
         }
      }
      String xmlContentString = content.toString();
      int beginDTD = xmlContentString.indexOf("<!DOCTYPE ");
      int endDTD = xmlContentString.indexOf(".dtd\">", beginDTD) + 6;
      String xmlWithoutDTD = xmlContentString.substring(0, beginDTD) +
            xmlContentString.substring(endDTD);
      StringReader reader = new StringReader(xmlWithoutDTD.trim());
      return reader;
   }
   

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------
   
   /**
    * Creates a new instance of API
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
   public APISpec(Class reference, String baseURL) throws InvalidSpecificationException {
      _reference = reference;
      _baseURL = baseURL;
      try {
         Reader reader = getReader(baseURL, "api.xml");
         parseApi(reader);
      } catch (IOException ioe) {
         throw new InvalidSpecificationException("I/O Exception:" + ioe.getMessage());
      }
   }
   
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   /**
    * The refence class.
    */
   private final Class _reference;
   
   /**
    * The base URL used to locate the specifications.
    */
   private final String _baseURL;
   
   /**
    * Name of the API.
    */
   private String _apiName;
   
   /**
    * Owner of the API.
    */
   private String _owner;
   
   /**
    * Description of the API.
    */
   private String _description;
   
   /**
    * The functions of the API.
    */
   private Map _functions = new HashMap();

   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Gets the name of the API.
    *
    * @return
    *    The name of the API, never <code>null</code>.
    */
   public String getName() {

      return _apiName;
   }
   
   /**
    * Gets the owner of the API.
    *
    * @return
    *    The owner of the API or <code>null</code> if no owner is defined.
    */
   public String getOwner() {
      
      return _owner;
   }
   
   /**
    * Gets the description of the API.
    *
    * @return
    *    The description of the API, never <code>null</code>.
    */
   public String getDescription() {
      
      return _description;
   }

   /**
    * Gets the function specifications defined in the API.
    * The key of the returned Map is the name of the function and the value
    * is the {@link FunctionSpec} object.
    *
    * @return
    *    The function specifications, never <code>null</code>.
    */
   public Map getFunctions() {
      
      return _functions;
   }

   /**
    * Get the specification of the given function.
    *
    * @param functionName
    *    The name of the function, can not be <code>null</code>
    *
    * @return
    *    The function specification.
    *
    * @throws EntityNotFoundException
    *    If the API does not define any function for the given name.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public FunctionSpec getFunction(String functionName)
   throws EntityNotFoundException, IllegalArgumentException {

      MandatoryArgumentChecker.check("functionName", functionName);
      
      FunctionSpec function = (FunctionSpec) _functions.get(functionName);
      
      if (function == null) {
         throw new EntityNotFoundException("Function \"" + functionName + "\" not found.");
      }
      
      return function;
   }

   /**
    * Parses the api.xml file.
    *
    * @param reader
    *    the reader that contain the content of the api.xml file, cannot be <code>null</code>.
    *
    * @throws IOException
    *    if one of the specification file cannot be read correctly.
    *
    * @throws InvalidSpecificationException
    *    if the specification is incorrect.
    */
   private void parseApi(Reader reader) throws IOException, InvalidSpecificationException {
      ElementParser parser = new ElementParser();
      Element api = null;
      try {
         api = parser.parse(reader);
      } catch (ParseException pe) {
         throw new InvalidSpecificationException("[API] " + pe.getMessage());
      }
      _apiName = api.getAttribute("name");
      _owner = api.getAttribute("owner");
      Element descriptionElement = (Element) api.getChildElements("description").get(0);
      _description = descriptionElement.getText();
      Iterator functions = api.getChildElements("function").iterator();
      while (functions.hasNext()) {
         Element nextFunction = (Element) functions.next();
         String functionName = nextFunction.getAttribute("name");
         FunctionSpec function = new FunctionSpec(functionName, _reference, _baseURL);
         _functions.put(functionName, function);
      }
   }
}