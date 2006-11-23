/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.manageable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.manageable.InitializationException;

/**
 * Tests for class <code>InitializationException</code>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class InitializationExceptionTests extends TestCase {

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
      return new TestSuite(InitializationExceptionTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>InitializationExceptionTests</code>
    * test suite with the specified name. The name will be passed to the
    * superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public InitializationExceptionTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Tests the <code>InitializationException</code> class.
    */
   public void testInitializationException() throws Exception {
      new InitializationException(null, null);
   }
}
