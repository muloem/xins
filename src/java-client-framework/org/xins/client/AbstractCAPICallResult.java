/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Base class for CAPI call result classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.209
 */
public abstract class AbstractCAPICallResult
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
    * Creates a new <code>AbstractCAPICallResult</code> object, based on the
    * specified <code>XINSCallResult</code>.
    *
    * @param result
    *    the lower-level {@link XINSCallResult}, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>result == null</code>.
    */
   protected AbstractCAPICallResult(XINSCallResult result)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("result", result);

      _result = result;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The XINS call result. This field cannot be <code>null</code>.
    */
   private XINSCallResult _result;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the underlying XINS call result.
    *
    * @return
    *    the underlying {@link XINSCallResult} object, never
    *    <code>null</code>.
    */
   XINSCallResult getXINSCallResult() {
      return _result;
   }
}
