/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;

/**
 * Result from a function call.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public class FunctionResult {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

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
      _parameters = parameters;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The result code. This field is <code>null</code> if no code was
    * returned.
    */
   private String _code;

   /**
    * The parameters and their values. This field is never <code>null</code>.
    * If there are no parameters, then this field is <code>null</code>.
    */
   private BasicPropertyReader _parameters;

   /**
    * The data element. This field is <code>null</code> if there is no data
    * element.
    */
   private Element _dataElement;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

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
      if (_parameters == null) {
         _parameters = new BasicPropertyReader();
      }

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
   PropertyReader getParameters() {
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
    *    not <code>null</code>.
    */
   protected String getParameter(String name) {
      return _parameters.get(name);
   }

   /**
    * Add a new Element to the data element.
    *
    * @param element
    *    the new element to add to the result, cannot be <code>null</code>.
    */
   protected void add(Element element) {
      if (_dataElement == null) {
         _dataElement = new Element("data");
      }
      _dataElement.add(element);
   }

   /**
    * Gets the Data element from this result.
    *
    * @return
    *    the data element of the result, can be <code>null</code>.
    */
   Element getDataElement() {
      return _dataElement;
   }
}
