/*
 * $Id$
 */
package org.xins.tests.common.http;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.http.HTTPCallException;
import org.xins.common.http.HTTPCallRequest;
import org.xins.common.http.HTTPCallResult;
import org.xins.common.http.HTTPMethod;

import org.xins.common.http.HTTPServiceCaller;
import org.xins.common.service.CallException;
import org.xins.common.service.Descriptor;
import org.xins.common.service.GroupDescriptor;
import org.xins.common.service.TargetDescriptor;

/**
 * Tests for class <code>HTTPServiceCallerTests</code>.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class HTTPServiceCallerTests extends TestCase {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

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

   public void testW3URL() throws Exception {
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET);
      Descriptor descriptor = new TargetDescriptor("http://www.w3.org/TR/2004/REC-xml-20040204/");
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertEquals("Incorrect succeeded descriptor.", descriptor, result.getSucceededTarget());
      assertTrue("Incorrect duration.", result.getDuration() > 0 && result.getDuration() < 5000);
      String text = result.getString();
      boolean correctStart = text.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html lang=\"EN\" xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" /><title>Extensible Markup Language (XML) 1.0 (Third Edition)</title>");
      assertTrue("Unexpected HTML received.", correctStart);
      assertEquals(2840783247L, checksum(text));
   }

   public void testParameters() throws Exception {
      BasicPropertyReader parameters = new BasicPropertyReader();
      parameters.set("pattern", "^([A-Za-z]([A-Za-z\\- ]{0,26}[A-Za-z])?)$");
      parameters.set("string", "Janwillem");
      parameters.set("submit", "submit");
      // XXX GET method doesn't work
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.POST, parameters);
      Descriptor descriptor = new TargetDescriptor("http://xins.sourceforge.net/patterntest.php");
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertEquals("Incorrect succeeded descriptor.", descriptor, result.getSucceededTarget());
      assertTrue("Incorrect duration.", result.getDuration() > 0 && result.getDuration() < 5000);
      String text = result.getString();
      assertTrue("Incorect content.", text.indexOf("\"Janwillem\" <span style='color:blue'>matches</span>") != -1);
   }

   public void testWrongURL() throws Exception {
      BasicPropertyReader parameters = new BasicPropertyReader();
      parameters.set("hello", "world");
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET, parameters);
      Descriptor descriptor = new TargetDescriptor("http://www.w3.org/nOnExIsTeNt.html");
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);

      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", result.getStatusCode(), 404);
      assertTrue("Incorrect duration.", result.getDuration() > 0);
   }
   
   public void testFailOverGet() throws Exception {
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET, null, false, null);
      TargetDescriptor failedTarget = new TargetDescriptor("http://anthony.xins.org");
      TargetDescriptor succeededTarget = new TargetDescriptor("http://www.w3.org/StyleSheets/TR/W3C-REC.css");
      TargetDescriptor[] descriptors = {failedTarget, succeededTarget};
      GroupDescriptor descriptor = new GroupDescriptor(GroupDescriptor.ORDERED_TYPE, descriptors);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);

      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertTrue("Incorrect duration.", result.getDuration() > 0);
      assertEquals("Incorrect succeeded target.", succeededTarget, result.getSucceededTarget());
      String text = result.getString();
      assertTrue("Incorrect content.", text.indexOf("Copyright 1997-") > 0);
   }
   
   public void testFailOverPost() throws Exception {
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.POST, null, true, null);
      TargetDescriptor failedTarget = new TargetDescriptor("http://anthony.xins.org");
      TargetDescriptor succeededTarget = new TargetDescriptor("http://xins.sourceforge.net/patterntest.php");
      TargetDescriptor[] descriptors = {failedTarget, succeededTarget};
      GroupDescriptor descriptor = new GroupDescriptor(GroupDescriptor.ORDERED_TYPE, descriptors);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);

      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", 200, result.getStatusCode());
      assertTrue("Incorrect duration.", result.getDuration() > 0);
      assertEquals("Incorrect succeeded target.", succeededTarget, result.getSucceededTarget());
      String text = result.getString();
      assertTrue("Incorrect content.", text.indexOf("Pattern test form") != -1);
   }
}
