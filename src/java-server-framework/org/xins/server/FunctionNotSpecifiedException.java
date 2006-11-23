/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

/**
 * Exception that indicates that an incoming request does not specify the
 * function to execute.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public final class FunctionNotSpecifiedException
extends Exception {

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>FunctionNotSpecifiedException</code>.
    */
   public FunctionNotSpecifiedException() {
      super("Function not specified in incoming request.");
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
