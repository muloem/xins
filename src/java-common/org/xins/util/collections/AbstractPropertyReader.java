/*
 * $Id$
 */
package org.xins.util.collections;

import java.util.Iterator;
import java.util.Map;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Implementation of some methods for the PropertyReader.
 *
 * @version $Revision$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>), Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public abstract class AbstractPropertyReader
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
    * Constructs a new <code>AbstractPropertyReader</code>.
    *
    * @param map
    *    the map used to put the data of this PropertyReader, cannot be
    *    <code>null</code>.
    */
   public AbstractPropertyReader(Map map) {
      _properties = map;
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

   public String get(String name) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("name", name);
      Object value = _properties.get(name);
      return (String) value;
   }

   public Iterator getNames() {
      return _properties.keySet().iterator();
   }

   /**
    * Returns the Map that contains the properties.
    *
    * @return
    *    the map used to store the properties, cannot be
    *    <code>null</code>.
    */
   protected Map getPropertiesMap() {
      return _properties;
   }
}
