/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections.expiry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.expiry.ExpiryStrategy;

/**
 * Tests for class <code>ExpiryStrategy</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class ExpiryStrategyTests extends TestCase {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

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
      return new TestSuite(ExpiryStrategyTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ExpiryStrategyTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ExpiryStrategyTests(String name) {
      super(name);
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testExpiryStrategy() throws Throwable {
      try {
         new ExpiryStrategy(0L, 0L);
         fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         new ExpiryStrategy(1L, 0L);
         fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         new ExpiryStrategy(0L, 1L);
         fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         new ExpiryStrategy(1L, 2L);
         fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      ExpiryStrategy strategy;
      long timeOut;
      long precision;

      timeOut   = 2000L;
      precision = 1001L;
      strategy  = new ExpiryStrategy(timeOut, precision);
      assertEquals(timeOut,   strategy.getTimeOut());
      assertEquals(precision, strategy.getPrecision());
      assertEquals(2, strategy.getSlotCount());

      timeOut   = 2000L;
      precision = 1000L;
      strategy  = new ExpiryStrategy(timeOut, precision);
      assertEquals(timeOut,   strategy.getTimeOut());
      assertEquals(precision, strategy.getPrecision());
      assertEquals(2, strategy.getSlotCount());

      timeOut   = 2000L;
      precision = 999L;
      strategy  = new ExpiryStrategy(timeOut, precision);
      assertEquals(timeOut,   strategy.getTimeOut());
      assertEquals(precision, strategy.getPrecision());
      assertEquals(3, strategy.getSlotCount());
   }
}
