/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import com.mycompany.allinone.capi.CAPI;
import com.mycompany.allinone.capi.DefinedTypesResult;
import com.mycompany.allinone.capi.SimpleTypesResult;
import com.mycompany.allinone.types.Salutation;
import com.mycompany.allinone.types.TextList;

import java.io.File;
import java.io.IOException;

import java.text.ParseException;

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.DataElement;
import org.xins.client.XINSCallRequest;
import org.xins.client.XINSCallResult;
import org.xins.client.XINSServiceCaller;
import org.xins.client.UnsuccessfulXINSCallException;

import org.xins.common.collections.PropertyReader;

import org.xins.common.http.HTTPMethod;
import org.xins.common.http.StatusCodeHTTPCallException;

import org.xins.common.service.TargetDescriptor;

import org.xins.common.types.standard.Date;
import org.xins.common.types.standard.Timestamp;

import org.xins.common.xml.Element;
import org.xins.common.xml.ElementBuilder;

import org.xins.tests.server.servlet.HTTPServletHandler;

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

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Tests CAPI and pre-defined.
    */
   public void testSimpleTypes() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      CAPI allInOne = new CAPI(descriptor);
      SimpleTypesResult result = allInOne.callSimpleTypes((byte)8, null, 65, 88l, 32.5f, new Double(37.2),
         "text", null, null, Date.fromStringForRequired("20041213"), Timestamp.fromStringForOptional("20041225153255"), new byte[]{25,88,66});
      assertNull(result.getOutputByte());
      assertEquals((short)-1, result.getOutputShort());
      assertEquals(16, result.getOutputInt());
      assertEquals(14l, result.getOutputLong());
      assertEquals("hello", result.getOutputText());
      assertNull(result.getOutputText2());
      assertNull(result.getOutputProperties());
      assertEquals(Date.fromStringForRequired("20040621"), result.getOutputDate());
      assertNull(result.getOutputTimestamp());
      assertEquals(3, result.getOutputBinary().length);
      assertEquals((byte)66, result.getOutputBinary()[2]);
   }

   /**
    * Tests a function called with some missing parameters.
    */
   public void testMissingParam() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      CAPI allInOne = new CAPI(descriptor);
      try {
         SimpleTypesResult result = allInOne.callSimpleTypes((byte)8, null, 65, 88l, 72.5f, new Double(37.2),
            null, null, null, Date.fromStringForRequired("20041213"), Timestamp.fromStringForOptional("20041225153222"), null);
         fail("The request is invalid, the function should throw an exception");
      } catch (UnsuccessfulXINSCallException exception) {
         assertEquals("_InvalidRequest", exception.getErrorCode());
         assertEquals(descriptor, exception.getTarget());
         assertNull(exception.getParameters());
         DataElement dataSection = exception.getDataElement();
         assertNotNull(dataSection);
         DataElement missingParam = (DataElement) dataSection.getChildElements().get(0);
         assertEquals("missing-param", missingParam.getName());
         assertEquals("inputText", missingParam.get("param"));
         assertEquals(0, missingParam.getChildElements().size());
         assertNull(missingParam.getText());
      }
   }

   /**
    * Tests CAPI and defined types.
    */
   public void testDefinedTypes() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
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
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
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
         List invalidParams = dataSection.getChildElements();
         DataElement invalidParam1 = (DataElement) invalidParams.get(0);
         assertEquals("invalid-value-for-type", invalidParam1.getName());
         assertEquals("inputIP", invalidParam1.get("param"));
         assertEquals(0, invalidParam1.getChildElements().size());
         assertNull(invalidParam1.getText());
         DataElement invalidParam2 = (DataElement) invalidParams.get(1);
         assertEquals("invalid-value-for-type", invalidParam2.getName());
         assertEquals("inputAge", invalidParam2.get("param"));
         assertEquals(0, invalidParam2.getChildElements().size());
         assertNull(invalidParam2.getText());
      }
   }

   /**
    * Tests a function that should returned a defined result code.
    */
   public void testResultCode() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      CAPI allInOne = new CAPI(descriptor);
      String result1 = allInOne.callResultCode("hello").getOutputText();
      assertEquals("The first call to ResultCode returned an incorrect result", "hello added.", result1);
      try {
         allInOne.callResultCode("hello");
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
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
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
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      CAPI allInOne = new CAPI(descriptor);
      DataElement element = allInOne.callDataSection("Doe").dataElement();
      List users = element.getChildElements();
      assertTrue("No users found.", users.size() > 0);
      DataElement su = (DataElement) users.get(0);
      assertEquals("Incorrect elements.", "user", su.getName());
      assertEquals("Incorrect name for su.", "superuser", su.get("name"));
      assertEquals("Incorrect address.", "12 Madison Avenue", su.get("address"));
      assertEquals("Incorrect PCDATA.", "This user has the root authorisation.", su.getText());
      assertEquals(0, su.getChildElements().size());
      DataElement doe = (DataElement) users.get(1);
      assertEquals("Incorrect elements.", "user", doe.getName());
      assertEquals("Incorrect name for Doe.", "Doe", doe.get("name"));
      assertEquals("Incorrect address.", "Unknown", doe.get("address"));
      assertNull(doe.getText());
      assertEquals(0, doe.getChildElements().size());
   }

   /**
    * Tests a function that returns a data section with elements that contain
    * other elements.
    */
   public void testDataSection2() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/", 2000);
      CAPI allInOne = new CAPI(descriptor);
      DataElement element = allInOne.callDataSection2("hello").dataElement();
      List packets = element.getChildElements();
      assertTrue("No destination found.", packets.size() > 0);
      DataElement packet1 = (DataElement) packets.get(0);
      assertEquals("Incorrect elements.", "packet", packet1.getName());
      assertNotNull("No destination specified.", packet1.get("destination"));
      List products = packet1.getChildElements();
      assertTrue("No product specified.", products.size() > 0);
      DataElement product1 = (DataElement) products.get(0);
      assertEquals("Incorrect price for product1", "12", product1.get("price"));

      DataElement packet2 = (DataElement) packets.get(1);
      assertEquals("Incorrect elements.", "packet", packet2.getName());
      assertNotNull("No destination specified.", packet2.get("destination"));
      List products2 = packet2.getChildElements();
      assertTrue("No product specified.", products2.size() > 0);
      DataElement product21 = (DataElement) products2.get(0);
      assertEquals("Incorrect price for product1", "12", product21.get("price"));
      assertTrue(product21.getChildElements().size() == 0);
   }

  /**
   * Tests a function that passes a data section as input and an output data
   * section with mutiple data element types.
   */
   public void testDataSection3() throws Throwable {
      ElementBuilder builder1 = new ElementBuilder();
      builder1.startElement("address");
      builder1.setAttribute("company", "McDo");
      builder1.setAttribute("postcode", "1234");
      Element address1 = builder1.createElement();
      ElementBuilder builder2 = new ElementBuilder();
      builder2.startElement("address");
      builder2.setAttribute("company", "Hello");
      builder2.setAttribute("postcode", "5678");
      Element address2 = builder2.createElement();
      ElementBuilder builder3 = new ElementBuilder();
      builder3.startElement("data");
      builder3.addChild(address1);
      builder3.addChild(address2);
      Element dataSection = builder3.createElement();

      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/", 2000);
      CAPI allInOne = new CAPI(descriptor);
      DataElement element = allInOne.callDataSection3("hello", dataSection).dataElement();
      List packets = element.getChildElements();
      assertTrue("No packets or envelopes found.", packets.size() == 4);

      DataElement envelope1 = (DataElement) packets.get(0);
      assertEquals("Incorrect elements.", "envelope", envelope1.getLocalName());
      assertEquals("1234", envelope1.getAttribute("destination"));

      DataElement envelope2 = (DataElement) packets.get(1);
      assertEquals("Incorrect elements.", "envelope", envelope2.getLocalName());
      assertEquals("5678", envelope2.getAttribute("destination"));

      DataElement packet1 = (DataElement) packets.get(2);
      assertEquals("Incorrect elements.", "packet", packet1.getLocalName());
      assertNotNull("No destination specified.", packet1.getAttribute("destination"));

      DataElement envelope3 = (DataElement) packets.get(3);
      assertEquals("Incorrect elements.", "envelope", envelope3.getLocalName());
      assertNotNull("No destination specified.", envelope3.getAttribute("destination"));
   }

  /**
   * Tests the param-combos for inclusive-or and exclusive-or.
   */
   public void testParamCombo1() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/", 2000);
      CAPI allInOne = new CAPI(descriptor);
      try {
         allInOne.callParamCombo(null, null, null, null, null, null, null);
         fail("The param-combo call should return an _InvalidRequest error code.");
      } catch (UnsuccessfulXINSCallException exception) {
         assertEquals("_InvalidRequest", exception.getErrorCode());
         assertEquals(descriptor, exception.getTarget());
         assertNull(exception.getParameters());
         assertNotNull(exception.getDataElement());
         DataElement dataSection = exception.getDataElement();
         Iterator itParamCombos = dataSection.getChildElements().iterator();
         if (itParamCombos.hasNext()) {
            DataElement paramCombo1 = (DataElement)itParamCombos.next();
            assertEquals("param-combo", paramCombo1.getLocalName());
            assertEquals("inclusive-or", paramCombo1.getAttribute("type"));
         } else {
            fail("No param combo element found.");
         }
         if (itParamCombos.hasNext()) {
            DataElement paramCombo2 = (DataElement)itParamCombos.next();
            assertEquals("param-combo", paramCombo2.getLocalName());
            assertEquals("exclusive-or", paramCombo2.getAttribute("type"));
         } else {
            fail("Just one param combo element was found.");
         }
      }
   }

  /**
   * Tests the param-combos for all-or-none.
   */
   public void testParamCombo2() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/", 2000);
      CAPI allInOne = new CAPI(descriptor);
      try {
         allInOne.callParamCombo(null, null, new Integer(5), null, "Paris", null, new Byte((byte)33));
         fail("The param-combo call should return an _InvalidRequest error code.");
      } catch (UnsuccessfulXINSCallException exception) {
         assertEquals("_InvalidRequest", exception.getErrorCode());
         assertEquals(descriptor, exception.getTarget());
         assertNull(exception.getParameters());
         assertNotNull(exception.getDataElement());
         DataElement dataSection = exception.getDataElement();
         Iterator itParamCombos = dataSection.getChildElements().iterator();
         if (itParamCombos.hasNext()) {
            DataElement paramCombo1 = (DataElement)itParamCombos.next();
            assertEquals("param-combo", paramCombo1.getLocalName());
            assertEquals("all-or-none", paramCombo1.getAttribute("type"));
         } else {
            fail("No param combo element found.");
         }
      }
   }

   /**
    * Tests the getXINSVersion() CAPI method.
    */
   public void testCAPIVersion() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      CAPI allInOne = new CAPI(descriptor);
      assertNotNull("No XINS version specified.", allInOne.getXINSVersion());
      assertTrue("The version does not starts with '1.'", allInOne.getXINSVersion().startsWith("1."));
   }

   /**
    * Tests a function that does not exists
    */
   public void testUnknownFunction() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("Unknown", null);
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      try {
         XINSCallResult result = caller.call(request);
      } catch (StatusCodeHTTPCallException exception) {
         assertEquals("Incorrect status code found.", 404, exception.getStatusCode());
      }
   }
}
