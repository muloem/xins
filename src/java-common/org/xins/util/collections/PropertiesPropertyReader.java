/*
 * $Id$
 */
package org.xins.util.collections;

import java.util.Properties;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Property reader based on a <code>Properties</code> object.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class PropertiesPropertyReader
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
      super(properties);
      MandatoryArgumentChecker.check("properties", properties);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

}
