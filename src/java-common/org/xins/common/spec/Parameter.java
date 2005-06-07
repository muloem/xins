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
public abstract class Parameter {
   
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
    * Creates a new instance of Parameter
    */
   public Parameter() {
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Gets the name of the parameter.
    *
    * @return
    *    The name of the parameter, never <code>null</code>.
    */
   public abstract String getName();
   
   /**
    * Gets the description of the parameter.
    *
    * @return
    *    The description of the parameter, never <code>null</code>.
    */
   public abstract String getDescription();

   /**
    * Returns whether the parameter is mandatory.
    *
    * @return
    *    <code>true</code> if the parameter is requierd, <code>false</code> otherwise.
    */
   public abstract boolean isRequired();

   /**
    * Gets the type of the parameter.
    *
    * @return
    *    The type of the parameter, never <code>null</code>.
    */
   public abstract Type getType();
}
