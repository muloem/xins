/*
 * $Id$
 */
package org.xins.tests.server;

import java.io.File;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import org.xins.client.XINSCallRequest;
import org.xins.client.XINSCallResult;
import org.xins.client.XINSServiceCaller;
import org.xins.common.collections.PropertyReader;
import org.xins.common.http.HTTPMethod;
import org.xins.common.service.TargetDescriptor;
import org.xins.tests.server.servlet.HTTPServletHandler;

/**
 * Tests for class <code>IPFilter</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class MetaFunctionsTests extends TestCase {

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
      return new TestSuite(MetaFunctionsTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>IPFilterTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public MetaFunctionsTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   private HTTPServletHandler _httpServer;
   
   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      // Start the web server
      File xinsProps = new File(System.getProperty("user.dir"), "../xins-examples/xins.properties");
      System.setProperty("org.xins.server.config", xinsProps.getAbsolutePath());
      _httpServer = new HTTPServletHandler("../xins-examples/xins-project/build/webapps/allinone/allinone.war");
      System.err.println("Web server set up.");
   }

   public void testGetVersion() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_GetVersion", null, false, HTTPMethod.GET);
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      System.err.println("Calling server...");
      XINSCallResult result = caller.call(request);
      System.err.println("Server called");
      assertNull("The function returned a result code.", result.getErrorCode());
      assertNull("The function returned a data element.", result.getDataElement());
      PropertyReader parameters = result.getParameters();
      assertNotNull("The function did not returned any parameters.", parameters);
      assertNotNull("No java version specified.", parameters.get("java.version"));
      assertNotNull("No XINS version specified.", parameters.get("xins.version"));
      assertNotNull("No xmlenc version specified.", parameters.get("xmlenc.version"));
      assertEquals("Incorrect number of parameters.", 3, parameters.size());
   }
   
   /**
    * Stop the server.
    */
   protected void tearDown() {
      _httpServer.close();
   }
}
