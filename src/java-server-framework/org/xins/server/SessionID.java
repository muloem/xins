/*
 * $Id$
 */
package org.xins.server;

import org.xins.types.Type;
import org.xins.types.TypeValueException;

/**
 * Base type for session ID types.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class SessionID extends Type {

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
    * Constructs a new <code>SessionID</code> type for the specified API.
    *
    * @param api
    *    the API for which to create the type, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || api == null</code>.
    */
   protected SessionID(String name, Class valueClass, API api)
   throws IllegalArgumentException {
      super(name, valueClass);
      if (api == null) {
         throw new IllegalArgumentException("api == null");
      }
      _api = api;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API for which this type is generated.
    */
   private final API _api;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the generator for this session ID type.
    *
    * @return
    *    the session ID generator, never <code>null</code>.
    */
   public abstract Generator getGenerator();


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Generator of session ID strings.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.52
    */
   public abstract class Generator
   extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Generator</code>.
       */
      protected Generator() {
         // empty
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Generates a session ID.
       *
       * @return
       *    the generated session ID, not be <code>null</code>.
       */
      public abstract String generateSessionID();
   }
}
