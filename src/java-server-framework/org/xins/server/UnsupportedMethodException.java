/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import org.xins.logdoc.ExceptionUtils;

/**
 * Exception that indicates that an incoming request is considered invalid
 * because the HTTP method is unsupported for function invocations.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 */
public final class UnsupportedMethodException
extends InvalidRequestException {

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>UnsupportedMethodException</code>.
    *
    * @param detail
    *    description of the issue, can be <code>null</code>.
    */
   public UnsupportedMethodException(String detail) {
      super(detail);
   }
}
