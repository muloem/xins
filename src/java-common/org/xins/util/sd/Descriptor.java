/*
 * $Id$
 */
package org.xins.util.sd;

/**
 * Service descriptor.
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
}
