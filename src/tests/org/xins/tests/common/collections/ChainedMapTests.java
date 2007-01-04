/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.ChainedMap;

/**
 * Tests for the <code>ChainedMap</code> class.
 *
 * @version $Revision$
 * @author <a href="mailto:anthony.goubard@orange-ftgroup.com">Anthony Goubard</a>
 */
public class ChainedMapTests extends TestCase {

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
      return new TestSuite(ChainedMapTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ChainedMapTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ChainedMapTests(String name) {
      super(name);
   }

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testChainedMap() {
      int itemsCount = 20;
      Random rd = new Random();
      List arrayList = new ArrayList(itemsCount);
      Map sortedMap = new ChainedMap();
      for (int i = 0; i < itemsCount; i++) {
         byte[] randomText = new byte[8];
         rd.nextBytes(randomText);
         String randomString  = new String(randomText);
         if (arrayList.contains(randomString)) {
            i--;
            continue;
         }
         arrayList.add(randomString);
         sortedMap.put(randomString, "value" + i);
      }
      Iterator itSortedMap = sortedMap.values().iterator();
      int i = 0;
      while (itSortedMap.hasNext()) {
         String nextValue = (String) itSortedMap.next();
         assertEquals("value" + i, nextValue);
         i++;
      }
      assertEquals(itemsCount, sortedMap.size());
   }
}
