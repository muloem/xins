/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.XINSCallRequest;
import org.xins.client.XINSServiceCaller;
import org.xins.client.InvalidRequestException;

import org.xins.common.service.TargetDescriptor;

/**
 * Tests the <code>XINSServiceCaller</code> when it receives an invalid
 * request.
 *
 * @version $Revision$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class InvalidRequestTests extends TestCase {

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
      return new TestSuite(InvalidRequestTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>InvalidRequestTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public InvalidRequestTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The <code>XINSServiceCaller</code> used to call the API. This field is
    * initialized by {@link #setUp()}.
    */
   private XINSServiceCaller _caller;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void setUp() throws Exception {
      String           url    = "http://127.0.0.1:8080/";
      TargetDescriptor target = new TargetDescriptor(url);
      _caller = new XINSServiceCaller(target);
   }

   public void testRequiredParams() throws Exception {

      // Make a call to the SimpleTypes function
      XINSCallRequest request = new XINSCallRequest("SimpleTypes");
      InvalidRequestException exception;
      try {
         _caller.call(request);
         fail("Expected InvalidRequestException.");
         return;
      } catch (InvalidRequestException e) {
         exception = e;
      }

      // Error code must be _InvalidRequest
      assertEquals("_InvalidRequest", exception.getErrorCode());

      // The names of all parameters should be in the exception message
      String[] params = {
         "inputByte", "inputInt", "inputLong", "inputFloat", "inputText"
      };
      String exDetail = exception.getDetail();
      for (int i = 0; i < params.length; i++) {
         String p     = params[i];
         String error = "Expected InvalidRequestException detail (\""
                      + exDetail
                      + "\") to contain a reference to missing required "
                      + "parameter \""
                      + p
                      + "\".";
         String find = "No value given for required parameter \""
                     + p
                     + "\".";
         assertTrue(error, exDetail.indexOf(find) >= 0);
      }
   }

   public void testTypedParams() throws Exception {

      // Make a call to the ParamComboNotAll function
      XINSCallRequest request = new XINSCallRequest("ParamComboNotAll");
      request.setParameter("param1", "a");
      request.setParameter("param2", "a");
      request.setParameter("param3", "a");
      InvalidRequestException exception;
      try {
         _caller.call(request);
         fail("Expected InvalidRequestException.");
         return;
      } catch (InvalidRequestException e) {
         exception = e;
      }

      // Error code must be _InvalidRequest
      assertEquals("_InvalidRequest", exception.getErrorCode());

      // The names of all parameters should be in the exception message
      String[] params = { "param1", "param2", "param3" };
      String exDetail = exception.getDetail();
      for (int i = 0; i < params.length; i++) {
         String p     = params[i];
         String error = "Expected InvalidRequestException detail (\""
                      + exDetail
                      + "\") to contain a reference to parameter \""
                      + p
                      + "\" which has an invalid value.";
         String find = "The value for parameter \""
                     + p
                     + "\" is considered invalid";
         assertTrue(error, exDetail.indexOf(find) >= 0);
      }
   }

   public void testParamCombo() throws Exception {

      // Make a call to the ParamComboNotAll function
      XINSCallRequest request = new XINSCallRequest("ParamComboNotAll");
      request.setParameter("param1", "1");
      request.setParameter("param2", "1");
      request.setParameter("param3", "1");
      request.setParameter("param4", "1");
      InvalidRequestException exception;
      try {
         _caller.call(request);
         fail("Expected InvalidRequestException.");
         return;
      } catch (InvalidRequestException e) {
         exception = e;
      }

      // Error code must be _InvalidRequest
      assertEquals("_InvalidRequest", exception.getErrorCode());

      // The names of all parameters should be in the exception message
      String exDetail = exception.getDetail();
      String find = "Violated param-combo constraint of type \"not-all\" on"
                  + " parameters"
                  + " \"param1\", \"param2\", \"param3\" and \"param4\".";
      String error = "Expected InvalidRequestException detail (\""
                   + exDetail
                   + "\") to contain the following string: "
                   + find;
      assertTrue(error, exDetail.indexOf(find) >= 0);
   }
}
