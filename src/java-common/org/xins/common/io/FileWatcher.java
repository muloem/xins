/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
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
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class FileWatcher extends Thread {

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
    * Creates a new <code>FileWatcher</code> for the specified file.
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
      _file     = new File(file);
      _interval = interval;
      _listener = listener;
      _stopped  = false;

      // Configure thread as daemon
      setDaemon(true);

      // Immediately check if the file exists
      try {
         if (_file.exists()) {
            _lastModified = _file.lastModified();
         }
      } catch (SecurityException exception) {
         // ignore
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
    * Delay in seconds, at least 1.
    */
   private int _interval;

   /**
    * The listener. Not <code>null</code>
    */
   private final Listener _listener;

   /**
    * Timestamp of the last modification of the file. The value
    * <code>-1</code> indicates that the file could not be found the last time
    * this was checked.
    *
    * <p>Initially this field is <code>-1L</code>.
    */
   private long _lastModified;

   /**
    * Flag that indicates if this thread has been stopped.
    */
   private boolean _stopped;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Runs this thread. This method should not be called directly, call
    * {@link #start()} instead. That method will call this method.
    *
    * @throws IllegalStateException
    *    if <code>{@link Thread#currentThread()} != this</code>.
    */
   public void run() throws IllegalStateException {

      // Check preconditions
      if (Thread.currentThread() != this) {
         throw new IllegalStateException("Thread.currentThread() != this");
      }

      Log.log_1200(_file.getPath(), _interval);

      while (! _stopped) {
         try {
            while(! _stopped) {
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
    * Returns the current interval. This method can only be called from the listener
    * callback methods. If it is not, an exception is thrown.
    *
    * @return interval
    *    the current interval in seconds, always greater than or equal to 1.
    *
    * @throws IllegalStateException
    *    if <code>{@link Thread#currentThread()} != this</code>.
    */
   public int getInterval() throws IllegalStateException {

      // Check preconditions
      if (Thread.currentThread() != this) {
         throw new IllegalStateException("Thread.currentThread() != this");
      }

      return _interval;
   }

   /**
    * Changes the interval. This method can only be called from the listener
    * callback methods. If it is not, an exception is thrown.
    *
    * @param newInterval
    *    the new interval in seconds, must be greater than or equal to 1.
    *
    * @throws IllegalStateException
    *    if <code>{@link Thread#currentThread()} != this</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>interval &lt; 1</code>
    */
   public void setInterval(int newInterval)
   throws IllegalStateException, IllegalArgumentException {

      // Check preconditions
      if (Thread.currentThread() != this) {
         throw new IllegalStateException("Thread.currentThread() != this");
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
   public void end() {
      _stopped = true;

      Log.log_1202(_file.getPath());

      this.interrupt();
   }

   /**
    * Checks if the file changed. The following algorithm is used:
    *
    * <ul>
    *    <li>check if the file can be found;
    *    <li>if so, then determine when the file was last modified;
    *    <li>if either the file existence check or the file modification check
    *        causes a {@link SecurityException} to be thrown, then
    *        {@link Listener#securityException(SecurityException)} is called
    *        and the method returns;
    *    <li>otherwise if the file does not exist, then
    *        {@link Listener#fileNotFound()} is called and the method returns;
    *    <li>otherwise if the file does exist, but previously did not exist,
    *        then {@link Listener#fileFound()} is called and the method
    *        returns;
    *    <li>otherwise if the file was modified, then {@link Listener#fileModified()} is
    *        called and the method returns;
    *    <li>otherwise the file was not modified, then
    *        {@link Listener#fileNotModified()} is called and the method
    *        returns.
    * </ul>
    */
   private void check() {

      // Variable to store the file modification timestamp in. The value -1
      // indicates the file does not exist.
      long lastModified;

      // Check if the file can be found and if so, when it was last modified
      try {
         if (_file.exists()) {
            lastModified = _file.lastModified();
         } else {
            lastModified = -1;
         }

      // Authorisation problem; our code is not allowed to call File.exists()
      // and/or File.lastModified()
      } catch (SecurityException securityException) {
         try {
            _listener.securityException(securityException);
         } catch (Throwable t) {
            // TODO: Log
         }

         return;
      }

      // File can not be found
      if (lastModified == -1) {

         // Set _lastModified to -1, which indicates the file did not exist
         // last time it was checked.
         _lastModified = -1;

         // Notify the listener
         try {
            _listener.fileNotFound();
         } catch (Throwable t) {
            // TODO: Log
         }

      // Previously the file could not be found, but now it can
      } else if (_lastModified == -1) {

         // Update the field that stores the last known modification date
         _lastModified = lastModified;

         // Notify the listener
         try {
            _listener.fileFound();
         } catch (Throwable t) {
            // TODO: Log
         }

      // File has been modified
      } else if (lastModified != _lastModified) {

         // Update the field that stores the last known modification date
         _lastModified = lastModified;

         // Notify listener
         try {
            _listener.fileModified();
         } catch (Throwable t) {
            // TODO: Log
         }

      // File has not been modified
      } else {

         // Notify listener
         try {
            _listener.fileNotModified();
         } catch (Throwable t) {
            // TODO: Log
         }
      }
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Interface for file watcher listeners.
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
       * file still exists, either {@link #fileModified()} or
       * {@link #fileNotModified()} is called.
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
