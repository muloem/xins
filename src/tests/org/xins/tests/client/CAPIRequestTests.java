/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import com.mycompany.allinone.capi.DefinedTypesRequest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class tests the generated CAPI Request object.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class CAPIRequestTests extends TestCase {

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
      return new TestSuite(CAPIRequestTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CAPIRequestTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public CAPIRequestTests(String name) {
      super(name);
   }

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Tests that the generated get methods return the last value set.
    */
   public void testGetMethods() {
      DefinedTypesRequest request1 = new DefinedTypesRequest();
      assertNull("Incorrect initial value for age in the CAPI request", request1.getInputAge());

      request1.setInputAge((byte)8);
      assertEquals("The age value returned by the request is not the same as the one set.",
            8, request1.getInputAge().intValue());

      request1.setInputAge(null);
      assertNull("Incorrect reseted value for age in the CAPI request", request1.getInputAge());
   }
}
