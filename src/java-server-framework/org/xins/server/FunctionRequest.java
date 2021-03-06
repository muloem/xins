/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;
import org.xins.common.xml.Element;

/**
 * Function request. Consists of a function name, a set of parameters and a
 * data section. The function name is mandatory, while there may not be any
 * parameters nor data section.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@japplis.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.2.0
 */
public class FunctionRequest {

   /**
    * The name of the function. This field is never <code>null</code>.
    */
   private final String _functionName;

   /**
    * The parameters of the function. This field is never <code>null</code>
    */
   private final PropertyReader _parameters;

   /**
    * The data section of the function. If there is none, then this field is
    * <code>null</code>.
    */
   private final Element _dataElement;

   /**
    * Flag indicating whether the function should be skipped or not.
    */
   private final boolean _skipFunctionCall;

   /**
    * Creates a new <code>FunctionRequest</code>. The function name must be
    * specified.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters of the function requested, cannot be
    *    <code>null</code>.
    *
    * @param dataElement
    *    the data section of the input request, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    */
   public FunctionRequest(String         functionName,
                          PropertyReader parameters,
                          Element        dataElement)
   throws IllegalArgumentException {
       this(functionName, parameters, dataElement, false);
   }

   /**
    * Creates a new <code>FunctionRequest</code>. The function name must be
    * specified.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters of the function requested, cannot be
    *    <code>null</code>.
    *
    * @param dataElement
    *    the data section of the input request, can be <code>null</code>.
    *
    * @param skipFunctionCall
    *    <code>true</code> if the function shouldn't be executed, <code>false</code> otherwise.
    *
    * @throws IllegalArgumentException
    *    if <code>functionName == null</code>.
    *
    * @since XINS 2.0
    */
   public FunctionRequest(String         functionName,
                          PropertyReader parameters,
                          Element        dataElement,
                          boolean        skipFunctionCall)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName);

      // Store the function name (never null)
      _functionName = functionName;

      // Store the parameters, make sure this is never null
      if (parameters == null) {
         _parameters = PropertyReaderUtils.EMPTY_PROPERTY_READER;
      } else {
         _parameters = parameters;
      }

      // Store the data section, or null if there is none
      _dataElement = dataElement;

      _skipFunctionCall = skipFunctionCall;
   }

   /**
    * Gets the name of the function.
    *
    * @return
    *    the name of the function, never <code>null</code>.
    *
    * @since XINS 2.0
    */
   public String getFunctionName() {
      return _functionName;
   }

   /**
    * Gets the parameters of the function. The returned
    * {@link PropertyReader} instance is unmodifiable.
    *
    * @return
    *    the parameters of the function, never <code>null</code>.
    *
    * @since XINS 2.0
    */
   public PropertyReader getParameters() {
      return _parameters;
   }

   /**
    * Gets the data section of the request.
    *
    * @return
    *    the data section, or <code>null</code> if there is none.
    *
    * @since XINS 2.0
    */
   public Element getDataElement() {
      return _dataElement;
   }

   /**
    * Gets whether the function should be executed or not.
    *
    * @return
    *    <code>true</code> if the function shouldn't be executed, <code>false</code> otherwise.
    *
    * @since XINS 2.0
    */
   public boolean shouldSkipFunctionCall() {
      return _skipFunctionCall;
   }
}
