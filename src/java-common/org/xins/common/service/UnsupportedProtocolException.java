/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.service;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Exception that indicates that protocol specified in a
 * <code>TargetDescriptor</code> is not supported by a service caller.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
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
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = UnsupportedProtocolException.class.getName();


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
   private static final String createMessage(TargetDescriptor target)
   throws IllegalArgumentException {

      // TRACE: Enter constructor
      Log.log_1000(CLASSNAME, null);

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

      // TRACE: Leave constructor
      Log.log_1002(CLASSNAME, null);
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
