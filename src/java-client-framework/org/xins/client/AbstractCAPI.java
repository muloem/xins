/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.service.Descriptor;

/**
 * Base class for client-side calling interface classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.131
 */
public abstract class AbstractCAPI {

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
    * Creates a new <code>AbstractCAPI</code> object, using the specified
    * <code>Descriptor</code>.
    *
    * @param descriptor
    *    the descriptor for the service(s), cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    */
   protected AbstractCAPI(Descriptor descriptor)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("descriptor", descriptor);

      // Store service caller
      _caller = new XINSServiceCaller(descriptor);
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
}
