/*
 * $Id$
 */
package org.xins.server;

import org.xins.util.MandatoryArgumentChecker;
import org.xins.types.Type;

/**
 * Session.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.52
 */
public final class Session
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
    * Constructs a new <code>Session</code> with the specified ID.
    *
    * @param id
    *    the identifier for this session, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>id == null</code>.
    */
   public Session(String id) {
      MandatoryArgumentChecker.check("id", id);
      _id  = id;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The identifier for this session.
    */
   private final String _id;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the identifier.
    *
    * @return
    *    the identifier, never <code>null</code>.
    */
   public final String getID() {
      return _id;
   }
}
