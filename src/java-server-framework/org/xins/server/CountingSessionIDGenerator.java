/*
 * $Id$
 */
package org.xins.server;

/**
 * Counter-based generator of session ID strings.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.52
 */
public final class CountingSessionIDGenerator
extends SessionIDGenerator {

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
    * Constructs a new <code>CountingSessionIDGenerator</code> for the
    * specified API.
    *
    * @param api
    *    the api for which to create this session ID generator, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null</code>.
    */
   public CountingSessionIDGenerator(API api) {
      super(api);

      _lock = new Object();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The ID for the next session ID to be generated.
    */
   private long _nextID;

   /**
    * Object that will be locked on before the value of <code>_nextID</code>
    * is get and set.
    */
   private Object _lock;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected String generateSessionIDImpl() {
      long id;
      synchronized (_lock) {
         id = _nextID++;
      }
      return String.valueOf(id);
   }
}
