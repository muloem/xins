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
    * Constructs a new <code>SessionIDGenerator</code> for the specified API.
    *
    * @param api
    *    the api for which to create this session ID generator, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    */
   protected SessionIDGenerator(API api) {
      MandatoryArgumentChecker.check("api", api);
      _api  = api;
      _type = api.getSessionIDType();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API for which this session ID generator generates session IDs.
    */
   private final API _api;

   /**
    * The session ID type.
    */
   private final Type _type;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Generates a session ID (wrapper method).
    *
    * @return
    *    the generated session ID, cannot be <code>null</code>.
    *
    * @throws InternalError
    *    if {@link #generateSessionIDImpl()} returned <code>null</code> or if
    *    it returns a session ID that is not a valid value for the API session
    *    ID type.
    */
   public final String generateSessionID() throws InternalError {
      String id = generateSessionIDImpl();
      if (id == null) {
         throw new InternalError(getClass().getName() + ".generateSessionIDImpl() returned null.");
      }

      if (_type.isValidValue(id) == false) {
         throw new InternalError(getClass().getName() + ".generateSessionIDImpl() returned a value (\"" + id + "\") that is invalid for the session ID type for the API.");
      }

      return id;
   }

   /**
    * Generates a session ID (actual implementation).
    *
    * @return
    *    the generated session ID, should not be <code>null</code> and should
    *    conform to the API session ID type.
    */
   protected abstract String generateSessionIDImpl();
}
