/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.UnsuccessfulXINSCallException;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;
import org.xins.common.http.StatusCodeHTTPCallException;
import org.xins.common.service.TargetDescriptor;

import org.xins.client.DataElement;
import org.xins.client.XINSCallRequest;
import org.xins.client.XINSCallResult;
import org.xins.client.XINSServiceCaller;


/**
 * Tests for XINS meta functions.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Tauseef Rehman (<a href="mailto:tauseef.rehman@nl.wanadoo.com">tauseef.rehman@nl.wanadoo.com</a>)
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
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>MetaFunctionsTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public MetaFunctionsTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Tests the _GetVersion meta function.
    */
   public void testGetVersion() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_GetVersion", null);
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      XINSCallResult result = caller.call(request);
      assertNull("The function returned a result code.", result.getErrorCode());
      assertNull("The function returned a data element.", result.getDataElement());
      PropertyReader parameters = result.getParameters();
      assertNotNull("The function did not returned any parameters.", parameters);
      assertNotNull("No java version specified.", parameters.get("java.version"));
      assertNotNull("No XINS version specified.", parameters.get("xins.version"));
      assertNotNull("No xmlenc version specified.", parameters.get("xmlenc.version"));
      assertNotNull("No API version specified.", parameters.get("api.version"));
      assertEquals("Wrong API version specified.", "1.6", parameters.get("api.version"));
      assertEquals("Incorrect number of parameters.", 4, parameters.size());
   }

   /**
    * Tests the _GetStatistics meta function.
    */
   public void testGetStatistics() throws Throwable {
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      XINSCallResult result;

      // Determine the remote Java version
      XINSCallRequest request = new XINSCallRequest("_GetVersion");
      result = caller.call(request);
      assertNull("The function returned a result code.", result.getErrorCode());
      PropertyReader parameters = result.getParameters();
      assertNotNull("The function _GetVersion did not returned any parameters.", parameters);
      String javaVersion = parameters.get("java.version");
      assertNotNull("No Java version returned by _GetVersion.", javaVersion);
      assertTrue(javaVersion.length() > 0);
      boolean java14 = Utils.getJavaVersion() >= 1.4;

      // Get the statistics
      request = new XINSCallRequest("_GetStatistics", null);
      result = caller.call(request);
      assertNull("The function _GetStatistics returned a result code.", result.getErrorCode());
      assertNotNull("The function returned a data element.", result.getDataElement());
      parameters = result.getParameters();
      assertNotNull("The function _GetStatistics did not returned any parameters.", parameters);
      String startup = parameters.get("startup");
      assertNotNull("No startup date specified.", startup);
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS");
      try {
         formatter.parse(startup);
      } catch (ParseException parseException) {
         fail("Incorrect date format for startup time (\"" + startup + "\".");
      }
      String now = parameters.get("now");
      assertNotNull("No (now) date specified.", now);
      try {
         Date nowDate = formatter.parse(now);
         assertTrue("Start-up time (\"" + startup + "\") is after now time (\"" + now + "\").", nowDate.after(formatter.parse(startup)));
      } catch (ParseException parseException) {
         fail("Incorrect date format for 'now'.");
      }
      DataElement data = result.getDataElement();
      assertNull(data.getAttributes());
      List children = data.getChildElements();

      DataElement heap = (DataElement) children.get(0);
      assertNotNull("No total memory provided.", heap.get("total"));
      assertNotNull("No used memory provided.", heap.get("used"));
      assertNotNull("No free memory provided.", heap.get("free"));
      if (java14) {
         assertNotNull("No max memory provided.", heap.get("max"));
      }

      try {
         Long.parseLong(heap.get("total"));
         Long.parseLong(heap.get("used"));
         Long.parseLong(heap.get("free"));
         if (java14) {
            Long.parseLong(heap.get("max"));
         }
      } catch (Exception exception) {
         fail("Incorrect value while parsing a memory size.");
      }

      // browse all function
      int size = children.size();
      for (int i = 1; i < size; i++) {
         DataElement nextFunction = (DataElement) children.get(i);
         assertEquals("Object other than a function has been found.", "function", nextFunction.getName());
         assertNotNull("The function does not have a name", nextFunction.get("name"));
         // XXX: Also test the children.
         List subElements = nextFunction.getChildElements();
         assertTrue(subElements.size() >= 2);
         DataElement successful = (DataElement) subElements.get(0);
         checkFunctionStatistics(successful, true);
         DataElement unsuccessful = (DataElement) subElements.get(1);
         checkFunctionStatistics(unsuccessful, false);
      }
   }

   /**
    * Checks that the attributes of the successful or unsuccessful result are
    * returned correctly.
    *
    * @param functionElement
    *    the successful or unsuccessful element.
    * @param successful
    *    true if it's the successful element, false if it's the unsuccessful
    *    element.
    *
    * @throws Throwable
    *    if something fails.
    */
   private void checkFunctionStatistics(DataElement functionElement, boolean successful) throws Throwable {
      String success = successful ? "successful" : "unsuccessful";

      assertEquals("The function does not have any " + success + " sub-section.", success, functionElement.getName());
      assertNotNull("No average attribute defined", functionElement.get("average"));
      assertNotNull("No count attribute defined", functionElement.get("count"));
      List minMaxLast = functionElement.getChildElements();
      assertTrue(minMaxLast.size() >= 3);
      DataElement min = (DataElement) minMaxLast.get(0);
      assertEquals("The function does not have any successful sub-section.", "min", min.getName());
      assertNotNull("No average attribute defined", min.get("start"));
      assertNotNull("No count attribute defined", min.get("duration"));
      DataElement max = (DataElement) minMaxLast.get(1);
      assertEquals("The function does not have any successful sub-section.", "max", max.getName());
      assertNotNull("No average attribute defined", max.get("start"));
      assertNotNull("No count attribute defined", max.get("duration"));
      DataElement last = (DataElement) minMaxLast.get(2);
      assertEquals("The function does not have any successful sub-section.", "last", last.getName());
      assertNotNull("No average attribute defined", last.get("start"));
      assertNotNull("No count attribute defined", last.get("duration"));
   }

   /**
    * Tests the _NoOp meta function.
    */
   public void testNoOp() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_NoOp", null);
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      XINSCallResult result = caller.call(request);
      assertNull("The function returned a result code.", result.getErrorCode());
      assertNull("The function returned a data element.", result.getDataElement());
      assertNull("The function returned some parameters.", result.getParameters());
   }

   /**
    * Tests the _GetFunctionList meta function.
    */
   public void testGetFunctionList() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_GetFunctionList", null);
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      XINSCallResult result = caller.call(request);
      assertNull("The function returned a result code.", result.getErrorCode());
      assertNull("The function returned some parameters.", result.getParameters());
      assertNotNull("The function did not return a data element.", result.getDataElement());
      List functions = result.getDataElement().getChildElements();
      int size = functions.size();
      for (int i = 0; i < size; i++) {
         DataElement nextFunction = (DataElement) functions.get(i);
         assertEquals("Element other than a function found.", "function", nextFunction.getName());
         String version = nextFunction.get("version");
         String name = nextFunction.get("name");
         String enabled = nextFunction.get("enabled");
         assertNotNull(version);
         assertNotNull(name);
         assertNotNull(enabled);
         try {
            Double.parseDouble(version);
         } catch (NumberFormatException exception) {
            fail("Incorrect version number: " + exception.getMessage());
         }
         // By default all function are enabled
         assertEquals("The function is not enabled.", "true", enabled);
      }
   }

   /**
    * Tests the _GetSettings meta function.
    */
   public void testGetSettings() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_GetSettings", null);
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      XINSCallResult result = caller.call(request);
      assertNull("The function returned a result code.", result.getErrorCode());
      assertNull("The function returned some parameters.", result.getParameters());
      assertNotNull("The function did not return a data element.", result.getDataElement());
      List functions = result.getDataElement().getChildElements();

      assertTrue("No build section defined.", functions.size() > 0);
      DataElement build = (DataElement) functions.get(0);
      assertNull(build.getAttributes());
      assertEquals("build", build.getName());
      List buildProps = build.getChildElements();
      assertNotNull(buildProps);
      int size = buildProps.size();
      for (int i = 0; i < size; i++) {
         DataElement nextProp = (DataElement) buildProps.get(i);
         assertEquals("Element other than a property found.", "property", nextProp.getName());
         assertNotNull("No name attribute for the property.", nextProp.get("name"));
         assertNotNull("No value for the \"" + nextProp.get("name") + "\" property", nextProp.getText());
      }

      assertTrue("No runtime section defined.", functions.size() > 1);
      DataElement runtime = (DataElement) functions.get(1);
      assertNull(runtime.getAttributes());
      assertEquals("runtime", runtime.getName());
      List runtimeProps = runtime.getChildElements();
      assertNotNull(runtimeProps);
      size = runtimeProps.size();
      for (int i = 0; i < size; i++) {
         DataElement nextProp = (DataElement) runtimeProps.get(i);
         assertEquals("Element other than a property found.", "property", nextProp.getName());
         assertNotNull("No name attribute for the property.", nextProp.get("name"));
         assertNotNull("No value for the \"" + nextProp.get("name") + "\" property", nextProp.getText());
      }

      assertTrue("No system section defined.", functions.size() > 2);
      DataElement system = (DataElement) functions.get(2);
      assertNull(system.getAttributes());
      assertEquals("system", system.getName());
      List systemProps = system.getChildElements();
      assertNotNull(systemProps);
      size = systemProps.size();
      for (int i = 0; i < size; i++) {
         DataElement nextProp = (DataElement) systemProps.get(i);
         assertEquals("Element other than a property found.", "property", nextProp.getName());
         assertNotNull("No name attribute for the property.", nextProp.get("name"));
         assertNotNull("No value for the \"" + nextProp.get("name") + "\" property", nextProp.getText());
      }
   }

   /**
    * Tests the _DisableFunction and _EnableFunction meta functions.
    */
   public void testDisableEnableFunction() throws Throwable {
      // Test that the function is working
      BasicPropertyReader parameters = new BasicPropertyReader();
      parameters.set("inputText", "12000");
      XINSCallRequest request = new XINSCallRequest("Logdoc", parameters);
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      XINSCallResult result = caller.call(request);
      assertNull("The function returned a result code.", result.getErrorCode());
      assertNull("The function returned some parameters.", result.getParameters());
      assertNull("The function returned a data element.", result.getDataElement());

      // Disable the function
      BasicPropertyReader parameters2 = new BasicPropertyReader();
      parameters2.set("functionName", "Logdoc");
      XINSCallRequest request2 = new XINSCallRequest("_DisableFunction", parameters2);
      XINSCallResult result2 = caller.call(request2);
      assertNull("The function returned a result code.", result2.getErrorCode());
      assertNull("The function returned some parameters.", result2.getParameters());
      assertNull("The function returned a data element.", result2.getDataElement());

      // Test that the function is not working anymore
      try {
         XINSCallResult result3 = caller.call(request);
         fail("The call of a disabled function did not throw an exception.");
      } catch (UnsuccessfulXINSCallException exception) {
         assertEquals("Incorrect error code.", "_DisabledFunction", exception.getErrorCode());
         assertNull("The function returned some parameters.", exception.getParameters());
         assertNull("The function returned a data element.", exception.getDataElement());
      }

      // Enable the function
      XINSCallRequest request3 = new XINSCallRequest("_EnableFunction", parameters2);
      XINSCallResult result3 = caller.call(request3);
      assertNull("The function returned a result code.", result3.getErrorCode());
      assertNull("The function returned some parameters.", result3.getParameters());
      assertNull("The function returned a data element.", result3.getDataElement());

      // Test that the function is working
      XINSCallResult result4 = caller.call(request);
      assertNull("The function returned a result code.", result4.getErrorCode());
      assertNull("The function returned some parameters.", result4.getParameters());
      assertNull("The function returned a data element.", result4.getDataElement());
   }

   /**
    * Tests the _ReloadProperties meta function.
    * This test just tests if the meta function return succeeded.
    */
   public void testReloadProperties() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_ReloadProperties", null);
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/");
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      XINSCallResult result = caller.call(request);
      assertNull("The function returned a result code.", result.getErrorCode());
      assertNull("The function returned a data element.", result.getDataElement());
      assertNull("The function returned some parameters.", result.getParameters());
   }

   /**
    * Tests a meta function that does not exists.
    */
   public void testUnknownMetaFunction() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_Unknown", null);
      TargetDescriptor descriptor = new TargetDescriptor("http://127.0.0.1:8080/", 2000);
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      try {
         XINSCallResult result = caller.call(request);
      } catch (StatusCodeHTTPCallException exception) {
         assertEquals("Incorrect status code found.", 404, exception.getStatusCode());
      }
   }

   /**
    * Tests the _CheckLinks.
    */
   public void testCheckLinks() throws Throwable {
      XINSCallRequest request = new XINSCallRequest("_CheckLinks", null);
      TargetDescriptor descriptor =
         new TargetDescriptor("http://127.0.0.1:8080/", 20000);
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);
      XINSCallResult result = caller.call(request);
      assertNull("The function returned a result code.", result.getErrorCode());

      PropertyReader parameters = result.getParameters();
      assertEquals(2, parameters.size());
      assertEquals("7", parameters.get("linkCount"));
      //assertEquals(parameters.get("errorCount"), "4");

      DataElement dataElement = result.getDataElement();
      List elementList = dataElement.getChildElements();
      assertEquals(7, elementList.size());

      Iterator elementIt = elementList.iterator();
      while (elementIt.hasNext()) {
         DataElement element = (DataElement)elementIt.next();

         assertEquals("check", element.getLocalName());
         assertNotNull(element.getAttribute("url"));
         assertNotNull(element.getAttribute("result"));
         assertNotNull(element.getAttribute("duration"));

         String url = element.getAttribute("url");
         if ("http://www.cnn.com".equals(url)) {
            assertEquals("Success", element.getAttribute("result"));
         } else if ("http://www.bbc.co.uk".equals(url)) {
            assertEquals("Success", element.getAttribute("result"));
         } else if ("http://www.paypal.com:8080/".equals(url)) {
            assertEquals("ConnectionTimeout", element.getAttribute("result"));
         } else if ("http://127.0.0.1:7/".equals(url)) {
            assertEquals("ConnectionRefusal", element.getAttribute("result"));
         } else if ("http://tauseef.xins.org/".equals(url)) {
            assertEquals("UnknownHost", element.getAttribute("result"));
         } else if ("http://www.sourceforge.com/".equals(url)) {
            assertEquals("SocketTimeout", element.getAttribute("result"));
         } else if ("http://www.google.com/".equals(url)) {
            assertEquals("Success", element.getAttribute("result"));
         } else {
            fail("Contains a URL: " + url +
               ", which was not specified in the xins.properties");
         }
      }
   }

   /**
    * Tests that multiple calls to the server in parallel works.
    */
   public void testMultipleCallsToServer() throws Throwable {

      XINSCallRequest request = new XINSCallRequest("_NoOp", null);
      TargetDescriptor descriptor =
         new TargetDescriptor("http://127.0.0.1:8080/", 20000);
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);

      // Creating threads.
      int totalThreads = 20;
      MultiCallChecker[] threads = new MultiCallChecker[totalThreads];
      for (int count = 0; count < totalThreads; count ++) {
         MultiCallChecker callCheckerThread =
            new MultiCallChecker(caller, request);
         threads[count] = callCheckerThread;
      }

      // Running threads.
      for (int count = 0; count < totalThreads; count ++) {
         threads[count].start();
      }

      // Waiting till threads are finished.
      for (int count = 0; count < totalThreads; count ++) {
         threads[count].join();
      }

      // Testing threads.
      for (int count = 0; count < totalThreads; count ++) {
         MultiCallChecker callChecker = threads[count];

         assertNull(count + "Failed due to unexpected exception: " +
            callChecker.getException(), callChecker.getException());
         XINSCallResult result = callChecker.getCallResult();
         assertNull("The function returned a result code." +
            result.getErrorCode(), result.getErrorCode());
         assertNull("The function returned a data element." +
            result.getDataElement(), result.getDataElement());
         assertNull("The function returned some parameters.",
            result.getParameters());
      }
   }

   /**
    * Tests the <code>_ResetStatistics</code> method.
    */
   public void testResetStatistics() throws Throwable {

      TargetDescriptor descriptor =
         new TargetDescriptor("http://127.0.0.1:8080/", 20000);
      XINSServiceCaller caller = new XINSServiceCaller(descriptor);

      XINSCallRequest request = new XINSCallRequest("_ResetStatistics", null);
      caller.call(request);

      // TODO: Test output
      // TODO: Make sure _GetStatistics indicates no calls were done
      // TODO: Make sure _GetStatistics returns proper _lastReset value
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Runs multiple threads which make a call to the server.
    * The call can be any call to the server.
    *
    * The following example uses a {@link MultipleCallChecker} object to
    * make a call to the server.
    *
    * <pre>
    * XINSCallRequest request = new XINSCallRequest("_NoOp", null);
    * TargetDescriptor descriptor =
    *    new TargetDescriptor("http://127.0.0.1:8080/", 20000);
    * XINSServiceCaller caller = new XINSServiceCaller(descriptor);
    *
    * // Creating the thread.
    * MultiCallChecker callCheckerThread =
    *    new MultiCallChecker(caller, request);
    * callCheckerThread.start();
    *
    * // Testing threads.
    * Throwable exception = callCheckerThread.getException());
    * XINSCallResult result = callCheckerThread.getCallResult();
    * </pre>
    *
    * @version $Revision$ $Date$
    * @author Tauseef Rehman (<a href="mailto:tauseef.rehman@nl.wanadoo.com">tauseef.rehman@nl.wanadoo.com</a>)
    */
   private static final class MultiCallChecker extends Thread {

      //-------------------------------------------------------------------------
      // Constructors
      //-------------------------------------------------------------------------

      /**
       * Constructs a new <code>MultiCallChecker</code>.
       *
       * @param caller
       *    the {@link XINSServiceCaller}, which is used to make a call to
       *    the server, cannot be <code>null</code>.
       *
       * @param request
       *    the {@link XINSCallRequest}, which has the desired call to the
       *    server, cannot be <code>null</code>.
       *
       * @throws IllegalArgumentException
       *    if <code>caller == null || request == null</code>.
       */
      public MultiCallChecker (XINSServiceCaller caller,
         XINSCallRequest request)
      throws IllegalArgumentException{

         MandatoryArgumentChecker.check("caller", caller, "request", request);

         _caller = caller;
         _request = request;
         _exception = null;
      }


      //-------------------------------------------------------------------------
      // Fields
      //-------------------------------------------------------------------------

      /**
       * The call request with the desired call to the server.
       */
      private XINSCallRequest _request;

      /**
       * The caller used to make the call to the server.
       */
      private XINSServiceCaller _caller;

      /**
       * The result of the call to the server.
       */
      private XINSCallResult _result;

      /**
       * The exception returned by the call to the server.
       */
      private Throwable _exception;


      //-------------------------------------------------------------------------
      // Methods
      //-------------------------------------------------------------------------

      /**
       * Makes a call to the server using the caller and request.
       */
      public void run() {
         try {
            _result = _caller.call(_request);
         } catch (Throwable exception) {
            exception.printStackTrace();
            _exception = exception;
         }
      }

      /**
       * Return the result of the call made to the server.
       *
       * @return
       *    the result, never <code>null</code>.
       */
     public XINSCallResult getCallResult() {
         return _result;
      }

     /**
      * Return the exception occured while making a call to the server.
      *
      * @return
      *    the exception, never <code>null</code>.
      */
      public Throwable getException() {
         return _exception;
      }

   }
}
