/*
 * $Id$
 */
package org.xins.common.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility functions for collections.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class CollectionUtils extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * An unmodifiable empty map.
    */
   public final static Map EMPTY_MAP = Collections.unmodifiableMap(new HashMap());

   /**
    * An unmodifiable empty list.
    */
   public final static List EMPTY_LIST = Collections.unmodifiableList(new ArrayList(0));


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
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
