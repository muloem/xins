/*
 * $Id$
 */
package org.xins.util.collections.expiry;

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
   private ExpiryStrategy(long timeOut, long precision)
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

      // Determine number of slots
      long slotCount = timeOut / precision;
      if ((precision % precision) > 0) {
         slotCount++;
      }

      // Store data
      _timeOut   = timeOut;
      _precision = precision;
      _slotCount = (int) slotCount;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The time-out, in milliseconds.
    */
   public final long _timeOut;

   /**
    * The time-out precision, in milliseconds.
    */
   public final long _precision;

   /**
    * The number of slots that should be used by expiry collections that use
    * this strategy.
    */
   public final int _slotCount;


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
}
