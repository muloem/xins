/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;
import org.xins.common.collections.ProtectedPropertyReader;

import org.xins.common.xml.Element;

/**
 * A function request. Consists of a function name, a set of parameters and a
 * data section. The function name is mandatory, while there may not be any
 * parameters nor data section.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public class FunctionRequest {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Secret key used to control <code>ProtectedPropertyReader</code>
    * instances.
    */
   private static final Object SECRET_KEY = new Object();

   /**
    * A shared empty <code>ProtectedPropertyReader</code> instance.
    */
   private static final ProtectedPropertyReader EMPTY_PROPERTY_READER
      = new ProtectedPropertyReader(SECRET_KEY);


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

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
    *    if <code>functionName == null || parameters == null</code>.
    */
   public FunctionRequest(String         functionName,
                          PropertyReader parameters,
                          Element        dataElement)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionName", functionName);

      // Store the function name (never null)
      _functionName = functionName;

      // Store the parameters, make sure this is never null
      if (parameters != null) {
         _parameters = new ProtectedPropertyReader(SECRET_KEY);
         _parameters.copyFrom(SECRET_KEY, parameters);
      } else {
         _parameters = EMPTY_PROPERTY_READER;
      }

      // Store the data section, or null if there is none
      _dataElement = dataElement;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the function. This field is never <code>null</code>.
    */
   private final String _functionName;

   /**
    * The parameters of the function. This field is never <code>null</code>
    */
   private final ProtectedPropertyReader _parameters;

   /**
    * The data section of the function. If there is none, then this field is
    * <code>null</code>.
    */
   private final Element _dataElement;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the name of the function.
    *
    * @return
    *    the name of the function, never <code>null</code>.
    */
   String getFunctionName() {
      return _functionName;
   }

   /**
    * Gets the parameters of the function. The returned
    * {@link PropertyReader} instance is unmodifiable.
    *
    * @return
    *    the parameters of the function, never <code>null</code>.
    */
   PropertyReader getParameters() {
      return _parameters;
   }

   /**
    * Gets the data section of the request.
    *
    * @return
    *    the data section, or <code>null</code> if there is none.
    */
   Element getDataElement() {
      return _dataElement;
   }
}
