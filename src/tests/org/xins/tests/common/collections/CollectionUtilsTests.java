/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.common.collections.ChainedMap;

import org.xins.common.collections.CollectionUtils;

/**
 * Tests for class <code>CollectionUtils</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class CollectionUtilsTests extends TestCase {

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
      return new TestSuite(CollectionUtilsTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CollectionUtilsTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public CollectionUtilsTests(String name) {
      super(name);
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected void setUp() throws Exception {
      // empty
   }

   protected void tearDown() throws Exception {
      // empty
   }

   public void testCollectionUtils() {

      // TODO: Test list(Iterator)

      final String ARG_NAME = "argumentName";

      // Test list(String, Object[], int)
      try {
         CollectionUtils.list(null, null, 0);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {}
      try {
         CollectionUtils.list(ARG_NAME, null, 0);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {}
      try {
         CollectionUtils.list(null, new Object[0], 0);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {}
      try {
         CollectionUtils.list(ARG_NAME, new Object[0], 1);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {}

      // Empty array
      ArrayList list = CollectionUtils.list(ARG_NAME, new Object[0], 0);
      assertNotNull(list);
      assertEquals(0, list.size());

      // Non-empty array
      Object[] array = new Object[] { "a", "b", "c" };
      list = CollectionUtils.list(ARG_NAME, array, 3);
      assertNotNull(list);
      assertEquals(3, list.size());
      assertEquals("a", list.get(0));
      assertEquals("b", list.get(1));
      assertEquals("c", list.get(2));

      // Test maximum size again
      try {
         CollectionUtils.list(ARG_NAME, array, 4);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {}

      // Null value in array
      array[2] = null;
      try {
         CollectionUtils.list(ARG_NAME, array, 0);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {}

      // Duplicate value in array
      array[2] = array[0];
      try {
         CollectionUtils.list(ARG_NAME, array, 0);
      } catch (IllegalArgumentException exception) {}
   }
}
