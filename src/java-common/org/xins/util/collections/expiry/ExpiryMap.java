/*
 * $Id$
 */
package org.xins.util.collections.expiry;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Expiry map. Items in this map will expire after a predefined amount of
 * time, unless they're access within that timeframe.
 *
 * <p>This method is thread-safe.
 *
 * <p>This implementation of the {@link Map} interface violates that
 * interface, because {@link #put(Object,Object)} will not return the
 * previous value associated with specified key. This is because getting the
 * previous value is a bigger effort than actually setting the new value. The
 * {@link #put(Object,Object)} will always return <code>null</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class ExpiryMap
extends AbstractMap {

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
   public ExpiryMap(ExpiryStrategy strategy)
   throws IllegalArgumentException {

      _strategy = strategy;

      // XXX: Allow customization of Map construction?
      _recentlyAccessed = new HashMap(89);

      _slots = new Map[strategy.getSlotCount()];
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Entry set. This field is lazily initialized.
    */
   private EntrySet _entrySet;

   /**
    * The strategy used. This field cannot be <code>null</code>.
    */
   private final ExpiryStrategy _strategy;

   /**
    * The most recently accessed entries. This field cannot be
    * <code>null</code>. The entries in this map will expire after
    * {@link ExpiryStrategy#getTimeOut()} milliseconds, plus at maximum
    * {@link ExpiryStrategy#getPrecision()} milliseconds.
    */
   private final Map _recentlyAccessed;

   /**
    * Slots to contain the maps with entries that are not the most recently
    * accessed. The further back in the array, the faster the entries will
    * expire.
    */
   private final Map[] _slots;


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

   public int size() {
      int size;
      synchronized (_recentlyAccessed) {
         synchronized (_slots) {
            size = _recentlyAccessed.size();
            for (int i = 0; i < _slots.length; i++) {
               size += _slots[i].size();
            }
         }
      }
      return size;
   }

   public boolean isEmpty() {

      // XXX: This method may return a value that is no longer valid when it
      //      returns the value, because of the multi-threaded nature

      synchronized (_recentlyAccessed) {
         if (_recentlyAccessed.isEmpty() == false) {
            return false;
         }
      }

      for (int i = 0; i < _slots.length; i++) {
         Map slot = _slots[i];
         synchronized (slot) {
            if (slot.isEmpty() == false) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean containsKey(Object key) {
      synchronized (_recentlyAccessed) {
         if (_recentlyAccessed.containsKey(key)) {
            return true;
         }
      }

      for (int i = 0; i < _slots.length; i++) {
         Map slot = _slots[i];
         synchronized (slot) {
            if (slot.containsKey(key)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean containsValue(Object value) {
      synchronized (_recentlyAccessed) {
         if (_recentlyAccessed.containsValue(value)) {
            return true;
         }
      }

      for (int i = 0; i < _slots.length; i++) {
         Map slot = _slots[i];
         synchronized (slot) {
            if (slot.containsValue(value)) {
               return true;
            }
         }
      }

      return false;
   }

   public Object get(Object key) {
      synchronized (_recentlyAccessed) {
         if (_recentlyAccessed.containsKey(key)) {
            return _recentlyAccessed.get(key);
         }
      }

      for (int i = 0; i < _slots.length; i++) {
         Map slot = _slots[i];
         synchronized (slot) {
            if (slot.containsKey(key)) {
               return slot.get(key);
            }
         }
      }

      return null;
   }

   public Object put(Object key, Object value) {
      synchronized (_recentlyAccessed) {
         _recentlyAccessed.put(key, value);
      }

      // XXX: Returning null violates the contract of the interface
      //      java.util.Map, but it has a large impact on performance
      return null;
   }

   public void putAll(Map t) {
      java.util.Iterator i = t.entrySet().iterator();
      while (i.hasNext()) {
         Entry e = (Entry) i.next();
         put(e.getKey(), e.getValue());
      }
   }

   public void clear() {
      _recentlyAccessed.clear();
      for (int i = 0; i < _slots.length; i++) {
         _slots[i].clear();
      }
   }

   public Set entrySet() {
      if (_entrySet == null) {
         _entrySet = new EntrySet();
      }
      return _entrySet;
   }


   //-------------------------------------------------------------------------
   // Inner class
   //-------------------------------------------------------------------------

   /**
    * Entry set of the expiry map.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    */
   private class EntrySet extends AbstractSet {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new entry set.
       */
      private EntrySet() {
         // empty
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      public int size() {
         return ExpiryMap.this.size();
      }

      public java.util.Iterator iterator() {
         return null;
         // TODO: return new Iterator();
      }


      //----------------------------------------------------------------------
      // Inner classes
      //----------------------------------------------------------------------

      /**
       * Iterator for the entry set of the expiry map.
       *
       * @version $Revision$ $Date$
       * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
      private class Iterator
      extends Object
      implements java.util.Iterator {

         //-------------------------------------------------------------------
         // Constructors
         //-------------------------------------------------------------------

         //-------------------------------------------------------------------
         // Fields
         //-------------------------------------------------------------------

         //-------------------------------------------------------------------
         // Methods
         //-------------------------------------------------------------------

         public boolean hasNext() {
            
         }
      }
       */
   }
}
