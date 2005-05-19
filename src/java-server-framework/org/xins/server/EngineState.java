/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;

/**
 * State of an <code>Engine</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
final class EngineState extends Object {

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
    * Constructs a new <code>EngineState</code> object.
    *
    * @param name
    *    the name of this state, cannot be <code>null</code>.
    *
    * @param error
    *    flag that indicates whether this is an error state, <code>true</code>
    *    if it is.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   EngineState(String name, boolean error)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      _name  = name;
      _error = error;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The name of this state. Cannot be <code>null</code>.
    */
   private final String _name;

   /**
    * Flag that indicates whether this is an error state. Value is
    * <code>true</code> if it is.
    */
   private final boolean _error;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the name of this state.
    *
    * @return
    *    the name of this state, cannot be <code>null</code>.
    */
   public String getName() {
      return _name;
   }

   /**
    * Checks if this state is an error state.
    *
    * @return
    *    <code>true</code> if this is an error state, <code>false</code>
    *    otherwise.
    */
   public boolean isError() {
      return _error;
   }

   /**
    * Returns a textual representation of this object.
    *
    * @return
    *    the name of this state, never <code>null</code>.
    */
   public String toString() {
      return _name;
   }
}
