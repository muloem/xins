/*
 * $Id$
 */
package org.xins.util.collections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Modifiable implementation of a property reader.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class BasicPropertyReader
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
    * Constructs a new <code>BasicPropertyReader</code>.
    */
   public BasicPropertyReader() {
      _properties = new HashMap(89);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

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
   public void set(String name, String value)
   throws IllegalArgumentException {

      // Check preconditions
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
