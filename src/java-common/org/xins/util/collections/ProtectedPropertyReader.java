/*
 * $Id$
 */
package org.xins.util.collections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Modifiable implementation of a property reader, can be protected from
 * unauthorized changes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class ProtectedPropertyReader
extends Object
implements PropertyReader {

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
    * Constructs a new <code>ProtectedPropertyReader</code>.
    *
    * @param key
    *    the secret key that must be passed to {@link #set(Object,String,String)}
    *    in order to be authorized to modify this set of properties.
    *
    * @throws IllegalArgumentException
    *    if <code>key == null</code>.
    */
   public ProtectedPropertyReader(Object key)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("key", key);

      _key        = key;
      _properties = new HashMap(89);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The secret key.
    */
   private final Object _key;

   /**
    * The mappings from property keys to values.
    */
   private final Map _properties;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Sets or resets the specified property. If the specified value is
    * <code>null</code> then the property is reset to <code>null</code>,
    * otherwise the property with the specified name is set to the specified
    * value.
    *
    * <p>The key must be passed. If it is incorrect, then an
    * {@link IllegalArgumentException} is thrown. Note that an identity check
    * is done, <em>not</em> an equality check. So
    * {@link Object#equals(Object)} is not used, but the <code>==</code>
    * operator is.
    *
    * @param key
    *    the secret key, must be the same as the key specified with the
    *    constructor, cannot be <code>null</code>.
    *
    * @param name
    *    the name of the property to set or reset, cannot be
    *    <code>null</code>.
    *
    * @param value
    *    the value for the property, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public void set(Object key, String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
      if (key != _key) {
         throw new IllegalArgumentException("Invalid key.");
      }
      MandatoryArgumentChecker.check("name", name);

      // Store the value
      if (value != null) {
         _properties.put(name, value);
      } else {
         _properties.remove(name);
      }
   }

   public String get(String name) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("name", name);
      Object o = _properties.get(name);
      return (o == null) ? null : (String) o;
   }

   public Iterator getNames() {
      return _properties.keySet().iterator();
   }
}
