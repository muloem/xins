/*
 * $Id$
 */
package org.xins.client;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Non-shared session. Consists of a session identifier and a reference to the
 * function caller that represents the API that generated that session
 * identifier.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.72
 */
public final class NonSharedSession
extends Object {

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
    * Creates a new <code>NonSharedSession</code> for the specified function
    * caller and session identifier.
    *
    * @param functionCaller
    *    the function caller, cannot be <code>null</code>.
    *
    * @param sessionID
    *    the session identifier, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionCaller == null || sessionID == null</code>.
    */
   public NonSharedSession(FunctionCaller functionCaller, String sessionID)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("functionCaller", functionCaller,
                                     "sessionID",      sessionID);

      _functionCaller = functionCaller;
      _sessionID      = sessionID;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The function caller that generated the session identifier. Never
    * <code>null</code>.
    */
   private final FunctionCaller _functionCaller;

   /**
    * The generated session identifier. Never <code>null</code>.
    */
   private final String _sessionID;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the function caller that generated the session identifier.
    *
    * @return
    *    the function caller that generated the session identifier, never
    *    <code>null</code>.
    */
   public final FunctionCaller getFunctionCaller() {
      return _functionCaller;
   }

   /**
    * Returns the generated session identifier.
    *
    * @return
    *    the generated session identifier, never <code>null</code>.
    */
   public final String getSessionID() {
      return _sessionID;
   }
}
