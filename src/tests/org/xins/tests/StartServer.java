/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.servlet.container.HTTPServletHandler;

/**
 * Starts the web server.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class StartServer extends TestCase {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(StartServer.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>StartServer</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public StartServer(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testStartServer() throws Exception {
      
      File xinsProps = new File(System.getProperty("user.dir"), "src/tests/xins.properties".replace('/', File.separatorChar));
      System.setProperty("org.xins.server.config", xinsProps.getAbsolutePath());
      String warLocation = "src/tests/build/webapps/allinone/allinone.war".replace('/', File.separatorChar);
      File warFile = new File(System.getProperty("user.dir"), warLocation);

      // Start the web server
      System.out.println("Starting web server.");
      AllTests.HTTP_SERVER = new HTTPServletHandler(warFile);
      System.out.println("Web server started.");
   }
}
