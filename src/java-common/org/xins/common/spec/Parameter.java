/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

import org.xins.common.types.Type;

/**
 * Specification of the parameter.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class Parameter {
   
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
    * Creates a new instance of Parameter.
    *
    * @param name
    *    the name of the parameter
    * @param type
    *    the type of the parameter
    * @param required
    *    <code>true</code> if the parameter is required, <code>false</code> otherwise.
    * @param description
    *    the description of the parameter.
    */
   public Parameter(String name, String type, boolean required, String description) {
      _parameterName = name;
      _parameterType = type;
      _required = required;
      _description = description;
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   /**
    * Name of the parameter.
    */
   private final String _parameterName;
   
   /**
    * Type of the parameter.
    */
   private final String _parameterType;
   
   /**
    * Flags indicating if this parameter is required.
    */
   private final boolean _required;
   
   /**
    * Description of the parameter.
    */
   private String _description;
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Gets the name of the parameter.
    *
    * @return
    *    The name of the parameter, never <code>null</code>.
    */
   public String getName() {
      
      return _parameterName;
   }
   
   /**
    * Gets the description of the parameter.
    *
    * @return
    *    The description of the parameter, never <code>null</code>.
    */
   public String getDescription() {
      
      return _description;
   }

   /**
    * Returns whether the parameter is mandatory.
    *
    * @return
    *    <code>true</code> if the parameter is requierd, <code>false</code> otherwise.
    */
   public boolean isRequired() {
      
      return _required;
   }

   /**
    * Gets the type of the parameter.
    *
    * @return
    *    The type of the parameter, never <code>null</code>.
    */
   public Type getType() {
      
      // TODO implement this function
      return null;
   }
}
