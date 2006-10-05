/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server.frontend;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import com.mycompany.portal.capi.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.DataElement;
import org.xins.client.InternalErrorException;
import org.xins.client.InvalidRequestException;
import org.xins.client.UnacceptableRequestException;
import org.xins.client.UnsuccessfulXINSCallException;
import org.xins.client.XINSCallConfig;
import org.xins.client.XINSCallRequest;
import org.xins.client.XINSCallResult;
import org.xins.client.XINSServiceCaller;
import org.xins.common.ProgrammingException;
import org.xins.common.Utils;
import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.PropertyReader;
import org.xins.common.http.HTTPCallRequest;
import org.xins.common.http.HTTPCallResult;
import org.xins.common.http.HTTPMethod;
import org.xins.common.http.HTTPServiceCaller;
import org.xins.common.http.StatusCodeHTTPCallException;
import org.xins.common.service.TargetDescriptor;
import org.xins.common.servlet.container.HTTPServletHandler;
import org.xins.common.types.standard.Date;
import org.xins.common.types.standard.Timestamp;
import org.xins.common.xml.Element;
import org.xins.common.xml.ElementBuilder;
import org.xins.common.xml.ElementParser;
import org.xins.logdoc.ExceptionUtils;

import org.xins.tests.AllTests;
import org.xins.tests.StartServer;

/**
 * Tests the functions in the <em>allinone</em> API using the generated CAPI
 * classes.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:anthony.goubard@orange-ft.com">Anthony Goubard</a>
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 */
public class PortalAPITests extends TestCase {

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
      return new TestSuite(PortalAPITests.class);
   }


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>PortalAPITests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public PortalAPITests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   private TargetDescriptor _target;
   private CAPI _capi;

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void setUp() throws Exception {
      int port = AllTests.port() + 1;
      _target = new TargetDescriptor("http://localhost:" + port + "/portal/");
      _capi   = new CAPI(_target);
   }

   public void testControlCommand() throws Exception {
      BasicPropertyReader params = new BasicPropertyReader();
      params.set("command", "Control");
      String htmlResult = callCommand(params);
      assertTrue("Incorrect content.", htmlResult.indexOf("API start-up time") != -1);
   }

   public void testFlushControlCommand() throws Exception {
      BasicPropertyReader params = new BasicPropertyReader();
      params.set("command", "Control");
      params.set("action", "FlushCommandTemplateCache");
      callCommand(params);
   }

   public void testRefreshControlCommand() throws Exception {
      BasicPropertyReader params = new BasicPropertyReader();
      params.set("command", "Control");
      params.set("action", "RefreshCommandTemplateCache");
      callCommand(params);
   }

   public void testRemoveSessionPropertiesControlCommand1() throws Exception {
      BasicPropertyReader params = new BasicPropertyReader();
      params.set("command", "Control");
      params.set("action", "RemoveSessionProperties");
      callCommand(params);
   }

   public void testRemoveSessionPropertiesControlCommand2() throws Exception {
      
      // Check that testProp is not in the session
      BasicPropertyReader paramsControl = new BasicPropertyReader();
      paramsControl.set("command", "Control");
      String controlResult1 = callCommand(paramsControl);
      assertTrue("username property already in the session.", controlResult1.indexOf("username") == -1);
      
      // Add testProp to the session
      BasicPropertyReader paramsLogin = createLoginParams();
      callCommand(paramsLogin);
      
      // Check that it's in the session
      String controlResult2 = callCommand(paramsControl);
      assertTrue("username property not found in the session.", controlResult2.indexOf("test1") != -1);
      
      // Clear the session
      BasicPropertyReader paramsRemove = new BasicPropertyReader();
      paramsRemove.set("command", "Control");
      paramsRemove.set("action", "RemoveSessionProperties");
      callCommand(paramsRemove);
      
      // Check that it's no more in the session
      String controlResult3 = callCommand(paramsControl);
      assertTrue("username has not been cleared of the session.", controlResult3.indexOf("test1") == -1);
   }

   public void testRedirection() throws Exception {
      BasicPropertyReader params = createLoginParams();
      String result = callCommand(params);
      assertEquals("", result);
   }

   public void testSourceMode() throws Exception {
      BasicPropertyReader params = createLoginParams();
      callCommand(params);
      BasicPropertyReader params2 = new BasicPropertyReader();
      params2.set("command", "MainPage");
      params2.set("mode", "source");
      String xmlResult = callCommand(params2);
      ElementParser parser = new ElementParser();
      Element result = parser.parse(new StringReader(xmlResult));
      assertEquals("commandresult", result.getLocalName());
      assertEquals(4, result.getChildElements("parameter").size());
      assertEquals(1, result.getAttributeMap().size());
   }

   public void testTemplateMode() throws Exception {
      BasicPropertyReader params = createLoginParams();
      params.set("mode", "template");
      String xmlResult = callCommand(params);
      ElementParser parser = new ElementParser();
      Element result = parser.parse(new StringReader(xmlResult));
      assertEquals("stylesheet", result.getLocalName());
   }

   public void testInvalidRequest() throws Exception {
      BasicPropertyReader params = createLoginParams();
      params.remove("password");
      params.set("mode", "source");
      String xmlResult = callCommand(params);
      ElementParser parser = new ElementParser();
      Element result = parser.parse(new StringReader(xmlResult));
      assertEquals("commandresult", result.getLocalName());
      assertTrue(xmlResult.indexOf("<parameter name=\"error.type\">FieldError</parameter>") != -1);
      Element error = result.getUniqueChildElement("data").getUniqueChildElement("errorlist").getUniqueChildElement("fielderror");
      assertEquals("mand", error.getAttribute("type"));
      assertEquals("password", error.getAttribute("field"));
   }

   private String callCommand(PropertyReader params) throws Exception {
      HTTPServiceCaller callControl = new HTTPServiceCaller(_target);
      HTTPCallRequest callRequest = new HTTPCallRequest(params);
      HTTPCallResult callResult = callControl.call(callRequest);
      assertTrue("Incorrect status code returned: " + callResult.getStatusCode(),
            callResult.getStatusCode() < 400);
      String htmlResult = callResult.getString();
      return htmlResult;
   }

   private BasicPropertyReader createLoginParams() {
      BasicPropertyReader paramsLogin = new BasicPropertyReader();
      paramsLogin.set("command", "Login");
      paramsLogin.set("action", "Okay");
      paramsLogin.set("username", "test1");
      paramsLogin.set("password", "passW1");
      return paramsLogin;
   }

   /**
    * Tests invalid responses from the server.
    */
   public void testInvalidResponse() throws Exception {
      XINSCallRequest request = new XINSCallRequest("InvalidResponse");
      XINSServiceCaller caller = new XINSServiceCaller(_target);
      try {
         caller.call(request);
         fail("No invalid response received as expected.");
      } catch (UnsuccessfulXINSCallException exception) {
         assertEquals("_InvalidResponse", exception.getErrorCode());
         assertEquals(_target, exception.getTarget());
         assertNull(exception.getParameters());
         DataElement dataSection = exception.getDataElement();
         assertNotNull(dataSection);
         DataElement missingParam = (DataElement) dataSection.getChildElements().get(0);
         assertEquals("missing-param", missingParam.getName());
         assertEquals("outputText1", missingParam.get("param"));
         assertEquals(0, missingParam.getChildElements().size());
         assertNull(missingParam.getText());
         DataElement invalidParam = (DataElement) dataSection.getChildElements().get(1);
         assertEquals("invalid-value-for-type", invalidParam.getName());
         assertEquals("pattern", invalidParam.get("param"));
         assertEquals(0, invalidParam.getChildElements().size());
         assertNull(invalidParam.getText());
      }
   }

   /**
    * Tests invalid responses from the server using the new XINS 1.2 call
    * method.
    */
   public void testInvalidResponse2() throws Exception {

      InvalidResponseRequest request = new InvalidResponseRequest();
      try {
         _capi.callInvalidResponse(request);
         fail("Expected InternalErrorException.");
      } catch (InternalErrorException exception) {
         assertEquals("_InvalidResponse", exception.getErrorCode());
         assertEquals(_target, exception.getTarget());
         assertNull(exception.getParameters());
         DataElement dataSection = exception.getDataElement();
         assertNotNull(dataSection);
         DataElement missingParam = (DataElement) dataSection.getChildElements().get(0);
         assertEquals("missing-param", missingParam.getName());
         assertEquals("outputText1", missingParam.get("param"));
         assertEquals(0, missingParam.getChildElements().size());
         assertNull(missingParam.getText());
         DataElement invalidParam = (DataElement) dataSection.getChildElements().get(1);
         assertEquals("invalid-value-for-type", invalidParam.getName());
         assertEquals("pattern", invalidParam.get("param"));
         assertEquals(0, invalidParam.getChildElements().size());
         assertNull(invalidParam.getText());
      }

      request = new InvalidResponseRequest();
      request.setErrorCode("ErrorCodeNotKnownWhatsoever");
      try {
         _capi.callInvalidResponse(request);
         fail("Expected InternalErrorException.");
      } catch (InternalErrorException exception) {
         assertEquals("_InternalError", exception.getErrorCode());
         assertEquals(_target,          exception.getTarget());
      }

      request = new InvalidResponseRequest();
      request.setErrorCode("InvalidNumber");
      try {
         _capi.callInvalidResponse(request);
         fail("Expected InternalErrorException.");
      } catch (InternalErrorException exception) {
         assertEquals("_InternalError", exception.getErrorCode());
         assertEquals(_target,          exception.getTarget());
      }
   }

   public void testGetSettingsFunction() throws Exception {
      XINSCallRequest request = new XINSCallRequest("_GetSettings");
      XINSServiceCaller caller = new XINSServiceCaller(_target);
      try {
         caller.call(request);
         fail("The call to _GetSettings should have failed with ACL denied.");
      } catch (StatusCodeHTTPCallException schcex) {

         // As expected.
         assertEquals(403, schcex.getStatusCode());
      }
   }
}