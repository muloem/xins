/*
 * $Id$
 */
package org.xins.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Response validator that just performs some common checks. The following
 * checks are performed:
 *
 * <ul>
 *    <li>No duplicate parameter names
 *    <li>No duplicate attribute names
 * </ul>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.50
 */
public class BasicResponseValidator
extends Object
implements ResponseValidator {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Singleton instance.
    */
   public static final BasicResponseValidator SINGLETON = new BasicResponseValidator();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>BasicResponseValidator</code>.
    */
   protected BasicResponseValidator() {
      _threadLocals = new ThreadLocal();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Thread-local variables. Per thread this {@link ThreadLocal} contains a
    * {@link Map}<code>[2]</code>, if it is already initialized. If so, then
    * the first element is the parameter {@link Map} and the second is the
    * attribute {@link Map}. Both may initially be <code>null</code>.
    */
   private final ThreadLocal _threadLocals;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the 2-size <code>Map[]</code> array for this thread. If there is no
    * array yet, then it is created and stored in {@link #_threadLocals}.
    *
    * @return
    *    the {@link Map}<code>[]</code> array for this thread, never
    *    <code>null</code>.
    */
   private final Map[] getThreadLocals() {
      Object o = _threadLocals.get();
      Map[] arr;
      if (o == null) {
         arr = new Map[2];
         _threadLocals.set(arr);
      } else {
         arr = (Map[]) o;
      }
      return arr;
   }

   /**
    * Cleans up the current response.
    */
   private final void reset() {
      Object o = _threadLocals.get();
      if (o != null) {
         Map[] arr = (Map[]) o;

         // Clean the parameter map, if any
         o = arr[0];
         if (o != null) {
            Map map = (Map) o;
            map.clear();
         }

         // Clean the attribute map, if any
         o = arr[1];
         if (o != null) {
            Map map = (Map) o;
            map.clear();
         }
      }
   }

   /**
    * Gets the parameter <code>Map</code> for the current response. If there
    * is none, then one will be created and stored.
    *
    * @return
    *    the parameter {@link Map}, never <code>null</code>.
    */
   protected final Map getParameters() {

      // Get the 2-size Map array
      Map[] arr = getThreadLocals();

      // Get the parameter Map
      Object o = arr[0];
      Map parameters;
      if (o == null) {
         parameters = new HashMap();
         arr[0] = parameters;
      } else {
         parameters = (Map) arr[0];
      }

      return parameters;
   }

   /**
    * Fails with an <code>InvalidResponseException</code> after cleaning up.
    * Subclasses should use this method if they find that the response is
    * invalid.
    *
    * @param message
    *    the message, can be <code>null</code>.
    *
    * @throws InvalidResponseException
    *    always thrown, right after cleanup is performed.
    */
   protected final void fail(String message)
   throws InvalidResponseException {
      reset();
      throw new InvalidResponseException(message);
   }

   public final void startResponse(boolean success, String code)
   throws InvalidResponseException {

      // Reset in case endResponse() or cancelResponse() were not called
      reset();

      boolean succeeded = false;
      try {
         startResponseImpl(success, code);
         succeeded = true;
      } finally {
         // If an exception is thrown, then reset, just in case the subclass
         // threw something other than an InvalidResponseException
         if (succeeded == false) {
            reset();
         }
      }
   }

   protected void startResponseImpl(boolean success, String code)
   throws InvalidResponseException {
      // empty
   }

   public final void param(String name, String value)
   throws InvalidResponseException {
      Map parameters = getParameters();
      Object o = parameters.get(name);
      if (o != null) {
         fail("Duplicate parameter named \"" + name + "\".");
      }
      boolean succeeded = false;
      try {
         paramImpl(name, value);
         succeeded = true;
      } finally {
         // If an exception is thrown, then reset, just in case the subclass
         // threw something other than an InvalidResponseException
         if (succeeded == false) {
            reset();
         }
      }
      parameters.put(name, value);
   }

   protected void paramImpl(String name, String value)
   throws InvalidResponseException {
      // empty
   }

   public void startTag(String name)
   throws InvalidResponseException {
      // empty
   }

   public void attribute(String name, String value)
   throws InvalidResponseException {
      // empty
   }

   public void pcdata(String text)
   throws InvalidResponseException {
      // empty
   }

   public void endTag()
   throws InvalidResponseException {
      // empty
   }

   public final void endResponse()
   throws InvalidResponseException {
      try {
         endResponseImpl();
      } finally {
         reset();
      }
   }

   protected void endResponseImpl()
   throws InvalidResponseException {
      // empty
   }

   public final void cancelResponse() {
      try {
         cancelResponseImpl();
      } finally {
         reset();
      }
   }

   protected void cancelResponseImpl() {
      // empty
   }
}
