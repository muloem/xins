/*
 * $Id$
 */
package org.xins.util.collections.expiry;

import java.util.HashMap;
import java.util.Map;
import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.threads.Doorman;

/**
 * Expiry folder. Contains values indexed by key. Entries in this folder will
 * expire after a predefined amount of time, unless they're accessed within that
 * timeframe.
 *
 * <p>This class is thread-safe.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class ExpiryFolder
extends Object {

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
    * Constructs a new <code>ExpiryFolder</code>.
    *
    * @param strategy
    *    the strategy that should be applied, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>strategy == null</code>.
    */
   public ExpiryFolder(ExpiryStrategy strategy)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("strategy", strategy);

      // Initialize fields
      _strategy         = strategy;
      _recentlyAccessed = new HashMap(89);
      _slotCount        = strategy.getSlotCount();
      _slots            = new Map[_slotCount];
      _lastSlot         = _slotCount - 1;
      _sizeLock         = new Object();

      _recentlyAccessedDoorman = new Doorman(89);
      _slotsDoorman            = new Doorman(89);

      // Notify the strategy
      strategy.folderAdded(this);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

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
   private volatile Map _recentlyAccessed;

   /**
    * Number of active slots. Always equals
    * {@link #_slots}<code>.length</code>.
    */
   private final int _slotCount;

   /**
    * The index of the last slot. This is always
    * {@link #_slotCount}<code> - 1</code>.
    */
   private final int _lastSlot;

   /**
    * Slots to contain the maps with entries that are not the most recently
    * accessed. The further back in the array, the faster the entries will
    * expire.
    */
   private final Map[] _slots;

   /**
    * Doorman protecting the field <code>_recentlyAccessed</code>.
    */
   private final Doorman _recentlyAccessedDoorman;

   /**
    * Doorman protecting the field <code>_slots</code>.
    */
   private final Doorman _slotsDoorman;

   /**
    * The size of this folder.
    */
   private int _size;

   /**
    * Lock for the <code>_size</code>.
    */
   private final Object _sizeLock;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Notifies this map that the precision time frame has passed since the
    * last tick.
    *
    * <p>If any entries are expirable, they will be removed from this folder.
    */
   void tick() {

      // First enter the protected area for '_recentlyAccessed', because that
      // is the most difficult to enter
      _recentlyAccessedDoorman.enterAsWriter();

      // Then enter the protected area for '_slots' as well
      _slotsDoorman.enterAsWriter();

      // Keep a link to the old map with recently accessed elements and then
      // reset _recentlyAccessed so we can leave the protected area for
      // '_recentlyAccessed' right away
      Map oldRecentlyAccessed = _recentlyAccessed;
      _recentlyAccessed = new HashMap();

      // Leave the protected area for '_recentlyAccessed' first, because that
      // is the heaviest used
      _recentlyAccessedDoorman.leaveAsWriter();

      // Shift the slots
      Map toBeExpired = _slots[_lastSlot];
      for (int i = _lastSlot; i > 0; i--) {
         _slots[i] = _slots[i - 1];
      }
      _slots[0] = oldRecentlyAccessed;

      // Leave the protected area for '_slots' as well.
      _slotsDoorman.leaveAsWriter();

      // Adjust the size
      synchronized (_sizeLock) {
         _size -= toBeExpired.size();
      }

      // Invalidate the reference to the set of expired entries
      toBeExpired = null;
   }

   /**
    * Gets the number of entries.
    *
    * @return
    *    the number of entries in this expiry folder, always &gt;= 0.
    */
   public int size() {
      synchronized (_sizeLock) {
         return _size;
      }
   }

   /**
    * Gets the value associated with a key. If the key is found, then the
    * expiry time-out for the matching entry will be reset.
    *
    * <p>The more recently the specified entry accessed, the faster the
    * lookup.
    *
    * @param key
    *    the key to lookup, cannot be <code>null</code>.
    *
    * @return
    *    the value associated with the specified key, or <code>null</code> if
    *    and only if this folder does not contain an entry with the specified
    *    key.
    *
    * @throws IllegalArgumentException
    *    if <code>key == null</code>.
    */
   public Object get(Object key) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("key", key);

      // Search in the recently accessed map before
      _recentlyAccessedDoorman.enterAsReader();
      Object o = _recentlyAccessed.get(key);
      _recentlyAccessedDoorman.leaveAsReader();

      // If not found, then look in the slots
      if (o == null) {
         _slotsDoorman.enterAsReader();
         for (int i = 0; i < _slotCount && o == null; i++) {
            o = _slots[i].get(key);
         }
         _slotsDoorman.leaveAsReader();
      }

      return o;
   }

   /**
    * Associates the specified value with the specified key in this folder.
    *
    * @param key
    *    they key for the entry, cannot be <code>null</code>.
    *
    * @param value
    *    they value for the entry, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>key == null || value == null</code>.
    */
   public void put(Object key, Object value)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("key", key, "value", value);

      // Store the association in the set of recently accessed entries
      _recentlyAccessedDoorman.enterAsWriter();
      _recentlyAccessed.put(key, value);
      _recentlyAccessedDoorman.leaveAsWriter();

      // Bump the size
      synchronized (_sizeLock) {
         _size++;
      }
   }
}
