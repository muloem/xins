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
public abstract class ParamCombo {
   
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
    */
   public ParamCombo() {
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Returns whether the param combo is a all-or-none type.
    *
    * @return
    *    <code>true</code> if the type is all-or-none, <code>false</code> otherwise.
    */
   public abstract boolean isAllOrNone();
   
   /**
    * Returns whether the param combo is a exclusive-or type.
    *
    * @return
    *    <code>true</code> if the type is exclusive-or, <code>false</code> otherwise.
    */
   public abstract boolean isExclusiveOr();
   
   /**
    * Returns whether the param combo is a inclusive-or type.
    *
    * @return
    *    <code>true</code> if the type is inclusive-or, <code>false</code> otherwise.
    */
   public abstract boolean isInclusiveOr();
   
   /**
    * Gets the parameters defined in the param combo.
    *
    * @return
    *    The specification of the parameters defined in the param combo, never <code>null</code>.
    */
   public abstract Parameter[] getParameters();
   
}
