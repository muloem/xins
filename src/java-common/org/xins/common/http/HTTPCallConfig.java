/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

import org.xins.common.service.CallConfig;

import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Call configuration for the HTTP service caller.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public final class HTTPCallConfig extends CallConfig {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Fully-qualified name of this class.
    */
   private static final String CLASSNAME = HTTPCallConfig.class.getName();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HTTPCallConfig</code> objcet.
    */
   public HTTPCallConfig() {
      _method = HTTPMethod.POST;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The HTTP method to use. This field cannot be <code>null</code>.
    */
   private HTTPMethod _method;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the HTTP method associated with this configuration.
    *
    * @return
    *    the HTTP method, never <code>null</code>.
    */
   public HTTPMethod getMethod() {
      HTTPMethod method;
      _doorman.enterAsReader();
      method = _method;
      _doorman.leaveAsReader();
      return method;
   }
}
