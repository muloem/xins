/*
 * $Id$
 */
package org.xins.tests.server;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.client.DataElement;

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
   protected void setUp() {
      File xinsProps = new File(System.getProperty("user.dir"), "../xins-examples/xins.properties");
      System.setProperty("org.xins.server.config", xinsProps.getAbsolutePath());
      String warLocation = "../xins-examples/xins-project/build/webapps/allinone/allinone.war".replace('/', File.separatorChar);
      File warFile = new File(System.getProperty("user.dir"), warLocation);
      
      // Start the web server
      //System.out.println("Web server set up.");
      _httpServer = new HTTPServletHandler(warFile);
   }

   public void testGetVersion() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_GetVersion", null);
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      XINSCallResult result = caller.call(request);
      assertNull("The function returned a result code.", result.getErrorCode());
      assertNull("The function returned a data element.", result.getDataElement());
      PropertyReader parameters = result.getParameters();
      assertNotNull("The function did not returned any parameters.", parameters);
      assertNotNull("No java version specified.", parameters.get("java.version"));
      assertNotNull("No XINS version specified.", parameters.get("xins.version"));
      assertNotNull("No xmlenc version specified.", parameters.get("xmlenc.version"));
      assertEquals("Incorrect number of parameters.", 3, parameters.size());
   }
   
   public void testGetStatistics() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_GetStatistics", null);
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      XINSCallResult result = caller.call(request);
      assertNull("The function returned a result code.", result.getErrorCode());
      assertNotNull("The function returned a data element.", result.getDataElement());
      PropertyReader parameters = result.getParameters();
      assertNotNull("The function did not returned any parameters.", parameters);
      assertNotNull("No startup date specified.", parameters.get("startup"));
      try {
         SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS");
         formatter.parse(parameters.get("startup"));
      } catch (ParseException parseException) {
         fail("Incorrect date format for startup time.");
      }
      assertNotNull("No (now) date specified.", parameters.get("now"));
      try {
         SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS");
         Date now = formatter.parse(parameters.get("now"));
         assertTrue("Start-up time is after 'now' time.", now.after(formatter.parse(parameters.get("startup"))));
      } catch (ParseException parseException) {
         fail("Incorrect date format for 'now'.");
      }
      DataElement data = result.getDataElement();
      assertFalse(data.getAttributes().hasNext());
      Iterator children = data.getChildren();
      
      DataElement heap = (DataElement) children.next();
      assertNotNull("No total memory provided.", heap.get("total"));
      assertNotNull("No used memory provided.", heap.get("used"));
      assertNotNull("No free memory provided.", heap.get("free"));
      assertNotNull("No max memory provided.", heap.get("max"));
      try {
         Long.parseLong(heap.get("total"));
         Long.parseLong(heap.get("used"));
         Long.parseLong(heap.get("free"));
         Long.parseLong(heap.get("max"));
      } catch (Exception exception) {
         fail("Incorrect value while parsing a memory size.");
      }
      
      while (children.hasNext()) {
         DataElement nextFunction = (DataElement) children.next();
         assertEquals("Object other than a fnuction has been found.", "function", nextFunction.getName());
         assertNotNull("The functino does not have a name", nextFunction.get("name"));
         // XXX also test the children.
      }
   }
   
   /**
    * Stop the server.
    */
   protected void tearDown() {
      _httpServer.close();
   }
}
