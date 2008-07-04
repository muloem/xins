/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections;

import java.util.Iterator;
import java.util.Properties;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Converter that is able to convert <code>PropertyReader</code> objects to
 * other kinds of objects.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.1
 */
public final class PropertyReaderConverter {

   /**
    * Constructs a new <code>PropertyReaderConverter</code>.
    */
   private PropertyReaderConverter() {
      // empty
   }

   /**
    * Converts the specified <code>PropertyReader</code> object to a new
    * <code>Properties</code> object.
    *
    * @param propertyReader
    *    the {@link PropertyReader} object, cannot be <code>null</code>.
    *
    * @return
    *    a new {@link Properties} object, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyReader == null</code>.
    */
   public static Properties toProperties(PropertyReader propertyReader)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("propertyReader", propertyReader);

      Properties prop = new Properties();
      Iterator keys = propertyReader.getNames();
      while (keys.hasNext()) {
         String key = (String) keys.next();
         String value = propertyReader.get(key);

         prop.setProperty(key, value);
      }
      return prop;
   }
}
