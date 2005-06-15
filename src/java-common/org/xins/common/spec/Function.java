/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

/**
 * Specification of the function.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class Function {
   
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
    * Creates a new instance of Function
    */
   public Function() {
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Gets the name of the function.
    *
    * @return
    *    The name of the function, never <code>null</code>.
    */
   public String getName() {
      
      // TODO implement this function
      return null;
   }
   
   /**
    * Gets the description of the function.
    *
    * @return
    *    The description of the function, never <code>null</code>.
    */
   public String getDescription() {
      
      // TODO implement this function
      return null;
   }

   /**
    * Gets the input parameter specifications defined in the function.
    *
    * @return
    *    The input parameters, never <code>null</code>.
    */
   public Parameter[] getInputParameters() {
      
      // TODO implement this function
      return null;
   }
   
   /**
    * Gets the output parameter specifications defined in the function.
    *
    * @return
    *    The output parameters, never <code>null</code>.
    */
   public Parameter[] getOutputParameters() {
      
      // TODO implement this function
      return null;
   }
   
   /**
    * Gets the error code specifications defined in the function.
    * The standard error code are not included.
    *
    * @return
    *    The error code specifications, never <code>null</code>.
    */
   public ErrorCode[] getErrorCodes() {
      
      // TODO implement this function
      return null;
   }
   
   /**
    * Gets the specification of the elements of the input data section.
    *
    * @return
    *   The specification of the input data section, never <code>null</code>.
    */
   public DataSectionElement[] getInputDataSection() {
      
      // TODO implement this function
      return null;
   }
   
   /**
    * Gets the specification of the elements of the output data section.
    *
    * @return
    *   The specification of the output data section, never <code>null</code>.
    */
   public DataSectionElement[] getOutputDataSection() {
      
      // TODO implement this function
      return null;
   }
   
   /**
    * Gets the input param combo specifications.
    *
    * @return
    *    The specification of the input param combos, never <code>null</code>.
    */
   public ParamCombo[] getInputParamCombos() {
      
      // TODO implement this function
      return null;
   }
   
   /**
    * Gets the output param combo specifications.
    *
    * @return
    *    The specification of the output param combos, never <code>null</code>.
    */
   public ParamCombo[] getOutputParamCombos() {
      
      // TODO implement this function
      return null;
   }
}
