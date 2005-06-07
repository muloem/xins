/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

/**
 * Specification of the error code (also known as result code).
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public abstract class ErrorCode {
   
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
    * Creates a new instance of ErrorCode
    */
   public ErrorCode() {
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Gets the name of the error code.
    *
    * @return
    *    The name of the error code, never <code>null</code>.
    */
   public abstract String getName();
   
   /**
    * Gets the description of the error code.
    *
    * @return
    *    The description of the error code, never <code>null</code>.
    */
   public abstract String getDescription();

   /**
    * Gets the output parameter specifications defined in the error code.
    *
    * @return
    *    The output parameters, never <code>null</code>.
    */
   public abstract Parameter[] getOutputParameters();
   
   /**
    * Gets the specification of the elements of the output data section.
    *
    * @return
    *   The specification of the output data section, never <code>null</code>.
    */
   public abstract DataSectionElement[] getOutputDataSection();
   
}
