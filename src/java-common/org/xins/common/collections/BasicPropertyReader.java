/*
 * $Id$
 */
package org.xins.common.collections;

import java.util.HashMap;
import org.xins.common.MandatoryArgumentChecker;

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
    * Sets the specified property.
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
      getPropertiesMap().put(name, value);
   }

   /**
    * Removes the specified property.
    *
    * @param name
    *    the name of the property to set or reset, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null</code>.
    */
   public void remove(String name) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);

      // Remove the property
      getPropertiesMap().remove(name);
   }

}
