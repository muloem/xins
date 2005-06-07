/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.spec;

/**
 * Specification of an API.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public abstract class API {
   
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
    * Creates a new instance of API
    */
   public API() {
   }
   
   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
   
   /**
    * Gets the name of the API.
    *
    * @return
    *    The name of the API, never <code>null</code>.
    */
   public abstract String getName();
   
   /**
    * Gets the owner of the API.
    *
    * @return
    *    The owner of the API or <code>null</code> if no owner is defined.
    */
   public abstract String getOwner();
   
   /**
    * Gets the description of the API.
    *
    * @return
    *    The description of the API, never <code>null</code>.
    */
   public abstract String getDescription();

   /**
    * Gets the function specifications defined in the API.
    *
    * @return
    *    The function specifications, never <code>null</code>.
    */
   public abstract Function[] getFunctions();
   
   /**
    * Get the specification of the given function.
    *
    * @param functionName
    *    The name of the function, can not be <code>null</code>
    *
    * @return
    *    The function specification.
    *
    * @throws IllegalArgumentException
    *    If the API does not define any function for the given name.
    */
   public abstract Function getFunction(String functionName);
}
