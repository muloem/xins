/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server.frontend;

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
      HTTPServiceCaller callControl = new HTTPServiceCaller(_target);
      BasicPropertyReader params = new BasicPropertyReader();
      params.set("command", "Control");
      HTTPCallRequest callRequest = new HTTPCallRequest(params);
      HTTPCallResult callResult = callControl.call(callRequest);
      assertEquals("Incorrect status code returned.", 200, callResult.getStatusCode());
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
}
