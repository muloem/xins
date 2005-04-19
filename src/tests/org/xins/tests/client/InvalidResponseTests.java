/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import com.mycompany.allinone.capi.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.client.UnacceptableResultXINSCallException;

import org.xins.common.service.*;


import org.xins.common.servlet.container.HTTPServletHandler;
import org.xins.common.types.standard.Date;
import org.xins.common.types.standard.Timestamp;

/**
 * Tests the CAPI when it receives invalid result.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class InvalidResponseTests extends TestCase {
   
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
      return new TestSuite(InvalidResponseTests.class);
   }

   
   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------
   
   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------
   
   /**
    * Constructs a new <code>InvalidResponseTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public InvalidResponseTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
   
   /**
    * The <code>CAPI</code> object used to call the API. This field is
    * initialized by {@link #setUp()}.
    */
   private CAPI _capi;
   private HTTPServletHandler httpServer2;

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void setUp() throws Exception {
      httpServer2 = new HTTPServletHandler("org.xins.tests.client.InvalidResponseServlet", 8081, true);
      TargetDescriptor target = new TargetDescriptor("http://127.0.0.1:8081/", 5000, 1000, 4000);
      _capi   = new CAPI(target);
   }

   public void testMissingParameter() throws Exception {
      try {
         SimpleTypesResult result = _capi.callSimpleTypes((byte)8, null, 65, 88L, 72.5f, new Double(37.2),
            "test", null, null, Date.fromStringForRequired("20041213"), Timestamp.fromStringForOptional("20041225153222"), null);
         fail("The result is invalid, the function should throw an UnacceptableResultXINSCallException exception");
      } catch (UnacceptableResultXINSCallException exception) {
         // as expected
      } catch (Exception exception) {
         fail("The result is invalid, the function should throw an UnacceptableResultXINSCallException exception");
      }
   }
   
   public void tearDown() throws Exception {
      httpServer2.close();
   }
}
