/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import org.xins.common.Utils;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;

/**
 * Management bean for the API.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.5.0
 */
public final class APIManager implements APIManagerMBean {

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   APIManager(API api) {
      _api = api;
      try {
         _ip = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException uhex) {
         Log.log_3250(uhex);
         _ip = "127.0.0.1";
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The API, never <code>null</code>
    */
   private final API _api;

   /**
    * The IP address runing this class, never <code>null</code>.
    */
   private String _ip;

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Gets the version of the API.
    *
    * @return
    *    the version of the API running.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public String getAPIVersion() throws IOException {
      return getBootstrapProperties().get(API.API_VERSION_PROPERTY);
   }

   /**
    * Gets the version of XINS which is running this API.
    *
    * @return
    *    the version of XINS running the API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public String getXINSVersion() throws IOException {
      return Library.getVersion();
   }

   /**
    * Gets the name of the API.
    *
    * @return
    *    the name the API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public String getAPIName() throws IOException {
      return _api.getName();
   }

   /**
    * Gets the bootstrap properties.
    *
    * @return
    *    the bootstrap properties for this API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public PropertyReader getBootstrapProperties() throws IOException {
      return _api.getBootstrapProperties();
   }

   /**
    * Gets the runtime properties.
    *
    * @return
    *    the runtime properties for this API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public PropertyReader getRuntimeProperties() throws IOException {
      return _api.getRuntimeProperties();
   }

   /**
    * Gets the time at which the API was started.
    *
    * @return
    *    the time at which the API was started in the form YYYYMMDDThhmmssSSS+TZ.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public String getStartupTime() throws IOException {
      return _api.toDateString(_api.getStartupTimestamp());
   }
    
   /**
    * Executes the _NoOp meta function.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public void noOp() throws IOException, NoSuchFunctionException, AccessDeniedException {
      FunctionRequest noOpRequest = new FunctionRequest("_NoOp",
            PropertyReaderUtils.EMPTY_PROPERTY_READER, null);
      _api.handleCall(System.currentTimeMillis(), noOpRequest, _ip);
   }

   /**
    * Reloads the runtime properties if the file has changed.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public void reloadProperties() throws IOException, NoSuchFunctionException, AccessDeniedException {
      FunctionRequest reloadPropertiesRequest = new FunctionRequest("_ReloadProperties",
            PropertyReaderUtils.EMPTY_PROPERTY_READER, null);
      _api.handleCall(System.currentTimeMillis(), reloadPropertiesRequest, _ip);
   }
}
