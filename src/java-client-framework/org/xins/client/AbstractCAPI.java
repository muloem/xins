/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Base class for client-side calling interface classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.131
 */
public abstract class AbstractCAPI
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
    * Creates a new <code>AbstractCAPI</code> object.
    *
    * @param caller
    *    the XINS service caller, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>caller == null</code>.
    */
   protected AbstractCAPI(XINSServiceCaller caller)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("caller", caller);

      // Set fields
      _caller = caller;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The XINS service caller to use. This field cannot be <code>null</code>.
    */
   private XINSServiceCaller _caller;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the XINS service caller to use.
    *
    * @return
    *    the XINS service caller to use, never <code>null</code>.
    */
   protected XINSServiceCaller getCaller() {
      return _caller;
   }

   /**
    * Returns the version of XINS used to build this API.
    *
    * @return
    *    the version as a {@link String}, cannot be <code>null</code>.
    */
   public abstract String getXINSVersion();
}
