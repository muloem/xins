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
import java.util.List;
import java.util.Map;
import org.xins.common.text.ParseException;

import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

/**
 * Specification of an API.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class API {
   
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
    * Creates a new instance of API
    *
    * @param reference
    *    the reference class used to located the specifications.
    */
   public API(Class reference) throws InvalidSpecificationException {
      _reference = reference;
      try {
         InputStream in = reference.getResourceAsStream("/specs/api.xml");
         InputStreamReader reader = new InputStreamReader(in);
         parseApi(reader);
         reader.close();
         in.close();
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
    * Cache for the functions of the API.
    * The key is the name of the function, the value is the Function object.
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
    *
    * @return
    *    The function specifications, never <code>null</code>.
    */
   public Function[] getFunctions() {
      
      int functionsCount = _functions.size();
      Function[] result = new Function[functionsCount];
      
      Iterator itFunctions = _functions.values().iterator();
      int i = 0;
      while (itFunctions.hasNext()) {
         Function nextFunction = (Function) itFunctions.next();
         result[i++] = nextFunction;
      }
      return result;
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
    * @throws IllegalArgumentException
    *    If the API does not define any function for the given name.
    */
   public Function getFunction(String functionName) {

      Function function = (Function) _functions.get(functionName);
      
      if (function == null) {
         throw new IllegalArgumentException("Function \"" + functionName + "\" not found.");
      }
      
      return function;
   }

   private void parseApi(Reader reader) throws IOException, InvalidSpecificationException {
      ElementParser parser = new ElementParser();
      Element api = null;
      try {
         api = parser.parse(reader);
      } catch (ParseException pe) {
         throw new InvalidSpecificationException(pe.getMessage());
      }
      _apiName = api.getAttribute("name");
      _owner = api.getAttribute("owner");
      Element descriptionElement = (Element) api.getChildElements("description").get(0);
      _description = descriptionElement.getText();
      Iterator functions = api.getChildElements("function").iterator();
      while (functions.hasNext()) {
         Element nextFunction = (Element) functions.next();
         String functionName = nextFunction.getAttribute("name");
         Function function = new Function(functionName, _reference);
         _functions.put(functionName, function);
      }
   }
}
