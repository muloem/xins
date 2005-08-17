/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.io;

import java.io.File;
import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;

/**
 * File watcher thread. This thread checks if a file changed and if it has, it
 * notifies the listener. The check is performed every <em>n</em> seconds,
 * where <em>n</em> can be configured.
 *
 * <p>Initially this thread will be a daemon thread. This can be changed by
 * calling {@link #setDaemon(boolean)}.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class FileWatcher extends Thread {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = FileWatcher.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>FileWatcher</code> for the specified file, with the
    * specified interval.
    *
    * @param file
    *    the name of the file to watch, cannot be <code>null</code>.
    *
    * @param interval
    *    the interval in seconds, must be greater than or equal to 1.
    *
    * @param listener
    *    the object to notify on events, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>file == null || listener == null || interval &lt; 1</code>
    */
   public FileWatcher(String file, int interval, Listener listener)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("file", file, "listener", listener);
      if (interval < 1) {
         throw new IllegalArgumentException("interval (" + interval + ") < 1");
      }

      // Store the information
      _file          = new File(file);
      _interval      = interval;
      _listener      = listener;
      _listenerClass = listener.getClass().getName();
      _shouldStop    = false;

      // Configure thread as daemon
      setDaemon(true);

      // Immediately check if the file can be read from
      try {
         if (_file.canRead()) {
            _lastModified = _file.lastModified();
         }
      } catch (SecurityException exception) {
         // ignore
         // TODO: Log
      }
   }


   /**
    * Creates a new <code>FileWatcher</code> for the specified file.
    *
    * <p>The interval must be set before the thread can be started.
    *
    * @param file
    *    the name of the file to watch, cannot be <code>null</code>.
    *
    * @param listener
    *    the object to notify on events, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>file == null || listener == null</code>
    *
    * @since XINS 1.2.0
    */
   public FileWatcher(String file, Listener listener)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("file", file, "listener", listener);

      // Store the information
      _file          = new File(file);
      _interval      = 0;
      _listener      = listener;
      _listenerClass = listener.getClass().getName();
      _shouldStop    = false;

      // Configure thread as daemon
      setDaemon(true);

      // Immediately check if the file can be read from
      try {
         if (_file.canRead()) {
            _lastModified = _file.lastModified();
         }
      } catch (SecurityException exception) {
         // ignore
         // TODO: Log
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The file to watch. Not <code>null</code>.
    */
   private final File _file;

   /**
    * Delay in seconds, at least 1. When the interval is uninitialized, the
    * value of this field is less than 1.
    */
   private int _interval;

   /**
    * The listener. Not <code>null</code>
    */
   private final Listener _listener;

   /**
    * The name of the class of the listener. Not <code>null</code>
    */
   private final String _listenerClass;

   /**
    * Timestamp of the last modification of the file. The value
    * <code>-1L</code> indicates that the file could not be found the last
    * time this was checked.
    *
    * <p>Initially this field is <code>-1L</code>.
    */
   private long _lastModified;

   /**
    * Flag that indicates if this thread has been ordered to stop.
    */
   private boolean _shouldStop;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Runs this thread. This method should not be called directly, call
    * {@link #start()} instead. That method will call this method.
    *
    * @throws IllegalStateException
    *    if <code>{@link Thread#currentThread()} != this</code> or if the
    *    interval was not set yet.
    */
   public void run() throws IllegalStateException {

      // TODO: Check state

      // Check preconditions
      if (Thread.currentThread() != this) {
         throw new IllegalStateException("Thread.currentThread() != this");
      } else if (_interval < 1) {
         throw new IllegalStateException("The interval has not been set yet.");
      }

      Log.log_1200(_file.getPath(), _interval);

      while (! _shouldStop) {
         try {
            while(! _shouldStop) {

               // Wait for the designated amount of time
               sleep(((long)_interval) * 1000L);

               // Check if the file changed
               check();
            }
         } catch (InterruptedException exception) {
            // Fall through
         }
      }
   }

   /**
    * Returns the current interval.
    *
    * @return interval
    *    the current interval in seconds, always greater than or equal to 1,
    *    except if the interval is not initialized yet, in which case 0 is
    *    returned.
    */
   public synchronized int getInterval() {
      return _interval;
   }

   /**
    * Changes the file check interval.
    *
    * @param newInterval
    *    the new interval in seconds, must be greater than or equal to 1.
    *
    * @throws IllegalArgumentException
    *    if <code>interval &lt; 1</code>
    */
   public synchronized void setInterval(int newInterval)
   throws IllegalArgumentException {

      // TODO: Check state

      // Check preconditions
      if (newInterval < 1) {
         throw new IllegalArgumentException("newInterval (" + newInterval + ") < 1");
      }

      // Change the interval
      if (newInterval != _interval) {
         Log.log_1201(_file.getPath(), _interval, newInterval);
         _interval = newInterval;
      }
   }

   /**
    * Stops this thread.
    */
   public synchronized void end() {

      // TODO: Check state

      _shouldStop = true;

      Log.log_1202(_file.getPath());

      this.interrupt();
   }

   /**
    * Checks if the file changed. The following algorithm is used:
    *
    * <ul>
    *    <li>check if the file is readable;
    *    <li>if so, then determine when the file was last modified;
    *    <li>if either the file existence check or the file modification check
    *        causes a {@link SecurityException} to be thrown, then
    *        {@link Listener#securityException(SecurityException)} is called
    *        and the method returns;
    *    <li>otherwise if the file is not readable (it may not exist), then
    *        {@link Listener#fileNotFound()} is called and the method returns;
    *    <li>otherwise if the file is readable, but previously was not,
    *        then {@link Listener#fileFound()} is called and the method
    *        returns;
    *    <li>otherwise if the file was modified, then {@link Listener#fileModified()} is
    *        called and the method returns;
    *    <li>otherwise the file was not modified, then
    *        {@link Listener#fileNotModified()} is called and the method
    *        returns.
    * </ul>
    *
    * @since XINS 1.2.0
    */
   public synchronized void check() {

      // TODO: Check state

      final String THIS_METHOD = "check()";

      // Variable to store the file modification timestamp in. The value -1
      // indicates the file does not exist.
      long lastModified;

      // Check if the file can be read from and if so, when it was last
      // modified
      try {
         if (_file.canRead()) {
            lastModified = _file.lastModified();
         } else {
            lastModified = -1L;
         }

      // Authorisation problem; our code is not allowed to call canRead()
      // and/or lastModified() on the File object
      } catch (SecurityException securityException) {

         // Notify the listener
         try {
            _listener.securityException(securityException);

         // Ignore any exceptions thrown by the listener callback method
         } catch (Throwable t) {
            final String SUBJECT_METHOD = "securityException(java.lang.SecurityException)";
            final String DETAIL         = null;
            Log.log_1051(t, CLASSNAME, THIS_METHOD, _listenerClass, SUBJECT_METHOD, DETAIL);
         }

         // Short-circuit
         return;
      }

      // File can not be found
      if (lastModified == -1L) {

         // Set _lastModified to -1, which indicates the file did not exist
         // last time it was checked.
         _lastModified = -1L;

         // Notify the listener
         try {
            _listener.fileNotFound();

         // Ignore any exceptions thrown by the listener callback method
         } catch (Throwable t) {
            final String SUBJECT_METHOD = "fileNotFound()";
            final String DETAIL         = null;
            Log.log_1051(t, CLASSNAME, THIS_METHOD, _listenerClass, SUBJECT_METHOD, DETAIL);
         }

      // Previously the file could not be found, but now it can
      } else if (_lastModified == -1L) {

         // Update the field that stores the last known modification date
         _lastModified = lastModified;

         // Notify the listener
         try {
            _listener.fileFound();

         // Ignore any exceptions thrown by the listener callback method
         } catch (Throwable t) {
            final String SUBJECT_METHOD = "fileFound()";
            final String DETAIL         = null;
            Log.log_1051(t, CLASSNAME, THIS_METHOD, _listenerClass, SUBJECT_METHOD, DETAIL);
         }

      // File has been modified
      } else if (lastModified != _lastModified) {

         // Update the field that stores the last known modification date
         _lastModified = lastModified;

         // Notify listener
         try {
            _listener.fileModified();

         // Ignore any exceptions thrown by the listener callback method
         } catch (Throwable t) {
            final String SUBJECT_METHOD = "fileModified()";
            final String DETAIL         = null;
            Log.log_1051(t, CLASSNAME, THIS_METHOD, _listenerClass, SUBJECT_METHOD, DETAIL);
         }

      // File has not been modified
      } else {

         // Notify listener
         try {
            _listener.fileNotModified();

         // Ignore any exceptions thrown by the listener callback method
         } catch (Throwable t) {
            final String SUBJECT_METHOD = "fileNotModified()";
            final String DETAIL         = null;
            Log.log_1051(t, CLASSNAME, THIS_METHOD, _listenerClass, SUBJECT_METHOD, DETAIL);
         }
      }
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Interface for file watcher listeners.
    *
    * <p>Note that exceptions thrown by these callback methods will be ignored
    * by the <code>FileWatcher</code>.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    *
    * @since XINS 1.0.0
    */
   public interface Listener {

      /**
       * Callback method, called if the file is checked but cannot be found.
       * This method is called the first time the file is determined not to
       * exist, but also each consecutive time the file is still determined
       * not to be found.
       */
      void fileNotFound();

      /**
       * Callback method, called if the file is found for the first time since
       * the <code>FileWatcher</code> was started. Each consecutive time the
       * file still exists (and is readable), either
       * {@link #fileModified()} or {@link #fileNotModified()} is called.
       */
      void fileFound();

      /**
       * Callback method, called if an authorisation error prevents that the
       * file is checked for existence and last modification date.
       *
       * @param exception
       *    the caught exception, not <code>null</code>.
       */
      void securityException(SecurityException exception);

      /**
       * Callback method, called if the file was checked and found to be
       * modified.
       */
      void fileModified();

      /**
       * Callback method, called if the file was checked but found not to be
       * modified.
       */
      void fileNotModified();
   }
}
