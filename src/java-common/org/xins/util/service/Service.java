/*
 * $Id$
 */
package org.xins.util.service;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Service accessor. This abstract class must be subclasses by specific kinds
 * of service accessors, for example for HTTP, FTP, JDBC, etc.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.115
 */
public abstract class Service extends Object {

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
    * Constructs a new <code>Service</code> object.
    *
    * @param descriptor
    *    the descriptor of the service, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>descriptor == null</code>.
    */
   public Service(Descriptor descriptor)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("descriptor", descriptor);

      // Set fields
      _descriptor = descriptor;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The descriptor for this service. Cannot be <code>null</code>.
    */
   private final Descriptor _descriptor;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the descriptor.
    *
    * @return
    *    the descriptor for this service, never <code>null</code>.
    */
   public Descriptor getDescriptor() {
      return _descriptor;
   }
}
