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

      _timeOut   = timeOut;
      _precision = precision;
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
}
