/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.util.service;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.service.UnsupportedProtocolException;

/**
 * Tests for class <code>UnsupportedProtocolException</code>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class UnsupportedProtocolExceptionTests extends TestCase {

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
      return new TestSuite(UnsupportedProtocolExceptionTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>UnsupportedProtocolExceptionTests</code> test 
    * suite with the specified name. The name will be passed to the 
    * superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public UnsupportedProtocolExceptionTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testContructor() throws Throwable {
      try {
         UnsupportedProtocolException upe = new UnsupportedProtocolException(null);
         fail("UnsupportedProtocolException did not throw an exception with a <null> argument for the constructor.");
      } catch (IllegalArgumentException ex) {
         // As excepted
      }
      TargetDescriptor descriptor = new TargetDescriptor("http://www.test.org");
      UnsupportedProtocolException upe = new UnsupportedProtocolException(descriptor);
      assertEquals("The descriptor target returned is not the same as the one passed to the constructor.", descriptor, upe.getTargetDescriptor());
   }
}
