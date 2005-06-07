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
public abstract class Function {
   
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
   public abstract String getName();
   
   /**
    * Gets the description of the function.
    *
    * @return
    *    The description of the function, never <code>null</code>.
    */
   public abstract String getDescription();

   /**
    * Gets the input parameter specifications defined in the function.
    *
    * @return
    *    The input parameters, never <code>null</code>.
    */
   public abstract Parameter[] getInputParameters();
   
   /**
    * Gets the output parameter specifications defined in the function.
    *
    * @return
    *    The output parameters, never <code>null</code>.
    */
   public abstract Parameter[] getOutputParameters();
   
   /**
    * Gets the error code specifications defined in the function.
    * The standard error code are not included.
    *
    * @return
    *    The error code specifications, never <code>null</code>.
    */
   public abstract ErrorCode[] getErrorCodes();
   
   /**
    * Gets the specification of the elements of the input data section.
    *
    * @return
    *   The specification of the input data section, never <code>null</code>.
    */
   public abstract DataSectionElement[] getInputDataSection();
   
   /**
    * Gets the specification of the elements of the output data section.
    *
    * @return
    *   The specification of the output data section, never <code>null</code>.
    */
   public abstract DataSectionElement[] getOutputDataSection();
   
   /**
    * Gets the input param combo specifications.
    *
    * @return
    *    The specification of the input param combos, never <code>null</code>.
    */
   public abstract ParamCombo[] getInputParamCombos();
   
   /**
    * Gets the output param combo specifications.
    *
    * @return
    *    The specification of the output param combos, never <code>null</code>.
    */
   public abstract ParamCombo[] getOutputParamCombos();
}
