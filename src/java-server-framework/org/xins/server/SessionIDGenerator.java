/*
 * $Id$
 */
package org.xins.server;

import org.xins.util.MandatoryArgumentChecker;
import org.xins.types.Type;

/**
 * Generator of session ID strings.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.52
 */
public abstract class SessionIDGenerator
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
    * Constructs a new <code>SessionIDGenerator</code>.
    */
   protected SessionIDGenerator() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Generates a session ID.
    *
    * @return
    *    the generated session ID, not be <code>null</code>.
    */
   public abstract String generateSessionID();
}
