/*
 * $Id$
 */
package org.xins.server;

import org.apache.log4j.Logger;

/**
 * Class that represents the XINS/Java Server Framework library.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.8
 */
public final class Library extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The logging category used by the XINS/Java Server Framework during
    * startup/initialization, re-initialization and shutdown. This field is
    * not <code>null</code>.
    */
   static final Logger LIFESPAN_LOG = Logger.getLogger("org.xins.server.LIFESPAN");

   /**
    * The logging category used by the XINS/Java Server Framework core during
    * runtime. This field is not <code>null</code>.
    */
   static final Logger RUNTIME_LOG = Logger.getLogger("org.xins.server.RUNTIME");


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns the version of this library.
    *
    * @return
    *    the version of this library, for example <code>"%%VERSION%%"</code>,
    *    never <code>null</code>.
    */
   public static final String getVersion() {
      return "%%VERSION%%";
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Library</code> object.
    */
   private Library() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
