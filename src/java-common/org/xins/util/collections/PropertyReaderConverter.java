/*
 * $Id$
 */
package org.xins.util.collections;

import java.util.Iterator;
import java.util.Properties;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Converter that is able to convert <code>PropertyReader</code> objects to
 * other kinds of objects.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class PropertyReaderConverter extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Converts the specified <code>PropertyReader</code> object to a new
    * <code>Properties</code> object.
    *
    * @param propertyReader
    *    the {@link PropertyReader} object, cannot be <code>null</code>.
    *
    * @return
    *    a new {@link Properties} object, never <code>null</code>.
    */
   public static final Properties toProperties(PropertyReader propertyReader)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("propertyReader", propertyReader);

      Properties p = new Properties();
      Iterator keys = propertyReader.getNames();
      while (keys.hasNext()) {
         String key = (String) keys.next();
         String value = propertyReader.get(key);

         p.setProperty(key, value);
      }
      return p;
   }


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>PropertyReaderConverter</code>.
    */
   private PropertyReaderConverter() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
