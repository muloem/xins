/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.http;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.http.HTTPCallRequest;
import org.xins.common.http.HTTPCallResult;
import org.xins.common.http.HTTPMethod;
import org.xins.common.http.HTTPServiceCaller;

import org.xins.common.service.Descriptor;
import org.xins.common.service.GroupDescriptor;
import org.xins.common.service.SocketTimeOutCallException;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.service.UnsupportedProtocolException;

/**
 * Tests for class <code>HTTPServiceCallerTests</code>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class HTTPServiceCallerTests extends TestCase {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Total time-out to use for HTTP connections.
    */
   private final static int TOTAL_TO = 30000;

   /**
    * Connection time-out to use when making HTTP connections.
    */
   private final static int CONN_TO = 15000;

   /**
    * Socket time-out to use on HTTP connections.
    */
   private final static int SOCKET_TO = 15000;


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
      return new TestSuite(HTTPServiceCallerTests.class);
   }

   private static long checksum(String s) {
      CRC32 crc = new CRC32();
      try {
         byte[] bytes = s.getBytes("UTF-8");
         crc.update(bytes);
         return crc.getValue();
      } catch (UnsupportedEncodingException exception) {
         throw new Error(exception);
      }
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>HTTPServiceCaller</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public HTTPServiceCallerTests(String name) {
      super(name);
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testConstructor() throws Exception {

      TargetDescriptor descriptor;

      // One-argument constructor
      HTTPServiceCaller caller = new HTTPServiceCaller(null);
      assertEquals(null, caller.getDescriptor());

      try {
         descriptor = new TargetDescriptor("blah://www.google.com");
         new HTTPServiceCaller(descriptor);
         fail("The \"blah\" protocol should not be supported.");
      } catch (UnsupportedProtocolException upe) {
         // as expected
      }

      descriptor = new TargetDescriptor("http://www.google.com");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      descriptor = new TargetDescriptor("https://www.google.com");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      descriptor = new TargetDescriptor("hTTp://www.google.com");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      descriptor = new TargetDescriptor("HTTPs://www.google.com");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      descriptor = new TargetDescriptor("HTTP://www.google.com");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      descriptor = new TargetDescriptor("https://sourceforge.net");
      caller = new HTTPServiceCaller(descriptor);
      assertEquals(descriptor, caller.getDescriptor());

      // TODO: Add tests for 2-argument constructor
   }

   public void testW3URL() throws Exception {
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET);
      Descriptor descriptor = new TargetDescriptor("http://www.w3.org/Consortium/Legal/2002/copyright-documents-20021231", TOTAL_TO, CONN_TO, SOCKET_TO);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertEquals("Incorrect succeeded descriptor.", descriptor, result.getSucceededTarget());
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
      String text = result.getString();
      boolean correctStart = text.startsWith("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n      \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n  <title>W3C Document License</title>");
      assertTrue("Unexpected HTML received.", correctStart);
      assertEquals(456039016L, checksum(text));
   }

   public void testPostParameters() throws Exception {
      BasicPropertyReader parameters = new BasicPropertyReader();
      parameters.set("pattern", "^([A-Za-z]([A-Za-z\\- ]{0,26}[A-Za-z])?)$");
      parameters.set("string", "Janwillem");
      parameters.set("submit", "submit");
      // XXX GET method doesn't work
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.POST, parameters);
      Descriptor descriptor = new TargetDescriptor("http://xins.sourceforge.net/patterntest.php", TOTAL_TO, CONN_TO, SOCKET_TO);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertEquals("Incorrect succeeded descriptor.", descriptor, result.getSucceededTarget());
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
      String text = result.getString();
      assertTrue("Incorect content.", text.indexOf("\"Janwillem\" <span style='color:blue'>matches</span>") != -1);
   }

   public void testGetParameters() throws Exception {
      BasicPropertyReader parameters = new BasicPropertyReader();
      parameters.set("pattern", "^([A-Za-z]([A-Za-z\\- ]{0,26}[A-Za-z])?)$");
      parameters.set("string", "Janwillem");
      parameters.set("submit", "submit");
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET, parameters);
      Descriptor descriptor = new TargetDescriptor("http://xins.sourceforge.net/patterntest.php", TOTAL_TO, CONN_TO, SOCKET_TO);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertEquals("Incorrect succeeded descriptor.", descriptor, result.getSucceededTarget());
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
      String text = result.getString();
      assertTrue("Incorect content.", text.indexOf("\"Janwillem\" <span style='color:blue'>matches</span>") != -1);
   }

   public void testWrongURL() throws Exception {
      BasicPropertyReader parameters = new BasicPropertyReader();
      parameters.set("hello", "world");
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET, parameters);
      Descriptor descriptor = new TargetDescriptor("http://www.w3.org/nOnExIsTeNt.html", TOTAL_TO, CONN_TO, SOCKET_TO);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);

      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", result.getStatusCode(), 404);
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
   }

   public void testFailOverGet() throws Exception {
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET, null, false, null);
      TargetDescriptor failedTarget = new TargetDescriptor("http://anthony.xins.org", TOTAL_TO, CONN_TO, SOCKET_TO);
      TargetDescriptor succeededTarget = new TargetDescriptor("http://www.w3.org/StyleSheets/TR/W3C-REC.css", TOTAL_TO, CONN_TO, SOCKET_TO);
      TargetDescriptor[] descriptors = {failedTarget, succeededTarget};
      GroupDescriptor descriptor = new GroupDescriptor(GroupDescriptor.ORDERED_TYPE, descriptors);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);

      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
      assertEquals("Incorrect succeeded target.", succeededTarget, result.getSucceededTarget());
      String text = result.getString();
      assertTrue("Incorrect content.", text.indexOf("Copyright 1997-") > 0);
   }

   public void testFailOverPost() throws Exception {
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.POST, null, true, null);
      TargetDescriptor failedTarget = new TargetDescriptor("http://anthony.xins.org", TOTAL_TO, CONN_TO, SOCKET_TO);
      TargetDescriptor succeededTarget = new TargetDescriptor("http://xins.sourceforge.net/patterntest.php", TOTAL_TO, CONN_TO, SOCKET_TO);
      TargetDescriptor[] descriptors = {failedTarget, succeededTarget};
      GroupDescriptor descriptor = new GroupDescriptor(GroupDescriptor.ORDERED_TYPE, descriptors);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);

      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertTrue("Incorrect duration.", result.getDuration() >= 0);
      assertEquals("Incorrect succeeded target.", succeededTarget, result.getSucceededTarget());
      String text = result.getString();
      assertTrue("Incorrect content.", text.indexOf("Pattern test form") != -1);
   }

   public void testSocketTimeOut() throws Exception {
      // Set socket time-out to 1 ms
      TargetDescriptor target = new TargetDescriptor("http://xins.sourceforge.net/", TOTAL_TO, CONN_TO, 1);
      HTTPServiceCaller caller = new HTTPServiceCaller(target);
      HTTPCallRequest request = new HTTPCallRequest();
      try {
         caller.call(request);
         fail("Expected SocketTimeOutCallException.");
      } catch (SocketTimeOutCallException exception) {
         // as expected

         // Test some aspects of the exception
         assertNull(exception.getNext());
         assertEquals(request, exception.getRequest());
         assertEquals(target,  exception.getTarget());
      }
   }
}
