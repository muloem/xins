/*
 * $Id$
 */
package org.xins.util.collections.expiry;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Expiry strategy. A strategy maintains a time-out and a time-out precision.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class ExpiryStrategy extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The logging category used by this class. This class field is never
    * <code>null</code>.
    */
   private static final Logger LOG = Logger.getLogger(ExpiryStrategy.class.getName());

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
    * Constructs a new <code>ExpiryStrategy</code>.
    *
    * @param timeOut
    *    the time-out, in milliseconds.
    *
    * @param precision
    *    the time-out precision, in milliseconds.
    *
    * @throws IllegalArgumentException
    *    if <code>timeOut &lt; 1
    *          || precision &lt; 1
    *          || timeOut &lt; precision</code>
    */
   public ExpiryStrategy(long timeOut, long precision)
   throws IllegalArgumentException {

      // Check preconditions
      if (timeOut < 1 || precision < 1) {
         if (timeOut < 1 && precision < 1) {
            throw new IllegalArgumentException("timeOut (" + timeOut + ") < 1 && precision (" + precision + ") < 1");
         } else if (timeOut < 1) {
            throw new IllegalArgumentException("timeOut (" + timeOut + ") < 1");
         } else {
            throw new IllegalArgumentException("precision (" + precision + ") < 1");
         }
      } else if (timeOut < precision) {
         throw new IllegalArgumentException("timeOut < precision");
      }

      // Determine instance number
      synchronized (INSTANCE_COUNT_LOCK) {
         _instanceNum = INSTANCE_COUNT++;
      }

      // Determine number of slots
      long slotCount = timeOut / precision;
      if ((precision % precision) > 0) {
         slotCount++;
      }

      // Initialize fields
      _timeOut   = timeOut;
      _precision = precision;
      _slotCount = (int) slotCount;
      _folders   = new ArrayList();

      if (LOG.isDebugEnabled()) {
         LOG.debug("Created " + toString() + ", time-out duration is " + _timeOut + " ms, precision is " + _precision + " ms.");
      }

      // Create and start the timer thread
      _timerThread = new TimerThread();
      _timerThread.setDaemon(true);
      _timerThread.start();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The instance number of this instance.
    */
   private final int _instanceNum;

   /**
    * The time-out, in milliseconds.
    */
   private final long _timeOut;

   /**
    * The time-out precision, in milliseconds.
    */
   private final long _precision;

   /**
    * The number of slots that should be used by expiry collections that use
    * this strategy.
    */
   private final int _slotCount;

   /**
    * The list of folders associated with this strategy.
    */
   private final List _folders;

   /**
    * The timer thread. Not <code>null</code>.
    */
   private final TimerThread _timerThread;

   /**
    * Flag that indicates if the time thread should stop or not. Initially
    * <code>false</code>, ofcourse.
    */
   private boolean _stop;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the time-out.
    *
    * @return
    *    the time-out, in milliseconds.
    */
   public final long getTimeOut() {
      return _timeOut;
   }

   /**
    * Returns the time-out precision.
    *
    * @return
    *    the time-out precision, in milliseconds.
    */
   public final long getPrecision() {
      return _precision;
   }

   /**
    * Returns the number of slots that should be used by expiry collections
    * that use this strategy.
    *
    * @return
    *    the slot count, always &gt;= 1.
    */
   public final int getSlotCount() {
      return _slotCount;
   }

   void folderAdded(ExpiryFolder folder)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("folder", folder);

      LOG.debug(folder.toString() + " associated with " + toString() + '.');

      synchronized (_folders) {
         _folders.add(folder);
      }
   }

   /**
    * Stops the thread that generates ticks that are passed to the registered
    * expiry folders.
    *
    * @throws IllegalStateException
    *    if this strategy was already stopped.
    */
   public void stop() {

      // Check preconditions
      if (_stop) {
         throw new IllegalStateException("Already stopped.");
      }

      _stop = true;
      _timerThread.interrupt();
   }

   private void doTick() {
      synchronized (_folders) {
         int count = _folders.size();
         for (int i = 0; i < count; i++) {
            ExpiryFolder folder = (ExpiryFolder) _folders.get(i);
            folder.tick();
         }
      }
   }

   public String toString() {
      return "XINS ExpiryStrategy #" + _instanceNum;
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   private final class TimerThread extends Thread {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      public TimerThread() {
         super(ExpiryStrategy.this.toString() + " timer thread");
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      public void run() {

         LOG.debug(getName() + " started.");

         while (! _stop) {
            try {
               while (! _stop) {
                  sleep(_precision);
                  doTick();
               }
            } catch (InterruptedException exception) {
               // fall through
            }
         }

         LOG.debug(getName() + " stopped.");
      }

      public String toString() {
         return getName();
      }
   }
}
