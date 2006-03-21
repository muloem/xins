/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
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

   /**
    * The <em>INITIAL</em> state.
    */
   static final EngineState INITIAL = new EngineState("INITIAL", false);

   /**
    * The <em>BOOTSTRAPPING_FRAMEWORK</em> state.
    */
   static final EngineState BOOTSTRAPPING_FRAMEWORK =
      new EngineState("BOOTSTRAPPING_FRAMEWORK", false);

   /**
    * The <em>FRAMEWORK_BOOTSTRAP_FAILED</em> state.
    */
   static final EngineState FRAMEWORK_BOOTSTRAP_FAILED =
      new EngineState("FRAMEWORK_BOOTSTRAP_FAILED", true);

   /**
    * The <em>CONSTRUCTING_API</em> state.
    */
   static final EngineState CONSTRUCTING_API =
      new EngineState("CONSTRUCTING_API", false);

   /**
    * The <em>API_CONSTRUCTION_FAILED</em> state.
    */
   static final EngineState API_CONSTRUCTION_FAILED =
      new EngineState("API_CONSTRUCTION_FAILED", true);

   /**
    * The <em>BOOTSTRAPPING_API</em> state.
    */
   static final EngineState BOOTSTRAPPING_API =
      new EngineState("BOOTSTRAPPING_API", false);

   /**
    * The <em>API_BOOTSTRAP_FAILED</em> state.
    */
   static final EngineState API_BOOTSTRAP_FAILED =
      new EngineState("API_BOOTSTRAP_FAILED", true);

   /**
    * The <em>INITIALIZING_API</em> state.
    */
   static final EngineState INITIALIZING_API =
      new EngineState("INITIALIZING_API", false);

   /**
    * The <em>API_INITIALIZATION_FAILED</em> state.
    */
   static final EngineState API_INITIALIZATION_FAILED =
      new EngineState("API_INITIALIZATION_FAILED", true);

   /**
    * The <em>READY</em> state.
    */
   static final EngineState READY =
      new EngineState("READY", false);

   /**
    * The <em>DISPOSING</em> state.
    */
   static final EngineState DISPOSING =
      new EngineState("DISPOSING", false);

   /**
    * The <em>DISPOSED</em> state.
    */
   static final EngineState DISPOSED
      = new EngineState("DISPOSED", false);


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

      // Initialize fields
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
