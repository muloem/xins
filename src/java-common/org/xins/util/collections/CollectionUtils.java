/*
 * $Id$
 */
package org.xins.util.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility functions for collections.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class CollectionUtils extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * An unmodifiable empty map.
    */
   public final static Map EMPTY_MAP = Collections.unmodifiableMap(new HashMap());


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Private constructor, no instances of this class should ever be
    * constructed.
    */
   private CollectionUtils() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
