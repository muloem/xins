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
    * @throws IllegalArgumentException
    *    if <code>api == null || name == null</code>.
    */
   protected Function(API api, String name)
   throws IllegalArgumentException {

      // Check argument
      MandatoryArgumentChecker.check("api", api, "name", name);

      _api  = api;
      _name = name;

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
    * The total duration of all successful calls up until now.
    */
   private int _successfulDuration;

   /**
    * The total duration of all unsuccessful calls up until now.
    */
   private int _unsuccessfulDuration;


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
   public final void performedCall(long start, long duration, boolean success, String code) {
      if (success) {
         synchronized (_successfulCallLock) {
            _successfulCalls++;
            _successfulDuration += duration;
         }
      } else {
         synchronized (_unsuccessfulCallLock) {
            _unsuccessfulCalls++;
            _unsuccessfulDuration += duration;
         }
      }
   }
}
