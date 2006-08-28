/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.server;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;
import org.xins.common.collections.PropertyReader;

/**
 * Management bean for the API.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
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
   CompositeDataSupport getBootstrapProperties() throws IOException;

   /**
    * Gets the runtime properties.
    *
    * @return
    *    the runtime properties for this API.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   CompositeDataSupport getRuntimeProperties() throws IOException;

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
    * Gets the list of the API functions.
    *
    * @return
    *    the list of the API function names.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public String[] getFunctionNames() throws IOException;

   /**
    * Gets the statistics of the functions.
    *
    * @return
    *    the statistics of the functions.
    *
    * @throws IOException
    *    if the connection to the MBean fails.
    */
   public TabularDataSupport getStatistics() throws IOException;

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
