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

   public void startResponse(boolean success, String code)
   throws InvalidResponseException {
      // empty
   }

   public void param(String name, String value)
   throws InvalidResponseException {
      Map parameters = getParameters();
      Object o = parameters.get(name);
      if (o != null) {
         throw new InvalidResponseException("Duplicate parameter named \"" + name + "\".");
      }
      parameters.put(name, value);
   }

   public void endResponse()
   throws InvalidResponseException {
      // TODO: Cleanup parameter map, if any
      // TODO: Cleanup attribute map, if any
   }

   public void cancelResponse() {
      // TODO: Cleanup parameter map, if any
      // TODO: Cleanup attribute map, if any
   }
}
