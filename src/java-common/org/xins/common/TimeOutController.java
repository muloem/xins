/*
 * $Id$
 */
package org.xins.common;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Utility class for executing a task with a certain time-out period.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.196
 */
public final class TimeOutController extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Runs the specified thread with a specific time-out. If the thread does
    * not finish within the specified time-out period, then the thread is
    * interrupted using the {@link Thread#interrupt()} method and a
    * {@link TimeOutException} is thrown.
    *
    * @param task
    *    the thread to run, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the timeOut in milliseconds, must be &gt; 0.
    *
    * @throws IllegalArgumentException
    *    if <code>task == null || timeOut &lt;= 0</code>.
    *
    * @throws IllegalThreadStateException
    *    if the thread was already started.
    *
    * @throws SecurityException
    *    if the thread did not finish within the total time-out period, but
    *    the interruption of the thread was disallowed (see
    *    {@link Thread#interrupt()}).
    *
    * @throws TimeOutException
    *    if the thread did not finish within the total time-out period and was
    *    interrupted.
    *
    * @deprecated
    *    Deprecated since XINS 0.204.
    *    Use {@link #execute(Runnable,int)} instead.
    */
   public static final void execute(Thread task, int timeOut)
   throws IllegalArgumentException,
          IllegalThreadStateException,
          SecurityException,
          TimeOutException {

      execute((Runnable) task, timeOut);
   }

   /**
    * Runs the specified task with a specific time-out. If the task does
    * not finish within the specified time-out period, then the thread
    * executing that task is interrupted using the {@link Thread#interrupt()}
    * method and a {@link TimeOutException} is thrown.
    *
    * @param task
    *    the task to run, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the timeOut in milliseconds, must be &gt; 0.
    *
    * @throws IllegalArgumentException
    *    if <code>task == null || timeOut &lt;= 0</code>.
    *
    * @throws IllegalThreadStateException
    *    if the specified task is a {@link Thread} that is already started.
    *
    * @throws SecurityException
    *    if the thread did not finish within the total time-out period, but
    *    the interruption of the thread was disallowed (see
    *    {@link Thread#interrupt()}).
    *
    * @throws TimeOutException
    *    if the thread did not finish within the total time-out period and was
    *    interrupted.
    *
    * @since XINS 0.204
    */
   public static final void execute(Runnable task, int timeOut)
   throws IllegalArgumentException,
          IllegalThreadStateException,
          SecurityException,
          TimeOutException {

      // Check preconditions
      MandatoryArgumentChecker.check("task", task);
      if (timeOut <= 0) {
         throw new IllegalArgumentException("timeOut (" + timeOut + ") <= 0");
      }

      // We need a Thread instance. If the argument is already a Thread
      // instance, then use it, otherwise construct a new Thread instance.
      Thread thread;
      if (task instanceof Thread) {
         thread = (Thread) task;
      } else {
         // TODO: Use a thread pool
         thread = new Thread(task);
      }

      // Start the thread. This may throw an IllegalThreadStateException.
      thread.start();

      // Wait for the thread to finish, within limits
      try {
         thread.join(timeOut);
      } catch (InterruptedException exception) {
         // ignore
         // TODO: Log?
      }

      // If the thread is still running at this point, it should stop
      if (thread.isAlive()) {

         // Interrupt the thread. This may throw a SecurityException
         thread.interrupt();

         throw new TimeOutException();
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>TimeOutController</code> object.
    */
   private TimeOutController() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
