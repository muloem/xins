/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
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
import org.xins.common.http.StatusCodeHTTPCallException;
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
    * Constructs a new <code>AllInOneAPITests</code> test suite with
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
      File xinsProps = new File(System.getProperty("user.dir"), "src/tests/xins.properties");
      System.setProperty("org.xins.server.config", xinsProps.getAbsolutePath());
      String warLocation = "src/tests/build/webapps/allinone/allinone.war".replace('/', File.separatorChar);
      File warFile = new File(System.getProperty("user.dir"), warLocation);

      // Start the web server
      //System.out.println("Web server set up.");
      _httpServer = new HTTPServletHandler(warFile);
   }

   /**
    * Tests CAPI and pre-defined.
    */
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

   /**
    * Tests a function called with some missing parameters.
    */
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

   /**
    * Tests CAPI and defined types.
    */
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

   /**
    * Tests a function called with some invalid parameters.
    */
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

   /**
    * Tests a function that should returned a defined result code.
    */
   public void testResultCode() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      CAPI allInOne = new CAPI(descriptor);
      String result1 = allInOne.callResultCode("hello");
      assertEquals("The first call to ResultCode returned an incorrect result", "hello added.", result1);
      try {
         String result2 = allInOne.callResultCode("hello");
         fail("The second call with the same parameter should return an AlreadySet error code.");
      } catch (UnsuccessfulXINSCallException exception) {
         assertEquals("AlreadySet", exception.getErrorCode());
         assertEquals(descriptor, exception.getTarget());
         assertNotNull(exception.getParameters());
         assertEquals("Incorrect value for the count parameter.", "1", exception.getParameter("count"));
         assertNull(exception.getDataElement());
      }
   }

   /**
    * Tests a function that writes messages to the Logdoc.
    */
   public void testLogdoc() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      CAPI allInOne = new CAPI(descriptor);
      // This method write some text using the logdoc
      // This test doesn't check that the data written in the logs are as expected
      try {
         allInOne.callLogdoc("hello");
         fail("The logdoc call should return an InvalidNumber error code.");
      } catch (UnsuccessfulXINSCallException exception) {
         assertEquals("InvalidNumber", exception.getErrorCode());
         assertEquals(descriptor, exception.getTarget());
         assertNull(exception.getParameters());
         assertNull(exception.getDataElement());
      }
      allInOne.callLogdoc("12000");
   }

   /**
    * Tests a function that returns a data section containing elements with
    * PCDATA.
    */
   public void testDataSection() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      CAPI allInOne = new CAPI(descriptor);
      DataElement element = allInOne.callDataSection("Doe");
      Iterator users = element.getChildren();
      assertTrue("No users found.", users.hasNext());
      DataElement su = (DataElement) users.next();
      assertEquals("Incorrect elements.", "user", su.getName());
      assertEquals("Incorrect name for su.", "superuser", su.get("name"));
      assertEquals("Incorrect address.", "12 Madison Avenue", su.get("address"));
      assertEquals("Incorrect PCDATA.", "This user has the root authorisation.", su.getText());
      assertNull(su.getChildren());
      DataElement doe = (DataElement) users.next();
      assertEquals("Incorrect elements.", "user", doe.getName());
      assertEquals("Incorrect name for Doe.", "Doe", doe.get("name"));
      assertEquals("Incorrect address.", "Unknown", doe.get("address"));
      assertNull(doe.getText());
      assertNull(doe.getChildren());
   }

   /**
    * Tests a function that returns a data section with elements that contain
    * other elements.
    */
   public void testDataSection2() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/", 2000);
      CAPI allInOne = new CAPI(descriptor);
      DataElement element = allInOne.callDataSection2("hello");
      Iterator packets = element.getChildren();
      assertTrue("No destination found.", packets.hasNext());
      DataElement packet1 = (DataElement) packets.next();
      assertEquals("Incorrect elements.", "packet", packet1.getName());
      assertNotNull("No destination specified.", packet1.get("destination"));
      Iterator products = packet1.getChildren();
      assertNotNull("No product specified.", products);
      DataElement product1 = (DataElement) products.next();
      assertEquals("Incorrect price for product1", "12", product1.get("price"));

      DataElement packet2 = (DataElement) packets.next();
      assertEquals("Incorrect elements.", "packet", packet2.getName());
      assertNotNull("No destination specified.", packet2.get("destination"));
      Iterator products2 = packet2.getChildren();
      assertNotNull("No product specified.", products2);
      DataElement product21 = (DataElement) products2.next();
      assertEquals("Incorrect price for product1", "12", product21.get("price"));
   }

   /**
    * Tests the getXINSVersion() CAPI method.
    */
   public void testCAPIVersion() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      CAPI allInOne = new CAPI(descriptor);
      assertNotNull("No XINS version specified.", allInOne.getXINSVersion());
      assertTrue("The version does not starts with '1.'", allInOne.getXINSVersion().startsWith("1."));
   }

   /**
    * Tests a function that does not exists
    */
   public void testUnknownFunction() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("Unknown", null);
      TargetDescriptor descriptor = new TargetDescriptor("http://localhost:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      try {
         XINSCallResult result = caller.call(request);
      } catch (StatusCodeHTTPCallException exception) {
         assertEquals("Incorrect status code found.", 404, exception.getStatusCode());
      }
   }

   /**
    * Stop the server.
    */
   protected void tearDown() {
      _httpServer.close();
   }
}
