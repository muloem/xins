/*
 * $Id$
 */
package org.xins.client;

import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.service.TargetDescriptor;

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
    * Creates a new <code>NonSharedSession</code> for the specified target
    * and session identifier.
    *
    * @param target
    *    the {@link TargetDescriptor}, cannot be <code>null</code>.
    *
    * @param sessionID
    *    the session identifier, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>functionCaller == null || sessionID == null</code>.
    */
   public NonSharedSession(TargetDescriptor target, String sessionID)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("target",    target,
                                     "sessionID", sessionID);

      // Store information
      _target    = target;
      _sessionID = sessionID;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The <code>TargetDescriptor</code> that identifies the target that
    * generated the session identifier. Never <code>null</code>.
    */
   private final TargetDescriptor _target;

   /**
    * The generated session identifier. Never <code>null</code>.
    */
   private final String _sessionID;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns a descriptor of the target that generated the session
    * identifier. Never <code>null</code>.
    *
    * @return
    *    the target that generated the session identifier, never
    *    <code>null</code>.
    */
   public final TargetDescriptor getTarget() {
      return _target;
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
