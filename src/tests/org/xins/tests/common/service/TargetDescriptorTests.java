/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.service;

import java.net.MalformedURLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.service.TargetDescriptor;

/**
 * Tests for class <code>TargetDescriptor</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class TargetDescriptorTests extends TestCase {

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
      return new TestSuite(TargetDescriptorTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>TargetDescriptorTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public TargetDescriptorTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testTargetDescriptor() throws Exception {
      try {
         new TargetDescriptor(null);
         fail("TargetDescriptor(String) should throw an IllegalArgumentException if the argument is null.");
      } catch (IllegalArgumentException ex) {
         // as expected
      }

      String url = "blablabla";
      try {
         new TargetDescriptor(url);
         fail("TargetDescriptor(String) should throw a MalformedURLException if the argument is \"" + url + "\".");
      } catch (MalformedURLException ex) {
         // as expected
      }
   }
}
