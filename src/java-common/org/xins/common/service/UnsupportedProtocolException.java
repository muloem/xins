/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception that indicates that protocol specified in a
 * <code>TargetDescriptor</code> is not supported by a service caller.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.1.0
 *
 * @see TargetDescriptor
 * @see ServiceCaller
 */
public final class UnsupportedProtocolException
extends RuntimeException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Serial version UID. Used for serialization. The assigned value is for
    * compatibility with XINS 1.2.5.
    */
   private static final long serialVersionUID = 2847976540646154938L;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates the message for the constructor to pass up to the
    * superconstructor.
    *
    * @param target
    *    the {@link TargetDescriptor} that has an unsupported protocol, cannot
    *    be <code>null</code>.
    *
    * @return
    *    the created message, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>target == null</code>.
    */
   private static String createMessage(TargetDescriptor target)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("target", target);

      return "Unsupported protocol \""
           + target.getProtocol()
           + "\".";
   }

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>UnsupportedProtocolException</code> for the
    * specified target descriptor.
    *
    * @param target
    *    the {@link TargetDescriptor} that has an unsupported protocol, cannot
    *    be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>target == null</code>.
    */
   public UnsupportedProtocolException(TargetDescriptor target)
   throws IllegalArgumentException {

      super(createMessage(target));

      // Store
      _target = target;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The target descriptor that has an unsupported protocol. Cannot be
    * <code>null</code>.
    */
   private final TargetDescriptor _target;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the target descriptor that has an unsupported protocol.
    *
    * @return
    *    the {@link TargetDescriptor}, never <code>null</code>.
    */
   public TargetDescriptor getTargetDescriptor() {
      return _target;
   }
}
