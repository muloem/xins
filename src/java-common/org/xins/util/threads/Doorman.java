/*
 * $Id$
 */
package org.xins.util.threads;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Monitor that acts like a doorman. It implements a variation of the
 * <em>Alternating Reader Writer</em> algorithm.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.66
 */
public final class Doorman extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   private static final Queue.EntryType READ_QUEUE_ENTRY_TYPE = new Queue.EntryType();
   private static final Queue.EntryType WRITE_QUEUE_ENTRY_TYPE = new Queue.EntryType();

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Doorman</code> with the specified initial queue
    * size.
    *
    * @param queueSize
    *    the initial queue size, must be &gt;= 0.
    *
    * @throws IllegalArgumentException
    *    if <code>queueSize &lt; 0</code>.
    */
   public Doorman(int queueSize)
   throws IllegalArgumentException {

      // Check preconditions
      if (queueSize < 0) {
         throw new IllegalArgumentException("queueSize (" + queueSize + ") < 0");
      }

      // Initialize fields
      _currentActorLock = new Object();
      _currentReaders   = new HashSet();
      _queue            = new Queue(queueSize);
      _readAccess       = new Object();
      _writeAccess      = new Object();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Lock object for reading and writing the set of current readers and the
    * current writer.
    */
   private final Object _currentActorLock;

   /**
    * The set of currently active readers. All elements in the set are
    * {@link Thread} instances.
    */
   private final Set _currentReaders;

   /**
    * The currently active writer, if any.
    */
   private Thread _currentWriter;

   /**
    * The queue that contains the waiting readers and writers.
    */
   private final Queue _queue;

   /**
    * Object used for waiting for read access.
    */
   private final Object _readAccess;

   /**
    * Object used for waiting for write access.
    */
   private final Object _writeAccess;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Enters the 'protected area' as a reader. If necessary, this method will
    * wait until the area can be entered.
    *
    * @throws InterruptedException
    *    if a {@link Object#wait()} call was interrupted.
    */
   public void enterAsReader()
   throws InterruptedException {

      Thread reader = Thread.currentThread();

      boolean enterQueue;
      synchronized (_currentActorLock) {

         // Short-circuit if this thread is already entered
         if (_currentReaders.contains(reader)) {
            return;
         }

         // If there is an existing queue, or if a writer is busy, line up.
         enterQueue = !_queue.isEmpty() || _currentWriter != null;

         // If we don't have to join the queue, join the set of current
         // readers and go ahead
         if (!enterQueue) {
            _currentReaders.add(reader);
            return;

         // Otherwise we must join the queue
         } else {
            synchronized (_queue) {
               _queue.add(reader, READ_QUEUE_ENTRY_TYPE);
            }
         }
      }

      // Wait for read access
      boolean mayEnter;
      do {
         synchronized (_currentActorLock) {
            mayEnter = _currentReaders.contains(reader);
         }

         if (! mayEnter) {
            boolean exceptionThrown = true;
            try {
               _readAccess.wait();
               exceptionThrown = false;
            } finally {
               if (exceptionThrown) {
                  synchronized (_currentActorLock) {
                     _queue.remove(reader);
                  }
               }
            }
         }
      } while (! mayEnter);
   }

   /**
    * Notifies all appropriate waiting threads in the queue.
    */
   private void leave() {
      Queue.EntryType type;
      synchronized (_queue) {
         type = _queue.getTypeOfFirst();
      }

      if (type == READ_QUEUE_ENTRY_TYPE) {
         _readAccess.notifyAll();
      } else if (type == WRITE_QUEUE_ENTRY_TYPE) {
         _writeAccess.notifyAll();
      }
   }

   /**
    * Leaves the 'protected area' as a reader.
    */
   public void leaveAsReader() {
      Thread reader = Thread.currentThread();

      synchronized (_currentActorLock) {
         _currentReaders.remove(reader);
      }

      leave();
   }

   /**
    * Leaves the 'protected area' as a writer.
    */
   public void leaveAsWriter() {
      // XXX: Thread writer = Thread.currentThread();

      synchronized (_currentActorLock) {
         // TODO: What if _currentWriter != writer ?
         _currentWriter = null;
      }

      leave();
   }

   /**
    * Enters the 'protected area' as a writer. If necessary, this method will
    * wait until the area can be entered.
    *
    * @throws InterruptedException
    *    if a {@link Object#wait()} call was interrupted.
    */
   public void enterAsWriter()
   throws InterruptedException {
      Thread writer = Thread.currentThread();

      boolean enterQueue;
      synchronized (_currentActorLock) {

         // Short-circuit if this thread is already entered
         if (_currentWriter == writer) {
            return;
         }

         // If there is an existing queue, or if any thread is busy, line up.
         enterQueue = !_queue.isEmpty() || !_currentReaders.isEmpty() || _currentWriter != null;

         // If we don't have to join the queue, join the set of current
         // readers and go ahead
         if (!enterQueue) {
            _currentWriter = writer;
            return;

         // Otherwise we must join the queue
         } else {
            synchronized (_queue) {
               _queue.add(writer, WRITE_QUEUE_ENTRY_TYPE);
            }
         }
      }

      // Wait for write access
      boolean mayEnter;
      do {
         synchronized (_currentActorLock) {
            mayEnter = _currentWriter == writer;
         }

         if (! mayEnter) {
            boolean exceptionThrown = true;
            try {
               _writeAccess.wait();
               exceptionThrown = false;
            } finally {
               if (exceptionThrown) {
                  synchronized (_currentActorLock) {
                     _queue.remove(writer);
                  }
               }
            }
         }
      } while (! mayEnter);
   }


   //-------------------------------------------------------------------------
   // Inner class
   //-------------------------------------------------------------------------

   /**
    * Queue of waiting reader and writer threads.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    *
    * @since XINS 0.66
    */
   private static final class Queue
   extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new queue with the specified initial capacity.
       *
       * @param capacity
       *    the initial capacity, must be &gt;= 0.
       *
       * @throws IllegalArgumentException
       *    if <code>capacity &lt; 0</code>.
       */
      public Queue(int capacity) {
         if (capacity < 0) {
            throw new IllegalArgumentException("capacity (" + capacity + ") < 0");
         }
         _entries    = new LinkedList();
         _entryTypes = new HashMap(capacity);
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * The list of entries.
       */
      private final LinkedList _entries;

      /**
       * The entry types, by entry. This map has the {@link Thread threads} as
       * keys and their {@link EntryType types} as values.
       */
      private final Map _entryTypes;

      /**
       * Cached link to the first entry. This field is either
       * <code>null</code> or an instance of class {@link Thread}.
       */
      private Thread _first;

      /**
       * Cached type of the first entry. This field is either
       * <code>null</code> (if {@link #_entries} is empty), or
       * <code>(EntryType) </code>{@link #_entries}<code>.</code>{@link List#get() get}<code>(0)</code>
       * (if {@link #_entries} is not empty).
       */
      private EntryType _typeOfFirst;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      public boolean isEmpty() {
         return (_first == null);
      }

      public EntryType getTypeOfFirst() {
         return _typeOfFirst;
      }

      public void add(Thread thread, EntryType type) {
         if (_first == null) {
            _first       = thread;
            _typeOfFirst = type;
         }
         _entryTypes.put(thread, type);
         _entries.addLast(thread);
      }

      public void remove(Thread thread) {
         _entryTypes.remove(thread);
         if (thread == _first) {
            _entries.removeFirst();
            _first       = (Thread) _entries.getFirst();
            _typeOfFirst = _first == null ? null : (EntryType) _entryTypes.get(_first);
         } else {
            _entries.remove(thread);
         }
      }


      //----------------------------------------------------------------------
      // Inner classes
      //----------------------------------------------------------------------

      public static final class EntryType
      extends Object {

         //-------------------------------------------------------------------
         // Constructors
         //-------------------------------------------------------------------

         //-------------------------------------------------------------------
         // Fields
         //-------------------------------------------------------------------

         //-------------------------------------------------------------------
         // Methods
         //-------------------------------------------------------------------

      }
   }
}
