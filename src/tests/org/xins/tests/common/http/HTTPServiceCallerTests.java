/*
 * $Id$
 */
package org.xins.tests.common.http;

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

   public void testXinsURL() throws Exception {
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET);
      Descriptor descriptor = new TargetDescriptor("http://xins.sourceforge.net");
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", result.getStatusCode(), 200);
      assertEquals("Incorrect succeeded descriptor.", result.getSucceededTarget(), descriptor);
      assertTrue("Incorrect duration.", result.getDuration() > 0 && result.getDuration() < 5000);
      String text = result.getString();
      boolean correctStart = text.equals("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
      assertTrue("Incorrect HTML received.", correctStart);
      assertTrue("Incorrect content.", text.indexOf("XML Interface for Network Services") != -1);
   }

   public void testParameters() throws Exception {
      BasicPropertyReader parameters = new BasicPropertyReader();
      parameters.set("hl", "en");
      parameters.set("q", "XINS");
      parameters.set("btnG", "Google Seacrh");
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET, parameters);
      Descriptor descriptor = new TargetDescriptor("http://www.google.com");
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      HTTPCallResult result = caller.call(request);
      assertEquals("Received incorrect status code.", result.getStatusCode(), 200);
      assertEquals("Incorrect succeeded descriptor.", result.getSucceededTarget(), descriptor);
      assertTrue("Incorrect duration.", result.getDuration() > 0 && result.getDuration() < 5000);
      String text = result.getString("UTF-8");
      assertTrue("Incorect content.", text.indexOf("xins.sourceforge.net") != -1);
   }

   public void testWrongURL() throws Exception {
      BasicPropertyReader parameters = new BasicPropertyReader();
      parameters.set("hello", "world");
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.GET, parameters);
      Descriptor descriptor = new TargetDescriptor("http://xins.sourceforge.net/fakeURL.html");
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      try {
         HTTPCallResult result = caller.call(request);
         //assertEquals("Received incorrect status code.", httpException.getStatusCode(), 404);
         fail("The fake URL should throw a HTTPCallException");
      } catch (HTTPCallException httpException) {
         assertEquals("Incorrect failing target.", httpException.getTarget(), descriptor);
         assertTrue("Incorrect duration.", httpException.getDuration() > 0);
      } catch (CallException exception) {
         fail("The fake URL should throw a HTTPCallException");
      }
   }
   
   public void testFailOver() throws Exception {
      HTTPCallRequest request = new HTTPCallRequest(HTTPMethod.POST);
      TargetDescriptor failedTarget = new TargetDescriptor("http://xins.sourceforge.net/fakeURL.html");
      TargetDescriptor succeededTarget = new TargetDescriptor("http://xins.sourceforge.net/");
      TargetDescriptor[] descriptors = {failedTarget, succeededTarget};
      GroupDescriptor descriptor = new GroupDescriptor(GroupDescriptor.ORDERED_TYPE, descriptors);
      HTTPServiceCaller caller = new HTTPServiceCaller(descriptor);
      try {
         HTTPCallResult result = caller.call(request);
         assertEquals("Received incorrect status code.", result.getStatusCode(), 200);
         assertTrue("Incorrect duration.", result.getDuration() > 0);
         assertEquals("Incorrect succeeded target.", result.getSucceededTarget(), succeededTarget);
         String text = result.getString();
         assertTrue("Incorrect content.", text.indexOf("XML Interface for Network Services") != -1);
      } catch (CallException exception) {
         fail("The fake URL should throw a HTTPCallException");
      }
   }
}
