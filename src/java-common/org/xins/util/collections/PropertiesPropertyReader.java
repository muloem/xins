/*
 * $Id$
 */
package org.xins.util.collections;

import java.util.Iterator;
import java.util.Properties;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Property reader based on a <code>Properties</code> object.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class PropertiesPropertyReader
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
    * Constructs a new <code>PropertiesPropertyReader</code>.
    *
    * @param properties
    *    the {@link Properties} object to read from, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>properties == null</code>.
    */
   public PropertiesPropertyReader(Properties properties)
   throws IllegalArgumentException {
      MandatoryArgumentChecker.check("properties", properties);
      _properties = properties;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The <code>Properties</code> object to read from. This field is never
    * <code>null</code>.
    */
   private final Properties _properties;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public String get(String name) throws IllegalArgumentException {
      MandatoryArgumentChecker.check("name", name);
      Object o = _properties.get(name);
      return (o == null) ? null : (String) o;
   }

   public Iterator getNames() {
      return _properties.keySet().iterator();
   }
}
