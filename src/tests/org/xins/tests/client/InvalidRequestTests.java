/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
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

   public void testInvalidRequest_RequiredParams() throws Exception {
      XINSCallRequest request = new XINSCallRequest("SimpleTypes");
      InvalidRequestException exception;
      try {
         _caller.call(request);
         fail("Expected InvalidRequestException.");
         return;
      } catch (InvalidRequestException e) {
         exception = e;
      }

      assertEquals("_InvalidRequest", exception.getErrorCode());
      String[] required = {
         "inputByte", "inputInt", "inputLong", "inputFloat", "inputText"
      };

      String exDetail = exception.getDetail();
      for (int i = 0; i < required.length; i++) {
         String p     = required[i];
         String error = "Expected InvalidRequestException detail (\""
                      + exDetail
                      + "\" to contain reference to missing required "
                      + "parameter \""
                      + p
                      + "\".";
         assertTrue(error, exDetail.indexOf(p) >= 0);
      }
   }
}
