/*
 * $Id$
 */
package org.xins.server;

import java.io.IOException;
import java.util.Properties;

/**
 * Base class for API implementation classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public abstract class API
extends Object
implements DefaultReturnCodes {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Checks if the specified value is <code>null</code> or an empty string.
    * Only if it is then <code>true</code> is returned.
    *
    * @param value
    *    the value to check.
    *
    * @return
    *    <code>true</code> if and only if <code>value != null &amp;&amp;
    *    value.length() != 0</code>.
    */
   protected final static boolean isMissing(String value) {
      return value == null || value.length() == 0;
   }


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>API</code> object.
    */
   protected API() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Initialises this API.
    *
    * <p />The implementation of this method in class {@link API} is empty.
    *
    * @param properties
    *    the properties, can be <code>null</code>.
    *
    * @throws Throwable
    *    if the initialisation fails.
    */
   public void init(Properties properties)
   throws Throwable {
      // empty
   }

   /**
    * Handles a call to this API.
    *
    * @param context
    *    the context for this call, never <code>null</code>.
    *
    * @throws Throwable
    *    if anything goes wrong.
    */
   protected abstract void handleCall(CallContext context)
   throws Throwable;
}
