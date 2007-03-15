/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementBuilder;

/**
 * Result from a function call.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 *
 * @since XINS 1.0.0
 */
public class FunctionResult {

   /**
    * The result code. This field is <code>null</code> if no code was
    * returned.
    */
   private String _code;

   /**
    * The parameters and their values. This field is never <code>null</code>.
    */
   private BasicPropertyReader _parameters;

   /**
    * The data element builder. This field is <code>null</code> if there is no
    * data element.
    */
   private ElementBuilder _dataElementBuilder;

   /**
    * Creates a new successful <code>FunctionResult</code> instance.
    */
   public FunctionResult() {
      this(null, null);
   }

   /**
    * Creates a new <code>FunctionResult</code> instance.
    *
    * @param code
    *    the error code, can be <code>null</code> if the result is successful.
    */
   public FunctionResult(String code) {
      this(code, null);
   }

   /**
    * Creates a new <code>FunctionResult</code> instance.
    *
    * @param code
    *    the error code, can be <code>null</code> if the result is successful.
    * @param parameters
    *    the parameters for the result.
    */
   public FunctionResult(String code, BasicPropertyReader parameters) {
      _code = code;
      if (parameters == null) {
          _parameters = new BasicPropertyReader();
      } else {
        _parameters = parameters;
      }
   }

   /**
    * Returns the result code.
    *
    * @return
    *    the result code or <code>null</code> if no code was returned.
    */
   public String getErrorCode() {
      return _code;
   }

   /**
    * Checks that the output parameters are set as specified. If a parameter
    * is missing or if the value for it is invalid, then an
    * <code>InvalidResponseResult</code> is returned. Otherwise the parameters
    * are considered valid, and <code>null</code> is returned.
    *
    * <p>The implementation of this method in class {@link FunctionResult}
    * always returns <code>null</code>.
    *
    * @return
    *    an {@link InvalidResponseResult} instance if at least one output
    *    parameter is missing or invalid, or <code>null</code> otherwise.
    */
   protected InvalidResponseResult checkOutputParameters() {
      return null;
   }

   /**
    * Adds an output parameter to the result. The name and the value must
    * both be specified.
    *
    * @param name
    *    the name of the output parameter, not <code>null</code> and not an
    *    empty string.
    *
    * @param value
    *    the value of the output parameter, not <code>null</code> and not an
    *    empty string.
    */
   protected void param(String name, String value) {

      // This will erase any value set before with the same name.
      _parameters.set(name, value);
   }

   /**
    * Gets all parameters.
    *
    * @return
    *    a {@link PropertyReader} containing all parameters, or
    *    <code>null</code> if no parameters are set; the keys will be the
    *    names of the parameters ({@link String} objects, cannot be
    *    <code>null</code>), the values will be the parameter values
    *    ({@link String} objects as well, cannot be <code>null</code>).
    */
   public PropertyReader getParameters() {
      return _parameters;
   }

   /**
    * Gets the value of the specified parameter.
    *
    * @param name
    *    the parameter element name, not <code>null</code>.
    *
    * @return
    *    string containing the value of the parameter element,
    *    or <code>null</code> if the value is not set.
    */
   public String getParameter(String name) {
      return _parameters.get(name);
   }

   /**
    * Adds a new <code>Element</code> to the data element.
    *
    * @param element
    *    the new element to add to the result, cannot be <code>null</code>.
    *
    * @since XINS 1.1.0
    */
   protected void add(Element element) {
      if (_dataElementBuilder == null) {
         _dataElementBuilder = new ElementBuilder("data");
      }
      _dataElementBuilder.addChild(element);
   }

   /**
    * Gets the data element from this result.
    *
    * @return
    *    the data element of the result, can be <code>null</code>.
    */
   public Element getDataElement() {
      if (_dataElementBuilder == null) {
         return null;
      } else {
         return _dataElementBuilder.createElement();
      }
   }

   public String toString() {
      String asString = "";
      if (_code != null) {
         asString += "Error code: " + _code + "; ";
      } else {
         asString += "Successful result; ";
      }
      asString += PropertyReaderUtils.toString(_parameters, "no parameters") + "; ";
      if (_dataElementBuilder == null) {
         asString += "no data section";
      } else {
         asString += _dataElementBuilder.createElement().toString();
      }
      return asString;
   }
}
