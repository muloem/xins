/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections.expiry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.expiry.ExpiryFolder;
import org.xins.common.collections.expiry.ExpiryListener;
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
      assertNull(folder.remove(KEY_1));
      folder.put(KEY_2, VAL_2);
      assertEquals(VAL_2, folder.find(KEY_2));
      assertEquals(VAL_2, folder.get(KEY_2));
      assertEquals(VAL_2, folder.remove(KEY_2));
      assertNull(folder.remove(KEY_2));
      assertNull(folder.find(KEY_2));
      assertNull(folder.get(KEY_2));
      assertNull(folder.remove("This is a key that was never entered"));
      assertEquals(0, folder.size());

      // Test addition and retrieval of listener
      try {
         folder.addListener(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         folder.removeListener(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      ExpiryFolderListener listener = new ExpiryFolderListener();
      folder.removeListener(listener);

      // Make sure the listeners notified by the ExpiryStrategy will take some
      // time
      ExpiryFolder folder2 = new ExpiryFolder("SomeName", strategy);
      folder2.put(KEY_2, VAL_2);
      folder2.addListener(new TimeEater(DURATION / 2L));
      Thread.sleep(DURATION - PRECISION);

      // Test detailed expiry behavior
      folder.addListener(listener);
      folder.removeListener(listener);
      folder.addListener(listener);
      assertEquals(0, folder.size());
      folder.put(KEY_2, VAL_2);
      assertEquals(1, folder.size());
      final long WAIT_TIME = DURATION * 2L;
      long before = System.currentTimeMillis();
      Thread.sleep(WAIT_TIME);
      assertNull(folder.get(KEY_2));
      assertEquals(0, folder.size());
      long after = System.currentTimeMillis();
      long passed = after - before;

      // Listener should have been called exactly once
      assertEquals(1, listener._callbacks.size());

      // Get the one and only Callback object
      Callback cb = (Callback) listener._callbacks.get(0);

      // Source ExpiryFolder should match
      assertTrue("ExpiryFolder passed to listener mismatches real source.",
                 cb._folder == folder);

      // The map should contain expected key/value pair, nothing else
      Map expired = cb._expired;
      assertNotNull(expired);
      assertTrue(expired.size() == 1);
      assertEquals(VAL_2, expired.get(KEY_2));

      Iterator it = expired.keySet().iterator();
      assertTrue(it.hasNext());
      assertEquals(KEY_2, it.next());
      assertFalse(it.hasNext());

      // The entry should have been expired in the right time frame
      long callbackTime = cb._timeStamp - before;
      String message = "Entry expired in "
                     + callbackTime
                     + " ms while folder time-out is "
                     + DURATION
                     + " ms and precision is "
                     + PRECISION
                     + " ms.";
      assertTrue(message, callbackTime >= DURATION);
      assertTrue(message, callbackTime <= (DURATION + PRECISION));

      // Stop the ExpiryStrategy
      strategy.stop();
   }

   /**
    * Listener for an ExpiryFolder.
    */
   private class ExpiryFolderListener implements ExpiryListener {

      private ExpiryFolderListener() {
         _callbacks = new ArrayList();
      }

      private final List _callbacks;

      public void expired(ExpiryFolder folder, Map expired) {

         // Create Callback object
         Callback cb   = new Callback();
         cb._timeStamp = System.currentTimeMillis();
         cb._folder    = folder;
         cb._expired   = expired;

         // Store the Callback
         _callbacks.add(cb);
      }
   }

   private class TimeEater implements ExpiryListener {

      private TimeEater(long sleepTime) {
         _sleepTime = sleepTime;
      }

      private final long _sleepTime;

      public void expired(ExpiryFolder folder, Map expired) {

        folder.put(new Object(), new Object());

        try {
           Thread.sleep(_sleepTime);
        } catch (InterruptedException exception) {
           // XXX: ignore
        }
      }
   }

   private class Callback
   extends Object {

      private long _timeStamp;
      private ExpiryFolder _folder;
      private Map _expired;
   }
}
