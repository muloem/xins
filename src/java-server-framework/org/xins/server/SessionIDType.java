/*
 * $Id$
 */
package org.xins.server;

import org.xins.types.Type;

/**
 * Base type for session ID types.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class SessionIDType extends Type {

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
    * Constructs a new <code>SessionIDType</code> type for the specified API.
    *
    * @param name
    *    the name for this type, cannot be <code>null</code>.
    *
    * @param valueClass
    *    the value class, or <code>null</code> if {@link Object} should be
    *    used.
    *
    * @param api
    *    the API for which to create the type, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || api == null</code>.
    */
   protected SessionIDType(String name, Class valueClass, API api)
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
    * @since XINS 0.57
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
      public abstract Object generateSessionID();
   }
}
