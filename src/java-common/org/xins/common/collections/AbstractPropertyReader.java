/*
 * $Id$
 */
package org.xins.common.collections;

import java.util.Iterator;
import java.util.Map;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.WhislEncoding;

import org.xins.logdoc.LogdocStringBuffer;

/**
 * Implementation of some methods for the PropertyReader.
 *
 * @version $Revision$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>), Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
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
    *    the map containing the data of this <code>PropertyReader</code>,
    *    cannot be <code>null</code>.
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
    * Returns the <code>Map</code> that contains the properties.
    *
    * @return
    *    the {@link Map} used to store the properties in, cannot be
    *    <code>null</code>.
    */
   protected Map getPropertiesMap() {
      return _properties;
   }

   public final void serialize(LogdocStringBuffer buffer)
   throws NullPointerException {
      serialize(this, buffer);
   }
}
