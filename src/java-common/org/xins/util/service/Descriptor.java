/*
 * $Id$
 */
package org.xins.util.service;

import java.util.Iterator;

/**
 * Descriptor for a service or group of services.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.105
 */
public abstract class Descriptor extends Object {

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
    * Constructs a new <code>Descriptor</code>.
    */
   Descriptor() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Checks if this service descriptor denotes a group.
    *
    * @return
    *    <code>true</code> if this descriptor denotes a group,
    *    <code>false</code> otherwise.
    */
   public abstract boolean isGroup();

   /**
    * Iterates over all leaves, the service descriptors.
    *
    * <p>The returned {@link Iterator} will not support
    * {@link Iterator#remove()}. The iterator will only return
    * {@link TargetDescriptor} instances, no instances of other classes and
    * no <code>null</code> values.
    *
    * <p>Also, this iterator is guaranteed to return <em>at least</em> one
    * {@link TargetDescriptor} instance.
    *
    * @return
    *    iterator over the service descriptors on any level in this
    *    descriptor, in the correct order, never <code>null</code>.
    */
   public abstract Iterator iterateServices();
}
