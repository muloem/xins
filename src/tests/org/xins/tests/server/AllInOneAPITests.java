/*
 * $Id$
 */
package org.xins.tests.server;

import java.io.File;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.PropertyReader;
import org.xins.common.http.HTTPMethod;
import org.xins.common.service.TargetDescriptor;

import org.xins.client.DataElement;
import org.xins.client.XINSCallRequest;
import org.xins.client.XINSCallResult;
import org.xins.client.XINSServiceCaller;

import org.xins.tests.server.servlet.HTTPServletHandler;

import com.mycompany.allinone.capi.CAPI;

/**
 * Tests the allinone functions using the generated CAPI.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class AllInOneAPITests extends TestCase {
   
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
      return new TestSuite(AllInOneAPITests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>MetaFunctionsTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public AllInOneAPITests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The HTTP server used to handle the requests.
    */
   private HTTPServletHandler _httpServer;
   

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Starts the HTTP server with the correct parameters.
    */
   protected void setUp() throws ServletException, IOException {
      File xinsProps = new File(System.getProperty("user.dir"), "../xins-examples/xins.properties");
      System.setProperty("org.xins.server.config", xinsProps.getAbsolutePath());
      String warLocation = "../xins-examples/xins-project/build/webapps/allinone/allinone.war".replace('/', File.separatorChar);
      File warFile = new File(System.getProperty("user.dir"), warLocation);
      
      // Start the web server
      //System.out.println("Web server set up.");
      _httpServer = new HTTPServletHandler(warFile);
   }

   public void testDataSection2() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      CAPI allInOne = new CAPI(descriptor);
      DataElement element = allInOne.callDataSection2("hello");
      Iterator destination = element.getChildren();
      assertTrue("No destination found.", destination.hasNext());
      DataElement nextDestination = (DataElement) destination.next();
      assertEquals("Incorrect elements.", "packet", nextDestination.getName());
      assertNotNull("No destination specified.", nextDestination.get("destination"));
   }
   
   /**
    * Stop the server.
    */
   protected void tearDown() {
      _httpServer.close();
   }
}
