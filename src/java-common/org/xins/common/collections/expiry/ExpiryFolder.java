/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.collections.expiry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;

import org.xins.common.text.TextUtils;

/**
 * Expiry folder. Contains values indexed by key. Entries in this folder will
 * expire after a predefined amount of time, unless their lifetime is extended
 * within that timeframe. This is done using the {@link #get(Object)} method.
 *
 * <p>Listeners are supported. Listeners are added using the
 * {@link #addListener(ExpiryListener)} method and removed using the
 * {@link #removeListener(ExpiryListener)} method. If a listener is registered
 * multiple times, it will receive the events multiple times as well. And it
 * will have to be removed multiple times as well.
 *
 * <p>This class is thread-safe.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class ExpiryFolder
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The name of this class.
    */
   private static final String CLASSNAME = ExpiryFolder.class.getName();

   /**
    * The initial size for the queue of threads waiting to obtain read or
    * write access to a resource.
    */
   private static final int INITIAL_QUEUE_SIZE = 89;

   /**
    * The number of instances of this class.
    */
   private static int INSTANCE_COUNT;

   /**
    * Lock object for <code>INSTANCE_COUNT</code>.
    */
   private static final Object INSTANCE_COUNT_LOCK = new Object();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ExpiryFolder</code>.
    *
    * @param name
    *    description of this folder, to be used in log and exception messages,
    *    not <code>null</code>.
    *
    * @param strategy
    *    the strategy that should be applied, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || strategy == null</code>.
    *
    * @since XINS 1.1.0
    */
   public ExpiryFolder(String         name,
                       ExpiryStrategy strategy)
   throws IllegalArgumentException {

      // Determine instance number
      synchronized (INSTANCE_COUNT_LOCK) {
         _instanceNum = INSTANCE_COUNT++;
      }

      final String CONSTRUCTOR_DETAIL = "#"
                                      + _instanceNum
                                      + " [name="
                                      + TextUtils.quote(name)
                                      + "; strategy="
                                      + TextUtils.quote(name)
                                      + ']';

      Log.log_1000(CLASSNAME, CONSTRUCTOR_DETAIL);

      // Check preconditions
      MandatoryArgumentChecker.check("name", name, "strategy", strategy);

      // Initialize fields
      _name                 = name;
      _strategy             = strategy;
      _asString             = CLASSNAME + ' ' + CONSTRUCTOR_DETAIL;
      _recentlyAccessed     = new HashMap(89);
      _recentlyAccessedLock = new Object();
      _slotCount            = strategy.getSlotCount();
      _slots                = new HashMap[_slotCount];
      _lastSlot             = _slotCount - 1;
      _sizeLock             = new Object();
      _listeners            = new ArrayList(5);

      // Initialize all the fields in _slots
      for (int i = 0; i < _slotCount; i++) {
         _slots[i] = new HashMap(89);
      }

      // Notify the strategy that we listen to it
      strategy.folderAdded(this);

      Log.log_1002(CLASSNAME, CONSTRUCTOR_DETAIL);
   }

   /**
    * Constructs a new <code>ExpiryFolder</code>.
    *
    * @param name
    *    description of this folder, to be used in log and exception messages,
    *    not <code>null</code>.
    *
    * @param strategy
    *    the strategy that should be applied, not <code>null</code>.
    *
    * @param strictChecking
    *    flag that indicates if checking of thread synchronization operations
    *    should be strict or loose.
    *
    * @param maxQueueWaitTime
    *    the maximum time in milliseconds a thread can wait in the queue for
    *    obtaining read or write access to a resource, must be &gt; 0L.
    *
    * @throws IllegalArgumentException
    *    if <code>name             ==    null
    *          || strategy         ==    null
    *          || maxQueueWaitTime &lt;= 0L</code>.
    *
    * @deprecated
    *    Deprecated since XINS 1.1.0.
    *    Use the constructor {@link #ExpiryFolder(String,ExpiryStrategy)}
    *    instead.
    */
   public ExpiryFolder(String         name,
                       ExpiryStrategy strategy,
                       boolean        strictChecking,
                       long           maxQueueWaitTime)
   throws IllegalArgumentException {
      this(name, strategy);

      // Check the extra documented precondition
      if (maxQueueWaitTime <= 0L) {
         final String DETAIL = "maxQueueWaitTime ("
                             + maxQueueWaitTime
                             + "L) <= 0L";
         throw new IllegalArgumentException(DETAIL);
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The instance number of this instance.
    */
   private final int _instanceNum;

   /**
    * The name of this expiry folder.
    */
   private final String _name;

   /**
    * The strategy used. This field cannot be <code>null</code>.
    */
   private final ExpiryStrategy _strategy;

   /**
    * String representation. Cannot be <code>null</code>.
    */
   private final String _asString;

   /**
    * The most recently accessed entries. This field cannot be
    * <code>null</code>. The entries in this map will expire after
    * {@link ExpiryStrategy#getTimeOut()} milliseconds, plus at maximum
    * {@link ExpiryStrategy#getPrecision()} milliseconds.
    */
   private volatile HashMap _recentlyAccessed;

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
   private final HashMap[] _slots;

   /**
    * Lock for accessing the <code>_recentlyAccessed</code> field.
    */
   private final Object _recentlyAccessedLock;

   /**
    * The size of this folder. If code needs to write to this field, then it
    * should lock on {@link #_sizeLock}.
    */
   private int _size;

   /**
    * Lock for writing to the <code>_size</code> field.
    */
   private final Object _sizeLock;

   /**
    * The set of listeners. May be empty, but never is <code>null</code>.
    */
   private final ArrayList _listeners;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the name given to this expiry folder.
    *
    * @return
    *    the name assigned to this expiry folder, not <code>null</code>.
    */
   public final String getName() {
      return _name;
   }

   /**
    * Notifies this map that the precision time frame has passed since the
    * last tick.
    *
    * <p>If any entries are expirable, they will be removed from this folder.
    */
   void tick() {

      // Allocate memory for the new map of recently accessed entries outside
      // the synchronized sections
      HashMap newRecentlyAccessed = new HashMap();

      // Store the entries that need to be expired in this map
      HashMap toBeExpired;

      // Always get the lock for _recentlyAccessed first
      synchronized (_recentlyAccessedLock) {

         // Then get the lock for _slots
         synchronized (_slots) {

            // Keep a link to the old map with recently accessed elements and
            // then reset _recentlyAccessed
            HashMap oldRecentlyAccessed = _recentlyAccessed;
            _recentlyAccessed       = newRecentlyAccessed;

            // Shift the slots
            toBeExpired = _slots[_lastSlot];
            for (int i = _lastSlot; i > 0; i--) {
               _slots[i] = _slots[i - 1];
            }
            _slots[0] = oldRecentlyAccessed;
         }
      }

      // Adjust the size
      int toBeExpiredSize = (toBeExpired == null)
                          ? 0
                          : toBeExpired.size();
      if (toBeExpiredSize > 0) {
         int newSize;
         synchronized (_sizeLock) {
            _size -= toBeExpiredSize;
            newSize = _size;
            if (_size < 0) {
               _size = 0;
            }
         }

         // If the new size was negative, it has been fixed already, but
         // report it now, outside the synchronized section
         if (newSize < 0) {
            Log.log_1050(CLASSNAME, "tick()", CLASSNAME, "tick()", "Size of expiry folder \"" + _name + "\" dropped to " + newSize + ", adjusted it to 0.");
         }
         Log.log_1400(_asString, toBeExpiredSize, newSize);
      } else {
         Log.log_1400(_asString, 0, _size);
      }

      // XXX: Should we do this in a separate thread, so all locks held by the
      //      ExpiryStrategy are released?

      // Get a copy of the list of listeners
      List listeners;
      synchronized (_listeners) {
         listeners = new ArrayList(_listeners);
      }

      // Notify all listeners
      int count = listeners.size();
      if (count > 0) {
         Map unmodifiableExpired = Collections.unmodifiableMap(toBeExpired);
         for (int i = 0; i < count; i++) {
            ExpiryListener listener = (ExpiryListener) listeners.get(i);
            listener.expired(this, unmodifiableExpired);
         }
      }
   }

   /**
    * Adds the specified object as a listener for expiry events.
    *
    * @param listener
    *    the listener to be registered, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>listener == null</code>.
    */
   public void addListener(ExpiryListener listener)
   throws IllegalArgumentException {
      MandatoryArgumentChecker.check("listener", listener);

      synchronized (_listeners) {
         _listeners.add(listener);
      }
   }

   /**
    * Removes the specified object as a listener for expiry events.
    *
    * @param listener
    *    the listener to be unregistered, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>listener == null</code>.
    */
   public void removeListener(ExpiryListener listener)
   throws IllegalArgumentException {
      MandatoryArgumentChecker.check("listener", listener);

      synchronized (_listeners) {
         _listeners.remove(listener);
      }
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
    * Gets the value associated with a key and extends the lifetime of the
    * matching entry, if there was a match.
    *
    * <p>The more recently the specified entry was accessed, the faster the
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

      Object value;

      // Search in the recently accessed map first
      synchronized (_recentlyAccessedLock) {
         value = _recentlyAccessed.get(key);

         // If not found, then look in the slots
         if (value == null) {
            synchronized (_slots) {
               for (int i = 0; i < _slotCount && value == null; i++) {
                  value = _slots[i].remove(key);
               }
            }

            if (value != null) {
               _recentlyAccessed.put(key, value);
            }
         }
      }

      return value;
   }

   /**
    * Finds the value associated with a key. The lifetime of the matching
    * entry is not extended.
    *
    * <p>The more recently the specified entry was accessed, the faster the
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
   public Object find(Object key) throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("key", key);

      Object value;

      // Search in the recently accessed map first
      synchronized (_recentlyAccessedLock) {
         value = _recentlyAccessed.get(key);
      }

      // If not found, then look in the slots
      if (value == null) {
         synchronized (_slots) {
            for (int i = 0; i < _slotCount && value == null; i++) {
               value = _slots[i].get(key);
            }
         }
      }

      return value;
   }

   /**
    * Associates the specified key with the specified value.
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
      synchronized (_recentlyAccessedLock) {
         _recentlyAccessed.put(key, value);
      }

      // Bump the size
      synchronized (_sizeLock) {
         _size++;
      }
   }

   /**
    * Removes the specified key from this folder.
    *
    * @param key
    *    the key for the entry, cannot be <code>null</code>.
    *
    * @return
    *    the old value associated with the specified key, or <code>null</code>
    *    if and only if this folder does not contain an entry with the
    *    specified key.
    *
    * @throws IllegalArgumentException
    *    if <code>key == null</code>.
    */
   public Object remove(Object key)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("key", key);

      Object value;

      // Remove the key in the set of recently accessed entries
      synchronized (_recentlyAccessed) {
         value = _recentlyAccessed.remove(key);
      }

      // If not found, then look in the slots
      if (value == null) {
         synchronized (_slots) {
            for (int i = 0; i < _slotCount && value == null; i++) {
               value = _slots[i].remove(key);
            }
         }
      }

      // Decrease the size, if appropriate
      if (value != null) {
         synchronized (_sizeLock) {
            _size--;
         }
      }

      return value;
   }

   /**
    * Copies the entries of this ExpiryFolder into another one.
    * This method does not perform a deep copy, so if a key is added or
    * removed, both folders will be modified.
    *
    * @param newFolder
    *    the new folder where the entries should be copied into,
    *    cannot be <code>null</code>, cannot be <code>this</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>newFolder == null</code> or <code>newFolder == this</code>
    *    or the precision is the newFolder is not the same as for this folder.
    */
   public void copy(ExpiryFolder newFolder)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("newFolder", newFolder);
      if (newFolder == this) {
         // TODO: Log programming error
         throw new IllegalArgumentException("The folder can not be copied into itself.");
      }
      if (newFolder.getStrategy().getPrecision() != getStrategy().getPrecision()) {
         // TODO: Log programming error
         throw new IllegalArgumentException("The folders must have the same precision.");
      }

      synchronized (_recentlyAccessedLock) {
         synchronized (newFolder._recentlyAccessedLock) {
            synchronized (_slots) {
               synchronized (newFolder._slots) {

                  // Copy the recentlyAccessed
                  newFolder._recentlyAccessed = new HashMap(_recentlyAccessed);

                  // Copy the slots
                  for (int i = 0; i < _slotCount && i < newFolder._slotCount; i++) {
                     newFolder._slots[i] = new HashMap(_slots[i]);
                  }

                  // Copy the size
                  synchronized (newFolder._sizeLock) {
                     newFolder._size = _size;
                  }
               }
            }
         }
      }
   }

   /**
    * Returns the strategy associated with this folder
    *
    * @return
    *    the strategy, never <code>null</code>.
    */
   public ExpiryStrategy getStrategy() {
      return _strategy;
   }

   /**
    * Returns a textual representation of this object.
    *
    * @return
    *    a textual representation of this <code>ExpiryFolder</code>, which
    *    includes the name.
    */
   public String toString() {
      return _asString;
   }
}
