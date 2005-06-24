/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

/**
 * Specification of a param combo.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class ParamCombo {
   
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
    * Creates a new instance of ParamCombo
    *
    * @param type
    *    The type of the param-combo.
    * @param parameters
    *    The parameters this param-combo refers to.
    */
   public ParamCombo(String type, Parameter[] parameters) {
      _type = type;
      _parameters = parameters;
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   /**
    * The type of the param-combo.
    */
   private final String _type;
   
   /**
    * The parameters of this param-combo.
    */
   private final Parameter[] _parameters;
   
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Returns whether the param combo is a all-or-none type.
    *
    * @return
    *    <code>true</code> if the type is all-or-none, <code>false</code> otherwise.
    */
   public boolean isAllOrNone() {
      
      return _type.equals("all-or-none");
   }
   
   /**
    * Returns whether the param combo is a exclusive-or type.
    *
    * @return
    *    <code>true</code> if the type is exclusive-or, <code>false</code> otherwise.
    */
   public boolean isExclusiveOr() {
      
      return _type.equals("exclusive-or");
   }

   /**
    * Returns whether the param combo is a inclusive-or type.
    *
    * @return
    *    <code>true</code> if the type is inclusive-or, <code>false</code> otherwise.
    */
   public boolean isInclusiveOr() {
      
      return _type.equals("inclusive-or");
   }
   
   /**
    * Gets the parameters defined in the param combo.
    *
    * @return
    *    The specification of the parameters defined in the param combo, never <code>null</code>.
    */
   public Parameter[] getParameters() {
      
      return _parameters;
   }
}
