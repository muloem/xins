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

import org.xins.common.collections.expiry.ExpiryFolder;
import org.xins.common.collections.expiry.ExpiryStrategy;

/**
 * Tests for class <code>ExpiryFolder</code>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class ExpiryFolderTests extends TestCase {

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
      return new TestSuite(ExpiryFolderTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ExpiryFolderTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ExpiryFolderTests(String name) {
      super(name);
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   // TODO: Stop all expiry strategies globally

   public void testExpiryFolder() throws Throwable {
      final int    DURATION  = 500;
      final int    PRECISION = 100;
      final String NAME      = "TestFolder";

      // Construct an ExpiryStrategy
      ExpiryStrategy strategy = new ExpiryStrategy(DURATION, PRECISION);
      assertEquals(DURATION,             strategy.getTimeOut());
      assertEquals(PRECISION,            strategy.getPrecision());
      assertEquals(DURATION / PRECISION, strategy.getSlotCount());

      // Construct an ExpiryFolder
      ExpiryFolder folder = new ExpiryFolder(NAME, strategy);
      assertEquals(NAME,     folder.getName());
      assertEquals(strategy, folder.getStrategy());

      // Nothing should be in the ExpiryFolder
      final String KEY_1 = "hello";
      final String VAL_1 = "world";
      assertNull(folder.get(KEY_1));
      assertNull(folder.find(KEY_1));

      // Test get, find and put with null values
      try {
         folder.get(null);
         fail("IllegalArgumentException accepted.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         folder.find(null);
         fail("IllegalArgumentException accepted.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         folder.put(KEY_1, null);
         fail("IllegalArgumentException accepted.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         folder.put(null, VAL_1);
         fail("IllegalArgumentException accepted.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         folder.remove(null);
         fail("IllegalArgumentException accepted.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Put something in and make sure it is in there indeed
      folder.put(KEY_1, VAL_1);
      assertEquals(VAL_1, folder.find(KEY_1));
      assertEquals(VAL_1, folder.get(KEY_1));

      // Check expiry
      final String KEY_2 = "something";
      final String VAL_2 = "else";
      folder.put(KEY_2, VAL_2);
      assertEquals(VAL_2, folder.find(KEY_2));
      assertEquals(VAL_2, folder.get(KEY_2));
      Thread.sleep(DURATION + 1);
      assertNull("Entry should have expired.", folder.find(KEY_2));
      assertNull("Entry should have expired.", folder.find(KEY_2));

      // Test entry removal
      folder.put(KEY_2, VAL_2);
      assertEquals(VAL_2, folder.find(KEY_2));
      assertEquals(VAL_2, folder.get(KEY_2));
      assertEquals(VAL_2, folder.remove(KEY_2));
      assertNull(folder.remove(KEY_2));
      assertNull(folder.find(KEY_2));
      assertNull(folder.get(KEY_2));
      assertNull(folder.remove("key that was never entered"));

      // Stop the ExpiryStrategy
      strategy.stop();
   }
}
