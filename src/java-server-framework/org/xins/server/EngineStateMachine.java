/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.common.MandatoryArgumentChecker;

/**
 * State machine for the XINS server engine.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
final class EngineStateMachine extends Object {

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
    * The <em>DETERMINE_INTERVAL</em> state.
    */
   static final EngineState DETERMINE_INTERVAL =
      new EngineState("DETERMINE_INTERVAL", false);

   /**
    * The <em>DETERMINE_INTERVAL_FAILED</em> state.
    */
   static final EngineState DETERMINE_INTERVAL_FAILED =
      new EngineState("DETERMINE_INTERVAL_FAILED", true);

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
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>EngineStateMachine</code> object. Initially the
    * state will be {@link #INITIAL}.
    */
   EngineStateMachine() {
      _stateLock = new Object();
      _state     = INITIAL;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Lock for the <code>_state</code> field. This object must be locked on
    * before _state may be read or changed.
    */
   private final Object _stateLock;

   /**
    * The current state.
    */
   private EngineState _state;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the current state.
    *
    * @return
    *    the current state, cannot be <code>null</code>.
    */
   EngineState getState() {
      synchronized (_stateLock) {
         return _state;
      }
   }

   /**
    * Changes the current state.
    *
    * <p>If the state change is considered invalid, then an
    * {@link IllegalStateException} is thrown.
    *
    * @param newState
    *    the new state, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>newState == null</code>.
    *
    * @throws IllegalStateException
    *    if the state change is considered invalid.
    */
   void setState(EngineState newState)
   throws IllegalArgumentException, IllegalStateException {

      // Check preconditions
      MandatoryArgumentChecker.check("newState", newState);

      synchronized (_stateLock) {

         // Remember the current state
         EngineState oldState = _state;

         // Determine name of current and new state
         String oldStateName = (oldState == null)
                             ? null
                             : oldState.getName();
         String newStateName = newState.getName();

         // Short-circuit if the current equals the new state
         if (oldState == newState) {
            return;

         // Always allow changing state to DISPOSING
         } else if (oldState != DISPOSING && newState == DISPOSING) {

         // The first state change should be to bootstrap the framework
         } else if (oldState == INITIAL
                 && newState == BOOTSTRAPPING_FRAMEWORK) {

         // Bootstrapping the framework may fail
         } else if (oldState == BOOTSTRAPPING_FRAMEWORK
                 && newState == FRAMEWORK_BOOTSTRAP_FAILED) {

         // Bootstrapping the framework can be retried
         } else if (oldState == FRAMEWORK_BOOTSTRAP_FAILED
                 && newState == BOOTSTRAPPING_FRAMEWORK) {

         // Bootstrapping the framework may succeed, in which case the API
         // will be constructed
         } else if (oldState == BOOTSTRAPPING_FRAMEWORK
                 && newState == CONSTRUCTING_API) {

         // Construction of API may fail
         } else if (oldState == CONSTRUCTING_API
                 && newState == API_CONSTRUCTION_FAILED) {

         // API construction can be retried
         } else if (oldState == API_CONSTRUCTION_FAILED
                 && newState == CONSTRUCTING_API) {

         // Construction of API may succeed, in which case the API is
         // bootstrapped
         } else if (oldState == CONSTRUCTING_API
                 && newState == BOOTSTRAPPING_API) {

         // Bootstrapping the API may fail
         } else if (oldState == BOOTSTRAPPING_API
                 && newState == API_BOOTSTRAP_FAILED) {

         // Bootstrapping the API can be retried
         } else if (oldState == API_BOOTSTRAP_FAILED
                 && newState == BOOTSTRAPPING_API) {

         // If bootstrapping the API succeeds, then the next step is to
         // determine the watch interval
         } else if (oldState == BOOTSTRAPPING_API
                 && newState == DETERMINE_INTERVAL) {

         // Determination of the watch interval may change
         } else if (oldState == DETERMINE_INTERVAL
                 && newState == DETERMINE_INTERVAL_FAILED) {

         // Determination of the watch interval may be retried
         } else if (oldState == DETERMINE_INTERVAL_FAILED
                 && newState == DETERMINE_INTERVAL) {

         // If determination of the watch interval succeeds, then the next
         // step is to initialize the API
         } else if (oldState == DETERMINE_INTERVAL
                 && newState == INITIALIZING_API) {

         // API initialization may fail
         } else if (oldState == INITIALIZING_API
                 && newState == API_INITIALIZATION_FAILED) {

         // API initialization may be retried, but then the interval is
         // determined first
         } else if (oldState == API_INITIALIZATION_FAILED
                 && newState == DETERMINE_INTERVAL) {

         // API initialization may succeed, in which case the engine is ready
         } else if (oldState == INITIALIZING_API
                 && newState == READY) {

         // While the servet is ready, the watch interval may be redetermined,
         // which is the first step in reinitialization
         } else if (oldState == READY
                 && newState == DETERMINE_INTERVAL) {

         // After disposal the state changes to the final disposed state
         } else if (oldState == DISPOSING
                 && newState == DISPOSED) {

         // Otherwise the state change is not allowed, fail!
         } else {

            // Log error
            Log.log_3101(oldStateName, newStateName);

            // Throw exception
            String error = "The state "
                         + oldStateName
                         + " cannot be followed by the state "
                         + newStateName
                         + '.';
            throw new IllegalStateException(error);
         }

         // Perform the state change
         _state = newState;
         Log.log_3100(oldStateName, newStateName);
      }
   }
}
