/*
 * $Id$
 */
package org.xins.server;

import java.io.IOException;
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
   int _successfulCalls;

   /**
    * The number of unsuccessful calls executed up until now.
    */
   int _unsuccessfulCalls;

   /**
    * The total duration of all successful calls up until now.
    */
   long _successfulDuration;

   /**
    * The total duration of all unsuccessful calls up until now.
    */
   long _unsuccessfulDuration;

   /**
    * The minimum time a successful call took.
    */
   long _successfulMin = Long.MAX_VALUE;

   /**
    * The minimum time an unsuccessful call took.
    */
   long _unsuccessfulMin = Long.MAX_VALUE;

   /**
    * The maximum time a successful call took.
    */
   long _successfulMax;

   /**
    * The maximum time an unsuccessful call took.
    */
   long _unsuccessfulMax;


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
            _successfulCalls++;
            _successfulDuration += duration;
            _successfulMin = _successfulMin > duration ? duration : _successfulMin;
            _successfulMax = _successfulMax < duration ? duration : _successfulMax;
         }
      } else {
         synchronized (_unsuccessfulCallLock) {
            _unsuccessfulCalls++;
            _unsuccessfulDuration += duration;
            _unsuccessfulMin = _unsuccessfulMin > duration ? duration : _unsuccessfulMin;
            _unsuccessfulMax = _unsuccessfulMax < duration ? duration : _unsuccessfulMax;
         }
      }
   }
}
