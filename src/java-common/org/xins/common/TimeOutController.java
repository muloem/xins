/*
 * $Id$
 */
package org.xins.common;

import org.xins.common.ExceptionUtils;
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
    * @param thread
    *    the thread to run, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the timeOut in milliseconds, must be &gt; 0.
    *
    * @throws IllegalArgumentException
    *    if <code>thread == null || timeOut &lt;= 0</code>.
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
    */
   public static final void execute(Thread thread, int timeOut)
   throws IllegalArgumentException,
          IllegalThreadStateException,
          SecurityException,
          TimeOutException {

      // Check preconditions
      MandatoryArgumentChecker.check("thread", thread);
      if (timeOut <= 0) {
         throw new IllegalArgumentException("timeOut (" + timeOut + ") <= 0");
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
