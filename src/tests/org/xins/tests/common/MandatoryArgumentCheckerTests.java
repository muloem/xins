/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Tests for class <code>MandatoryArgumentChecker</code>
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class MandatoryArgumentCheckerTests extends TestCase {

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
      return new TestSuite(MandatoryArgumentCheckerTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>MandatoryArgumentCheckerTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public MandatoryArgumentCheckerTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      // empty
   }

   private void reset() {
      // empty
   }

   /**
    * Tests the simple check.
    */
   public void testOneArgument() throws Throwable {
      MandatoryArgumentChecker.check("hello", "world");
      try {
         MandatoryArgumentChecker.check("hello", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check(null, "world");
         fail("The MandatoryArgumentChecker did not throw an exception when a null name was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check(null, null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null name and value were passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
   }

   /**
    * Tests the check method that accept multiple parameters.
    */
   public void testMultiArguments() throws Throwable {

      // Two parameters
      MandatoryArgumentChecker.check("hello", "world", "hello", "you!");
       try {
         MandatoryArgumentChecker.check("hello", "world", null, "you!");
         fail("The MandatoryArgumentChecker did not throw an exception when a null name was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", "world", "hello", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", "you!");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check(null, null, null, null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null name and value were passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Three parameters
      MandatoryArgumentChecker.check("hello", "world", "hello", "you!", "hi", "me");
      try {
         MandatoryArgumentChecker.check("hello", "world", "hello", null, "hi", "me");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", null, "hi", "me");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", "you!", "hi", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", null, "hi", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Four parameters
      MandatoryArgumentChecker.check("hello", "world", "hello", "you!", "hi", "me", "bonjour", "tout le monde");
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", "you!", "hi", "me", "bonjour", "tout le monde");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", "world", "hello", null, "hi", "me", "bonjour", "tout le monde");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", "you!", "hi", null, "bonjour", "tout le monde");
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", "world", "hello", null, "hi", "me", "bonjour", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", null, "hi", "me", "bonjour", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         MandatoryArgumentChecker.check("hello", null, "hello", null, "hi", null, "bonjour", null);
         fail("The MandatoryArgumentChecker did not throw an exception when a null value was passed.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
   }
}
