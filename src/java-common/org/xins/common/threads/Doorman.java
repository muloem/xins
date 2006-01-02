/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.threads;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.ProgrammingException;
import org.xins.common.Utils;

import org.xins.logdoc.ExceptionUtils;

/**
 * Monitor that acts like a doorman. It implements a variation of the
 * <em>Alternating Reader Writer</em> algorithm.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 *
 * @deprecated
 *    Deprecated since XINS 1.0.1.
 *    Use <code>synchronized</code> sections instead.
 *    The implementation of this class is flawed.
 */
public final class Doorman extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The fully-qualified name of the <code>Doorman</code> class.
    */
   private static final String DOORMAN_CLASSNAME = Doorman.class.getName();

   /**
    * The fully-qualified name of the <code>Doorman.Queue</code> class.
    */
   private static final String QUEUE_CLASSNAME = Doorman.Queue.class.getName();

   /**
    * The type for readers in the queue.
    */
   private static final QueueEntryType READ_QUEUE_ENTRY_TYPE = new QueueEntryType("reader");

   /**
    * The type for writers in the queue.
    */
   private static final QueueEntryType WRITE_QUEUE_ENTRY_TYPE = new QueueEntryType("writer");


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
    * @param name
    *    the name for the protected area this doorman guards, to be used
    *    during logging and when creating exceptions, cannot be
    *    <code>null</code>.
    *
    * @param strict
    *    flag that indicates if strict thread synchronization checking should
    *    be performed.
    *
    * @param queueSize
    *    the initial queue size, must be &gt;= 0.
    *
    * @param maxQueueWaitTime
    *    the maximum number of milliseconds an entry should be allowed to wait
    *    in the queue, must be &gt;= 0.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null
    *          || queueSize &lt; 0
    *          || maxQueueWaitTime &lt; 0L</code>.
    */
   public Doorman(String  name,
                  boolean strict,
                  int     queueSize,
                  long    maxQueueWaitTime)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("name", name);
      if (queueSize < 0 || maxQueueWaitTime <= 0L) {
         String detail;
         if (queueSize < 0 && maxQueueWaitTime <= 0L) {
            detail = "queueSize ("
                   + queueSize
                   + ") < 0 && maxQueueWaitTime ("
                   + maxQueueWaitTime
                   + ") <= 0L";
         } else if (queueSize < 0) {
            detail = "queueSize (" + queueSize + ") < 0";
         } else {
            detail = "maxQueueWaitTime (" + maxQueueWaitTime + ") <= 0L";
         }
         throw new IllegalArgumentException(detail);
      }

      // Initialize other fields
      _name             = name;
      _asString         = "Doorman for protected area \"" + _name + '"';
      _strict           = strict;
      _currentActorLock = new Object();
      _currentReaders   = new HashSet();
      _queue            = new Queue(queueSize);
      _maxQueueWaitTime = maxQueueWaitTime;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Name of this doorman. Used for logging and exceptions.
    */
   private final String _name;

   /**
    * Textual presentation of this object. Returned by {@link #toString()}.
    */
   private final String _asString;

   /**
    * Flag that indicates if thread synchronization state checking should be
    * strict or loose.
    */
   private final boolean _strict;

   /**
    * Maximum wait time in the queue. After reaching this period of time, a
    * {@link QueueTimeOutException} is thrown.
    */
   private final long _maxQueueWaitTime;

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
    * The queue that contains the waiting readers and/or writers.
    */
   private final Queue _queue;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the name of this doorman.
    *
    * @return
    *    the name, never <code>null</code>
    */
   public String getName() {
      return _name;
   }

   /**
    * Gets the maximum time to wait in the queue.
    *
    * @return
    *    the maximum wait time, always &gt; 0.
    */
   public long getMaxQueueWaitTime() {
      return _maxQueueWaitTime;
   }

   /**
    * Enters the 'protected area' as a reader. If necessary, this method will
    * wait until the area can be entered.
    *
    * @throws QueueTimeOutException
    *    if this thread was waiting in the queue for too long.
    */
   public void enterAsReader()
   throws QueueTimeOutException {

      final String THIS_METHOD    = "enterAsReader()";
      final String CALLING_CLASS  = Utils.getCallingClass();
      final String CALLING_METHOD = Utils.getCallingMethod();

      Thread reader = Thread.currentThread();

      synchronized (_currentActorLock) {

         // Check preconditions
         if (_currentWriter == reader) {
            final String DETAIL = _asString
                                + ": "
                                + reader.getName()
                                + " attempts to enter as a reader while it is already the active writer.";
            Log.log_1050(DOORMAN_CLASSNAME, THIS_METHOD,
                         CALLING_CLASS,     CALLING_METHOD,
                         DETAIL);
            if (_strict) {
               throw new ProgrammingException(DOORMAN_CLASSNAME, THIS_METHOD,
                                              CALLING_CLASS,     CALLING_METHOD,
                                              DETAIL,            null);
            } else {
               leaveAsWriter();
            }
         } else if (_currentReaders.contains(reader)) {
            final String DETAIL = _asString
                                + ": "
                                + reader.getName()
                                + " attempts to enter as a reader while it is already an active reader.";
            Log.log_1050(DOORMAN_CLASSNAME, THIS_METHOD,
                         CALLING_CLASS,     CALLING_METHOD,
                         DETAIL);
            if (_strict) {
               throw new ProgrammingException(DOORMAN_CLASSNAME, THIS_METHOD,
                                              CALLING_CLASS,     CALLING_METHOD,
                                              DETAIL,            null);
            } else {

               return;
            }
         }

         // If there is a current writer, then we need to wait in the queue
         boolean enterQueue = _currentWriter != null;
         synchronized (_queue) {

            // If there is no current writer, but there is already a queue,
            // then we also need to join it
            enterQueue = enterQueue ? true : !_queue.isEmpty();

            // Join the queue if necessary
            if (enterQueue) {
               _queue.add(reader, READ_QUEUE_ENTRY_TYPE);
            }
         }

         // If we don't have to join the queue, join the set of current
         // readers and go ahead
         if (!enterQueue) {
            _currentReaders.add(reader);

            return;
         }
      }

      // Wait for read access, which should be triggered by an interrupt
      try {
         Thread.sleep(_maxQueueWaitTime);

         // If we get here, then the time-out was exceeded
         synchronized (_currentActorLock) {

            // Reset interrupted state, if this thread was interrupted between
            // the Thread.sleep(long) call and the acquiry of the
            // _currentActorLock lock.
            Thread.interrupted();

            if (_currentReaders.contains(reader)) {

               return;
            }

            synchronized (_queue) {
               _queue.remove(reader);
            }
         }
         final String DETAIL = _asString
                             + ": Unable to add a thread named "
                             + reader.getName()
                             + " to queue. Time-out after "
                             + _maxQueueWaitTime
                             + " ms.";
         Log.log_1050(DOORMAN_CLASSNAME, THIS_METHOD,
                      CALLING_CLASS,     CALLING_METHOD,
                      DETAIL);
         throw new QueueTimeOutException(DETAIL);
      } catch (InterruptedException exception) {
         // fall through
      }

      synchronized (_currentActorLock) {
         if (! _currentReaders.contains(reader)) {
            final String DETAIL = _asString
                                + ": "
                                + reader.getName()
                                + " was interrupted in enterAsReader(), but not in the set of current readers.";
            throw Utils.logProgrammingError(DOORMAN_CLASSNAME, "enterAsReader()", Utils.getCallingClass(), Utils.getCallingMethod(), DETAIL);
         }
      }
   }

   /**
    * Enters the 'protected area' as a writer. If necessary, this method will
    * wait until the area can be entered.
    *
    * @throws QueueTimeOutException
    *    if this thread was waiting in the queue for too long.
    */
   public void enterAsWriter()
   throws QueueTimeOutException {

      final String THIS_METHOD    = "enterAsWriter()";
      final String CALLING_CLASS  = Utils.getCallingClass();
      final String CALLING_METHOD = Utils.getCallingMethod();

      Thread writer = Thread.currentThread();

      synchronized (_currentActorLock) {

         // Check preconditions
         if (_currentWriter == writer) {
            final String DETAIL = _asString
                                + ": "
                                + writer.getName()
                                + " attempts to enter as a writer but it is already the active writer.";
            Log.log_1050(DOORMAN_CLASSNAME, THIS_METHOD, Utils.getCallingClass(), Utils.getCallingMethod(), DETAIL);
            if (_strict) {
               throw new ProgrammingException(DOORMAN_CLASSNAME, THIS_METHOD,
                                              CALLING_CLASS,     CALLING_METHOD,
                                              DETAIL,            null);
            } else {
               return;
            }
         } else if (_currentReaders.contains(writer)) {
            final String DETAIL = _asString
                                + ": "
                                + writer.getName()
                                + " attempts to enter as a writer but it is already an active reader.";
            Log.log_1050(DOORMAN_CLASSNAME, THIS_METHOD, Utils.getCallingClass(), Utils.getCallingMethod(), DETAIL);
            if (_strict) {
               throw new ProgrammingException(DOORMAN_CLASSNAME, THIS_METHOD,
                                              CALLING_CLASS,     CALLING_METHOD,
                                              DETAIL,            null);
            } else {
               leaveAsReader();
            }
         }

         // If there is a current writer or one or more current readers, then
         // we need to wait in the queue
         boolean enterQueue = ! (_currentWriter == null && _currentReaders.isEmpty());

         // Join the queue if necessary
         if (enterQueue) {
            synchronized (_queue) {
               _queue.add(writer, WRITE_QUEUE_ENTRY_TYPE);
            }

         // If we don't have to join the queue, become the current writer and
         // return
         } else {
            _currentWriter = writer;
            return;
         }
      }

      // Wait for write access, which should be triggered by an interrupt
      try {
         Thread.sleep(_maxQueueWaitTime);

         // If we get here, then the time-out was exceeded
         synchronized (_currentActorLock) {

            // Reset interrupted state, if this thread was interrupted between
            // the Thread.sleep(long) call and the acquiry of the
            // _currentActorLock lock.
            Thread.interrupted();

            if (_currentWriter == writer) {
               return;
            }

            synchronized (_queue) {
               _queue.remove(writer);
            }
         }

         final String DETAIL = _asString
                             + ": Unable to add a thread named "
                             + writer.getName()
                             + " to queue. Time-out after "
                             + _maxQueueWaitTime
                             + " ms.";
         Log.log_1050(DOORMAN_CLASSNAME, THIS_METHOD, Utils.getCallingClass(), Utils.getCallingMethod(), DETAIL);
         throw new QueueTimeOutException(DETAIL);
      } catch (InterruptedException exception) {
         // fall through
      }

      synchronized (_currentActorLock) {
         if (_currentWriter != writer) {
            final String DETAIL = _asString
                                + " : "
                                + writer.getName()
                                + " was interrupted in enterAsWriter(), but the current writer is "
                                + _currentWriter.getName()
                                + '.';
            throw Utils.logProgrammingError(DOORMAN_CLASSNAME, "enterAsWriter()", Utils.getCallingClass(), Utils.getCallingMethod(), DETAIL);
         }
      }
   }

   /**
    * Leaves the 'protected area' as a reader.
    */
   public void leaveAsReader() {

      final String THIS_METHOD    = "leaveAsReader()";
      final String CALLING_CLASS  = Utils.getCallingClass();
      final String CALLING_METHOD = Utils.getCallingMethod();

      Thread reader = Thread.currentThread();

      synchronized (_currentActorLock) {
         boolean readerRemoved = _currentReaders.remove(reader);

         if (!readerRemoved) {
            final String DETAIL = _asString
                                + ": "
                                + reader.getName()
                                + " attempts to leave protected area as reader, but it is not an active reader.";
            Log.log_1050(DOORMAN_CLASSNAME, THIS_METHOD, Utils.getCallingClass(), Utils.getCallingMethod(), DETAIL);
            if (_strict) {
               throw new ProgrammingException(DOORMAN_CLASSNAME, THIS_METHOD,
                                              CALLING_CLASS,     CALLING_METHOD,
                                              DETAIL,            null);
            } else {
               return;
            }
         }

         if (_currentReaders.isEmpty()) {

            synchronized (_queue) {

               // Determine if the queue has a writer atop, a reader atop or is
               // empty
               QueueEntryType type = _queue.getTypeOfFirst();

               if (type == WRITE_QUEUE_ENTRY_TYPE) {

                  // If a writer is waiting, activate it
                  _currentWriter = _queue.pop();
                  _currentWriter.interrupt();
               } else if (type == READ_QUEUE_ENTRY_TYPE) {

                  // If a reader leaves, the queue cannot contain a reader at the
                  // top, it must be either empty or have a writer at the top
                  final String DETAIL = _asString
                                      + ": Found reader at top of queue while a reader is leaving the protected area.";
                  throw Utils.logProgrammingError(DOORMAN_CLASSNAME, THIS_METHOD,
                                                  CALLING_CLASS,     CALLING_METHOD,
                                                  DETAIL,            null);
               }
            }
         }
      }
   }

   /**
    * Leaves the 'protected area' as a writer.
    */
   public void leaveAsWriter() {

      final String THIS_METHOD    = "leaveAsWriter()";
      final String CALLING_CLASS  = Utils.getCallingClass();
      final String CALLING_METHOD = Utils.getCallingMethod();

      Thread writer = Thread.currentThread();

      synchronized (_currentActorLock) {

         if (_currentWriter != writer) {
            final String DETAIL = _asString
                                + ": "
                                + writer.getName()
                                + " attempts to leave protected area as writer, but it is not the current writer.";
            Log.log_1050(DOORMAN_CLASSNAME, THIS_METHOD, Utils.getCallingClass(), Utils.getCallingMethod(), DETAIL);
            if (_strict) {
               throw new ProgrammingException(DOORMAN_CLASSNAME, THIS_METHOD,
                                              CALLING_CLASS,     CALLING_METHOD,
                                              DETAIL,            null);
            } else {
               return;
            }
         }

         synchronized (_queue) {

            // Determine if the queue has a writer atop, a reader atop or is
            // empty
            QueueEntryType type = _queue.getTypeOfFirst();

            // If a writer is waiting, activate it alone
            if (type == WRITE_QUEUE_ENTRY_TYPE) {

               _currentWriter = _queue.pop();
               _currentWriter.interrupt();

            // If readers are on top, active all readers atop
            } else if (type == READ_QUEUE_ENTRY_TYPE) {
               do {
                  Thread reader = _queue.pop();
                  _currentReaders.add(reader);
                  reader.interrupt();
               } while (_queue.getTypeOfFirst() == READ_QUEUE_ENTRY_TYPE);

            // Otherwise there is no active thread, make sure to reset the
            // current writer
            } else {
               _currentWriter = null;
            }
         }
      }
   }

   /**
    * Returns a textual representation of this object.
    *
    * @return
    *    a textual representation of this object, never <code>null</code>.
    */
   public String toString() {
      return _asString;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Queue of waiting reader and writer threads.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 1.0.0
    */
   private final class Queue
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
      public Queue(int capacity)
      throws IllegalArgumentException {

         // Check preconditions
         if (capacity < 0) {
            throw new IllegalArgumentException("capacity (" + capacity + ") < 0");
         }

         // Initialize fields
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
       * keys and their {@link Doorman.QueueEntryType types} as values.
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
       * <code>({@link Doorman.QueueEntryType}) </code>{@link #_entries}<code>.</code>{@link LinkedList#get(int) get}<code>(0)</code>
       * (if {@link #_entries} is not empty).
       */
      private QueueEntryType _typeOfFirst;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Determines if this queue is empty.
       *
       * @return
       *    <code>true</code> if this queue is empty, <code>false</code> if it
       *    is not.
       */
      public boolean isEmpty() {
         return (_first == null);
      }

      /**
       * Gets the type of the first waiting thread in this queue. If this
       * queue is empty, then <code>null</code> is returned.
       *
       * @return
       *    <code>null</code> if this queue is empty;
       *    {@link #READ_QUEUE_ENTRY_TYPE} is the first thread in this queue
       *    is waiting for read access;
       *    {@link #WRITE_QUEUE_ENTRY_TYPE} is the first thread in this queue
       *    is waiting for write access;
       */
      public QueueEntryType getTypeOfFirst() {
         return _typeOfFirst;
      }

      /**
       * Adds the specified thread to the queue of waiting threads.
       *
       * @param thread
       *    the thread to be added, should not be <code>null</code>.
       *
       * @param type
       *    the type of thread, should be either
       *    {@link #READ_QUEUE_ENTRY_TYPE} or {@link #WRITE_QUEUE_ENTRY_TYPE}.
       *
       * @throws Error
       *    if the specified thread is already in this queue.
       */
      public void add(Thread thread, QueueEntryType type)
      throws Error {

         final String THIS_METHOD = "add(java.lang.Thread,"
                                  + QueueEntryType.class.getName()
                                  + ')';
         final String CALLING_CLASS  = Utils.getCallingClass();
         final String CALLING_METHOD = Utils.getCallingMethod();

         // Check preconditions
         if (_entryTypes.containsKey(thread)) {
            QueueEntryType existingType = (QueueEntryType) _entryTypes.get(thread);
            final String DETAIL = _asString
                                + ": "
                                + thread.getName()
                                + " is already in this queue as a "
                                + existingType
                                + ", cannot add it as a "
                                + type
                                + '.';

            // XXX: We have to throw an Error, because the API docs state it
            //      (since XINS 1.0.0)
            ProgrammingException e =
               Utils.logProgrammingError(QUEUE_CLASSNAME, THIS_METHOD,
                                         CALLING_CLASS,   CALLING_METHOD,
                                         DETAIL,          null);
            Error error = new Error();
            ExceptionUtils.setCause(error, e);
            throw error;
         }

         // If the queue is empty, then store the new waiter as the first
         if (_first == null) {
            _first       = thread;
            _typeOfFirst = type;
         }

         // Store the waiter thread and its type
         _entryTypes.put(thread, type);
         _entries.addLast(thread);
      }

      /**
       * Pops the first waiting thread from this queue, removes it and then
       * returns it.
       *
       * @return
       *    the top waiting thread, never <code>null</code>.
       *
       * @throws Error
       *    if this queue is empty.
       */
      public Thread pop() throws Error {

         final String THIS_METHOD    = "pop()";
         final String CALLING_CLASS  = Utils.getCallingClass();
         final String CALLING_METHOD = Utils.getCallingMethod();

         // Check preconditions
         if (_first == null) {
            // XXX: We have to throw an Error, because the API docs state it
            //      (since XINS 1.0.0)
            final String DETAIL = "This queue is empty.";
            ProgrammingException e =
               Utils.logProgrammingError(QUEUE_CLASSNAME, THIS_METHOD,
                                         CALLING_CLASS,   CALLING_METHOD,
                                         DETAIL,          null);
            Error error = new Error();
            ExceptionUtils.setCause(error, e);
            throw error;
         }

         Thread oldFirst = _first;

         // Remove the current first
         _entries.removeFirst();
         _entryTypes.remove(oldFirst);

         // Get the new first, now that the other one is removed
         boolean empty = _entries.isEmpty();
         _first        = empty ? null : (Thread)         _entries.getFirst();
         _typeOfFirst  = empty ? null : (QueueEntryType) _entryTypes.get(_first);

         return oldFirst;
      }

      /**
       * Removes the specified thread from this queue.
       *
       * @param thread
       *    the thread to be removed from this queue, should not be
       *    <code>null</code>.
       *
       * @throws Error
       *    if this queue does not contain the specified thread.
       */
      public void remove(Thread thread)
      throws Error {

         final String THIS_METHOD    = "remove(java.lang.Thread)";
         final String CALLING_CLASS  = Utils.getCallingClass();
         final String CALLING_METHOD = Utils.getCallingMethod();

         if (thread == _first) {

            // Remove the current first
            _entries.removeFirst();

            // Get the new first, now that the other one is removed
            boolean empty = _entries.isEmpty();
            _first        = empty ? null : (Thread)         _entries.getFirst();
            _typeOfFirst  = empty ? null : (QueueEntryType) _entryTypes.get(_first);

         } else {

            // Remove the thread from the list
            if (! _entries.remove(thread)) {
               final String DETAIL = _asString
                                   + ": "
                                   + thread.getName()
                                   + " is not in this queue.";
               throw Utils.logProgrammingError(QUEUE_CLASSNAME, THIS_METHOD,
                                               CALLING_CLASS,   CALLING_METHOD,
                                               DETAIL,          null);
            }
         }

         _entryTypes.remove(thread);
      }
   }

   /**
    * Type of an entry in a queue for a doorman.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 1.0.0
    */
   public static final class QueueEntryType
   extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Creates a new <code>QueueEntryType</code> with the specified
       * description.
       *
       * @param description
       *    the description of this entry type, cannot be
       *    <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>description == null</code>.
       */
      public QueueEntryType(String description)
      throws IllegalArgumentException {
         MandatoryArgumentChecker.check("description", description);
         _description = description;
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      /**
       * Description of this entry type. Never <code>null</code>.
       */
      private final String _description;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns a textual representation of this object.
       *
       * @return
       *    a textual representation of this object, never <code>null</code>.
       */
      public String toString() {
         return _description;
      }
   }
}
