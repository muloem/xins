/*
 * $Id$
 */
package org.xins.client;

import org.xins.common.service.TargetDescriptor;

/**
 * Exception that indicates that a connection could not be established.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.196
 */
public abstract class ConnectionException extends CallException {

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
    * Constructs a new <code>ConnectionException</code>.
    *
    * @deprecated
    *    Deprecated since XINS 0.201. Use
    *    {@link #ConnectionException(CallRequest,TargetDescriptor)} instead.
    */
   ConnectionException() {
      super(null, null);
   }

   /**
    * Constructs a new <code>ConnectionException</code>, for the specified
    * <code>CallRequest</code> and <code>TargetDescriptor</code>.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null || target == null</code>.
    */
   ConnectionException(CallRequest      request,
                       TargetDescriptor target)
   throws IllegalArgumentException {

      // Call superconstructor first
      super(request, target, null, null);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
