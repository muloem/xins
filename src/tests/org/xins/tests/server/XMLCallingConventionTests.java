/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import java.io.*;
import java.net.*;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.http.HTTPCallRequest;
import org.xins.common.http.HTTPCallResult;
import org.xins.common.http.HTTPServiceCaller;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.text.HexConverter;
import org.xins.common.text.ParseException;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementParser;

import org.xins.tests.AllTests;

/**
 * Tests for calling conventions.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class XMLCallingConventionTests extends TestCase {

   /**
    * The random number generator.
    */
   private final static Random RANDOM = new Random();

   /**
    * Constructs a new <code>XMLCallingConventionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public XMLCallingConventionTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(XMLCallingConventionTests.class);
   }

   /**
    * Test the XML calling convention.
    */
   public void testXMLCallingConvention() throws Throwable {
      String randomLong = HexConverter.toHexString(RANDOM.nextLong());
      String randomFive = randomLong.substring(0, 5);

      // Successful call
      postXMLRequest(randomFive, true);

      // Unsuccessful call
      postXMLRequest(randomFive, false);
   }

   /**
    * Posts XML request.
    *
    * @param randomFive
    *    A randomly generated String.
    * @param success
    *    <code>true</code> if the expected result should be successfal,
    *    <code>false</code> otherwise.
    *
    * @throws Exception
    *    If anything goes wrong.
    */
   private void postXMLRequest(String randomFive, boolean success) throws Exception {
      String destination = AllTests.url() + "allinone/?_convention=_xins-xml";
      String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
              "<request function=\"ResultCode\">" +
              "  <param name=\"useDefault\">false</param>" +
              "  <param name=\"inputText\">" + randomFive + "</param>" +
              "</request>";
      Element result = CallingConventionTests.postXML(destination, data);
      assertEquals("result", result.getLocalName());
      if (success) {
         assertNull("The method returned an error code: " + result.getAttribute("errorcode"), result.getAttribute("errorcode"));
      } else {
         assertNotNull("The method did not return an error code for the second call: " + result.getAttribute("errorcode"), result.getAttribute("errorcode"));
         assertEquals("AlreadySet", result.getAttribute("errorcode"));
      }
      assertNull("The method returned a code attribute: " + result.getAttribute("code"), result.getAttribute("code"));
      assertNull("The method returned a success attribute.", result.getAttribute("success"));
      List child = result.getChildElements();
      assertEquals(1, child.size());
      Element param = (Element) child.get(0);
      assertEquals("param", param.getLocalName());
      if (success) {
         assertEquals("outputText", param.getAttribute("name"));
         assertEquals(randomFive + " added.", param.getText());
      } else {
         assertEquals("count", param.getAttribute("name"));
         assertEquals("1", param.getText());
      }
   }
}
