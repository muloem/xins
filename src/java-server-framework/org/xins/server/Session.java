/*
 * $Id$
 */
package org.xins.server;

import java.util.HashMap;
import java.util.Map;
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

   /**
    * Attributes for this session. This map contains {@link String} keys and
    * {@link Object} values.
    *
    * <p>This field is lazily initialized, so it is initially
    * <code>null</code>.
    */
   private Map _attributes;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the identifier.
    *
    * @return
    *    the identifier, never <code>null</code>.
    */
   public String getID() {
      return _id;
   }

   /**
    * Sets or resets the specified attribute. If the specified value is
    * <code>null</code> then the attribute setting will be removed (if it
    * existed at all).
    *
    * @param key
    *    the attribute key, cannot be <code>null</code>.
    *
    * @param value
    *    the attribute value, or <code>null</code> if the attribute should be
    *    reset.
    *
    * @throws IllegalArgumentException
    *    if <code>key == null</code>.
    */
   public void setAttribute(String key, Object value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("key", key);

      // If necessary init the Map and then store the entry
      if (_attributes == null) {
         if (value != null) {
            _attributes = new HashMap(89);
            _attributes.put(key, value);
         }

      // If the value is null, then remove the entry
      } else if (value == null) {
         _attributes.remove(key);
         // XXX: Check if the map is now empty and set it to null?

      // Otherwise store a new entry
      } else {
         _attributes.put(key, value);
      }
   }

   /**
    * Gets the value of the attribute with the specified key.
    *
    * @param key
    *    the attribute key, cannot be <code>null</code>.
    *
    * @return
    *    the attribute value, or <code>null</code> if the attribute is not
    *    set.
    *
    * @throws IllegalArgumentException
    *    if <code>key == null</code>.
    */
   public Object getAttribute(String key)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("key", key);

      if (_attributes != null) {
         return _attributes.get(key);
      } else {
         return null;
      }
   }

   /**
    * Touches this session so the expiry timer will be reset.
    */
   public void touch() {
      // TODO
   }
}
