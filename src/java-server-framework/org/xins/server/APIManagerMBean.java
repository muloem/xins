/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.util.Properties;
import org.xins.common.collections.PropertyReader;

/**
 * Management bean for the API.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.5.0
 */
public interface APIManagerMBean {

   /**
    * Gets the version of the API.
    *
    * @return
    *    the version of the API running.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   String getAPIVersion() throws IOException;

   /**
    * Gets the version of XINS which is running this API.
    *
    * @return
    *    the version of XINS running the API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   String getXINSVersion() throws IOException;

   /**
    * Gets the name of the API.
    *
    * @return
    *    the name the API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   String getAPIName() throws IOException;

   /**
    * Gets the bootstrap properties.
    *
    * @return
    *    the bootstrap properties for this API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   PropertyReader getBootstrapProperties() throws IOException;

   /**
    * Gets the runtime properties.
    *
    * @return
    *    the runtime properties for this API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   PropertyReader getRuntimeProperties() throws IOException;

   /**
    * Gets the time at which the API was started.
    *
    * @return
    *    the time at which the API was started in the form YYYYMMDDThhmmssSSS+TZ.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   String getStartupTime() throws IOException;
    
   /**
    * Executes the _NoOp meta function.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   void noOp() throws IOException, NoSuchFunctionException, AccessDeniedException;

   /**
    * Reloads the runtime properties if the file has changed.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   void reloadProperties() throws IOException, NoSuchFunctionException, AccessDeniedException;
}
