/*
 * $Id$
 */
package org.xins.server;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Base class for function implementation classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class Function
extends Object {

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
    * Constructs a new <code>Function</code> object.
    *
    * @param api
    *    the API to which this function belongs, not <code>null</code>.
    *
    * @param name
    *    the name, not <code>null</code>.
    *
    * @param version
    *    the version of the specification this function implements, not
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>api == null || name == null || version == null</code>.
    */
   protected Function(API api, String name, String version)
   throws IllegalArgumentException {

      // Check argument
      MandatoryArgumentChecker.check("api", api, "name", name, "version", version);

      _api     = api;
      _name    = name;
      _version = version;

      _api.functionAdded(this);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API implementation this function is part of.
    */
   private final API _api;

   /**
    * The name of this function.
    */
   private final String _name;

   /**
    * The version of the specification this function implements.
    */
   private final String _version;

   /**
    * Statistics object linked to this function.
    */
   private final Statistics _statistics = new Statistics();

   /**
    * Lock object for a successful call.
    */
   private final Object _successfulCallLock = new Object();

   /**
    * Lock object for an unsuccessful call.
    */
   private final Object _unsuccessfulCallLock = new Object();

   /**
    * The number of successful calls executed up until now.
    */
   private int _successfulCalls;

   /**
    * The number of unsuccessful calls executed up until now.
    */
   private int _unsuccessfulCalls;

   /**
    * The start time of the most recent successful call.
    */
   private long _lastSuccessfulStart;

   /**
    * The start time of the most recent unsuccessful call.
    */
   private long _lastUnsuccessfulStart;

   /**
    * The duration of the most recent successful call.
    */
   private long _lastSuccessfulDuration;

   /**
    * The duration of the most recent unsuccessful call.
    */
   private long _lastUnsuccessfulDuration;

   /**
    * The total duration of all successful calls up until now.
    */
   private long _successfulDuration;

   /**
    * The total duration of all unsuccessful calls up until now.
    */
   private long _unsuccessfulDuration;

   /**
    * The minimum time a successful call took.
    */
   private long _successfulMin = Long.MAX_VALUE;

   /**
    * The minimum time an unsuccessful call took.
    */
   private long _unsuccessfulMin = Long.MAX_VALUE;

   /**
    * The maximum time a successful call took.
    */
   private long _successfulMax;

   /**
    * The maximum time an unsuccessful call took.
    */
   private long _unsuccessfulMax;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the name of this function.
    *
    * @return
    *    the name, not <code>null</code>.
    */
   final String getName() {
      return _name;
   }

   /**
    * Returns the specification version for this function.
    *
    * @return
    *    the version, not <code>null</code>.
    */
   final String getVersion() {
      return _version;
   }

   /**
    * Returns the call statistics for this function.
    *
    * @return
    *    the statistics, never <code>null</code>.
    */
   final Statistics getStatistics() {
      return _statistics;
   }

   /**
    * Callback method that may be called after a call to this function. This
    * method will store statistics-related information.
    *
    * <p />This method does not <em>have</em> to be called. If statistics
    * gathering is disabled, then this method should not be called.
    *
    * @param start
    *    the timestamp indicating when the call was started, as a number of
    *    milliseconds since midnight January 1, 1970 UTC.
    *
    * @param duration
    *    the duration of the function call, as a number of milliseconds.
    *
    * @param success
    *    indication if the call was successful.
    *
    * @param code
    *    the function result code, or <code>null</code>.
    */
   final void performedCall(long start, long duration, boolean success, String code) {
      if (success) {
         synchronized (_successfulCallLock) {
            _lastSuccessfulStart    = start;
            _lastSuccessfulDuration = duration;
            _successfulCalls++;
            _successfulDuration += duration;
            _successfulMin = _successfulMin > duration ? duration : _successfulMin;
            _successfulMax = _successfulMax < duration ? duration : _successfulMax;
         }
      } else {
         synchronized (_unsuccessfulCallLock) {
            _lastUnsuccessfulStart    = start;
            _lastUnsuccessfulDuration = duration;
            _unsuccessfulCalls++;
            _unsuccessfulDuration += duration;
            _unsuccessfulMin = _unsuccessfulMin > duration ? duration : _unsuccessfulMin;
            _unsuccessfulMax = _unsuccessfulMax < duration ? duration : _unsuccessfulMax;
         }
      }
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Call statistics pertaining to a certain function.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
    */
   final class Statistics extends Object {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Statistics</code> object.
       */
      private Statistics() {
         // empty
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      /**
       * Returns the number of successful calls executed up until now.
       *
       * @return
       *    the number of successful calls executed up until now.
       */
      public int getSuccessfulCalls() {
         return _successfulCalls;
      }

      /**
       * Returns the number of unsuccessful calls executed up until now.
       *
       * @return
       *    the number of unsuccessful calls executed up until now.
       */
      public int getUnsuccessfulCalls() {
         return _unsuccessfulCalls;
      }

      /**
       * Returns the start time of the most recent successful call.
       *
       * @return
       *    the start time of the most recent successful call.
       */
      public long getLastSuccessfulStart() {
         return _lastSuccessfulStart;
      }

      /**
       * Returns the start time of the most recent unsuccessful call.
       *
       * @return
       *    the start time of the most recent unsuccessful call.
       */
      public long getLastUnsuccessfulStart() {
         return _lastUnsuccessfulStart;
      }

      /**
       * Returns the duration of the most recent successful call.
       *
       * @return
       *    the duration of the most recent successful call.
       */
      public long getLastSuccessfulDuration() {
         return _lastSuccessfulDuration;
      }

      /**
       * Returns the duration of the most recent unsuccessful call.
       *
       * @return
       *    the duration of the most recent unsuccessful call.
       */
      public long getLastUnsuccessfulDuration() {
         return _unsuccessfulDuration;
      }

      /**
       * Returns the total duration of all successful calls up until now.
       *
       * @return
       *    the total duration of all successful calls up until now.
       */
      public long getSuccessfulDuration() {
         return _successfulDuration;
      }

      /**
       * Returns the total duration of all unsuccessful calls up until now.
       *
       * @return
       *    the total duration of all unsuccessful calls up until now.
       */
      public long getUnsuccessfulDuration() {
         return _unsuccessfulDuration;
      }

      /**
       * Returns the minimum time a successful call took.
       *
       * @return
       *    the minimum time a successful call took.
       */
      public long getSuccessfulMin() {
         return _successfulMin;
      }

      /**
       * Returns the minimum time an unsuccessful call took.
       *
       * @return
       *    the minimum time an unsuccessful call took.
       */
      public long getUnsuccessfulMin() {
         return _unsuccessfulMin;
      }

      /**
       * Returns the maximum time a successful call took.
       *
       * @return
       *    the maximum time a successful call took.
       */
      public long getSuccessfulMax() {
         return _successfulMax;
      }

      /**
       * Returns the maximum time an unsuccessful call took.
       *
       * @return
       *    the maximum time an unsuccessful call took.
       */
      public long getUnsuccessfulMax() {
         return _unsuccessfulMax;
      }
   }
}
