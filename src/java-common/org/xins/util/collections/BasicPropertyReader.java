/*
 * $Id$
 */
package org.xins.util.collections;

import java.util.HashMap;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Modifiable implementation of a property reader.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class BasicPropertyReader
extends AbstractPropertyReader {

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
      super(new HashMap(89));
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

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
        getPropertiesMap().put(name, value);
      } else {
        getPropertiesMap().remove(name);
      }
   }

}
