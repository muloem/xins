/*
 * $Id$
 */
package org.xins.util.collections.expiry;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Expiry map. Items in this map will expire after a predefined amount of
 * time, unless they're access within that timeframe.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class ExpiryMap extends AbstractMap {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ExpiryMap</code>.
    *
    * @param strategy
    *    the strategy that should be applied, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>strategy == null</code>.
    */
   public ExpiryMap(ExpiryStrategy strategy) throws IllegalArgumentException {
      _strategy = strategy;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The strategy used. This field cannot be <code>null</code>.
    */
   private final ExpiryStrategy _strategy;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Touches the entry that is identified by the specified key.
    *
    * @param key
    *    the key that identifies the entry, can be <code>null</code>.
    *
    * @throws NoSuchEntryException
    *    if there was no entry with the specified key in this map; the entry
    *    may have expired.
    */
   public abstract void touch(Object key) throws NoSuchEntryException;

   /**
    * Notifies this map that the precision time frame has passed since the
    * last tick.
    *
    * <p>If any entries are expirable, they will be removed from this map.
    */
   public abstract void tick();
}
