/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.http;

import org.xins.common.service.CallConfig;

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

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HTTPCallConfig</code> object.
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

   /**
    * The HTTP user agent. This field can be <code>null</code>.
    */
   private String _userAgent;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   // TODO: Override describe()

   /**
    * Returns the HTTP method associated with this configuration.
    *
    * @return
    *    the HTTP method, never <code>null</code>.
    */
   public HTTPMethod getMethod() {
      synchronized (getLock()) {
         return _method;
      }
   }

   /**
    * Sets the HTTP method associated with this configuration.
    *
    * @param method
    *    the HTTP method to be associated with this configuration, can be
    *    <code>null</code>.
    */
   public void setMethod(HTTPMethod method) {
      synchronized (getLock()) {
         _method = method;
      }
   }

   /**
    * Sets the user agent associated with the HTTP call.
    *
    * @param agent
    *    the HTTP user agent, or <code>null</code> if no user-agent header
    *    should be sent.
    *
    * @since XINS 1.3.0
    */
   public void setUserAgent(String agent) {
      synchronized (getLock()) {
         _userAgent = agent;
      }
   }

   /**
    * Returns the HTTP user agent associated with the HTTP call.
    *
    * @return
    *    the HTTP user agent or <code>null</code> no user agent has been
    *    specified.
    *
    * @since XINS 1.3.0
    */
   public String getUserAgent() {
      synchronized (getLock()) {
         return _userAgent;
      }
   }
}
