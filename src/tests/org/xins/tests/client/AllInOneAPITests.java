/*
 * $Id$
 */
package org.xins.tests.client;

import java.io.File;
import java.io.IOException;

import java.text.ParseException;

import java.util.Iterator;

import javax.servlet.ServletException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.PropertyReader;
import org.xins.common.http.HTTPMethod;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.types.standard.Date;
import org.xins.common.types.standard.Timestamp;

import org.xins.client.DataElement;
import org.xins.client.XINSCallRequest;
import org.xins.client.XINSCallResult;
import org.xins.client.XINSServiceCaller;
import org.xins.client.UnsuccessfulXINSCallException;

import org.xins.tests.server.servlet.HTTPServletHandler;

import com.mycompany.allinone.capi.CAPI;
import com.mycompany.allinone.capi.DefinedTypesResult;
import com.mycompany.allinone.capi.SimpleTypesResult;
import com.mycompany.allinone.types.Salutation;
import com.mycompany.allinone.types.TextList;

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

   public void testSimpleTypes() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      CAPI allInOne = new CAPI(descriptor);
      SimpleTypesResult result = allInOne.callSimpleTypes((byte)8, null, 65, 88l, 
         "text", null, null, Date.fromStringForRequired("20041213"), Timestamp.fromStringForOptional("20041225153255"));
      assertNull(result.getOutputByte());
      assertEquals((short)-1, result.getOutputShort());
      assertEquals(16, result.getOutputInt());
      assertEquals(14l, result.getOutputLong());
      assertEquals("hello", result.getOutputText());
      assertNull(result.getOutputText2());
      assertNull(result.getOutputProperties());
      assertEquals(Date.fromStringForRequired("20040621"), result.getOutputDate());
      assertNull(result.getOutputTimestamp());
   }
   
   public void testMissingParam() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      CAPI allInOne = new CAPI(descriptor);
      try {
         SimpleTypesResult result = allInOne.callSimpleTypes((byte)8, null, 65, 88l, 
            null, null, null, Date.fromStringForRequired("20041213"), Timestamp.fromStringForOptional("20041225153222"));
         fail("The request is invalid, the function should throw an exception");
      } catch (UnsuccessfulXINSCallException exception) {
         assertEquals("_InvalidRequest", exception.getErrorCode());
         assertEquals(descriptor, exception.getTarget());
         assertNull(exception.getParameters());
         DataElement dataSection = exception.getDataElement();
         assertNotNull(dataSection);
         DataElement missingParam = (DataElement) dataSection.getChildren().next();
         assertEquals("missing-param", missingParam.getName());
         assertEquals("inputText", missingParam.get("param"));
         assertNull(missingParam.getChildren());
         assertNull(missingParam.getText());
      }
   }
   
   public void testDefinedTypes() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      CAPI allInOne = new CAPI(descriptor);
      TextList.Value textList = new TextList.Value();
      textList.add("hello");
      textList.add("world");
      DefinedTypesResult result = allInOne.callDefinedTypes("198.165.0.1", Salutation.LADY, (byte)28, textList);
      assertEquals("127.0.0.1", result.getOutputIP());
      assertEquals(Salutation.LADY, result.getOutputSalutation());
      assertEquals(Byte.decode("35"), result.getOutputAge());
      assertEquals(2, result.getOutputList().getSize());
      assertEquals(2, result.getOutputProperties().size());
   }
   
   public void testInvalidParams() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      CAPI allInOne = new CAPI(descriptor);
      TextList.Value textList = new TextList.Value();
      textList.add("Hello");
      textList.add("Test");
      try {
         DefinedTypesResult result = allInOne.callDefinedTypes("not an IP", Salutation.LADY, (byte)8, textList);
         fail("The request is invalid, the function should throw an exception");
      } catch (UnsuccessfulXINSCallException exception) {
         assertEquals("_InvalidRequest", exception.getErrorCode());
         assertEquals(descriptor, exception.getTarget());
         assertNull(exception.getParameters());
         DataElement dataSection = exception.getDataElement();
         assertNotNull(dataSection);
         Iterator invalidParams = dataSection.getChildren();
         DataElement invalidParam1 = (DataElement) invalidParams.next();
         assertEquals("invalid-value-for-type", invalidParam1.getName());
         assertEquals("inputIP", invalidParam1.get("param"));
         assertNull(invalidParam1.getChildren());
         assertNull(invalidParam1.getText());
         DataElement invalidParam2 = (DataElement) invalidParams.next();
         assertEquals("invalid-value-for-type", invalidParam2.getName());
         assertEquals("inputAge", invalidParam2.get("param"));
         assertNull(invalidParam2.getChildren());
         assertNull(invalidParam2.getText());
      }
   }
   
   public void testDataSection2() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      CAPI allInOne = new CAPI(descriptor);
      DataElement element = allInOne.callDataSection2("hello");
      Iterator destination = element.getChildren();
      assertTrue("No destination found.", destination.hasNext());
      DataElement destination1 = (DataElement) destination.next();
      assertEquals("Incorrect elements.", "packet", destination1.getName());
      assertNotNull("No destination specified.", destination1.get("destination"));
      Iterator products = destination1.getChildren();
      assertNotNull("No product specified.", products);
      DataElement product1 = (DataElement) products.next();
      assertEquals("Incorrect price for product1", "12", product1.get("price"));
      
      DataElement destination2 = (DataElement) destination.next();
      assertEquals("Incorrect elements.", "packet", destination2.getName());
      assertNotNull("No destination specified.", destination2.get("destination"));
      Iterator products2 = destination2.getChildren();
      assertNotNull("No product specified.", products2);
      DataElement product21 = (DataElement) products2.next();
      assertEquals("Incorrect price for product1", "12", product21.get("price"));
   }
   
   /**
    * Stop the server.
    */
   protected void tearDown() {
      _httpServer.close();
   }
}
