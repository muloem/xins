/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.client;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.service.CallConfig;
import org.xins.common.http.HTTPCallConfig;
import org.xins.common.http.HTTPMethod;

/**
 * Call configuration for the XINS service caller. The HTTP method and the
 * can be configured. By default it is set to <em>POST</em>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.1.0
 */
public final class XINSCallConfig extends CallConfig {

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
    * Constructs a new <code>XINSCallConfig</code> object.
    */
   public XINSCallConfig() {

      // Construct an underlying HTTPCallConfig
      _httpCallConfig = new HTTPCallConfig();

      // Configure the User-Agent header
      String userAgent = "XINS/Java Client Framework " + Library.getVersion();
      _httpCallConfig.setUserAgent(userAgent);

      // NOTE: HTTPCallConfig already defaults to HTTP POST
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The underlying HTTP call config. Cannot be <code>null</code>.
    */
   private HTTPCallConfig _httpCallConfig;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   // TODO: Override describe()

   /**
    * Returns an <code>HTTPCallConfig</code> object that corresponds with this
    * XINS call configuration object.
    *
    * @return
    *    an {@link HTTPCallConfig} object, never <code>null</code>.
    */
   HTTPCallConfig getHTTPCallConfig() {
      return _httpCallConfig;
   }

   /**
    * Returns the HTTP method associated with this configuration.
    *
    * @return
    *    the HTTP method, never <code>null</code>.
    */
   public HTTPMethod getHTTPMethod() {
      return _httpCallConfig.getMethod();
   }

   /**
    * Sets the HTTP method associated with this configuration.
    *
    * @param method
    *    the HTTP method to be associated with this configuration, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>method == null</code>.
    */
   public void setHTTPMethod(HTTPMethod method)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("method", method);

      // Store the setting in the HTTP call configuration
      _httpCallConfig.setMethod(method);
   }
}
