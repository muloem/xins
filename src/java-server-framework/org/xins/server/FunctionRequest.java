/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.collections.PropertyReader;
import org.xins.common.xml.Element;

/**
 * A function request.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public class FunctionRequest {
   
   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>FunctionRequest</code>.
    *
    * @param functionName
    *    the name of the function, cannot be <code>null</code>.
    *
    * @param parameters
    *    the parameters of the function requested, cannot be <code>null</code>
    *
    * @param dataElement
    *    the data section of the input request, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if 
    */
   public FunctionRequest(String functionName, PropertyReader parameters, Element dataElement) {
      _functionName = functionName;
      _parameters = parameters;
      _dataElement = dataElement;
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of the function.
    */
   private final String _functionName;
   
   /**
    * The parameters of the function.
    */
   private final PropertyReader _parameters;
   
   /**
    * The data section of the function.
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
    * Gets the parameters of the function.
    *
    * @return
    *    the parameters of the fucntion, never <code>null</code>.
    */
   PropertyReader getParameters() {
      return _parameters;
   }

   /**
    * Gets the data section of the request.
    *
    * @return
    *    the data section of the function or <code>null</code> if there is no 
    *    data section.
    */
   Element getDataElement() {
      return _dataElement;
   }
}
