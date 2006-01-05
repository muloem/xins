/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections;

import java.util.ArrayList;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.IncorrectSecretKeyException;
import org.xins.common.collections.ProtectedList;

/**
 * Tests for class <code>ProtectedList</code>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class ProtectedListTests extends TestCase {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The secret key for the test protected list.
    */
   private final static Object SECRET_KEY = new Object();


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
      return new TestSuite(ProtectedListTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ProtectedList</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ProtectedListTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testProtectedList() {

      // Construct a ProtectedList with null as secret key (should fail)
      try {
         new ProtectedList(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         new ProtectedList(null, 15);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      try {
         new ProtectedList(null, new ArrayList());
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Construct a ProtectedList with the secret key
      ProtectedList list = new ProtectedList(SECRET_KEY);
      assertEquals(0, list.size());

      // Try unsupported standard add-operation
      try {
         list.add("hello1");
         fail("Expected UnsupportedOperationException.");
      } catch (UnsupportedOperationException exception) {
         // as expected
      }

      // Add using secret key should succeed
      list.add(SECRET_KEY, "hello2");
      assertEquals(1,        list.size());
      assertEquals("hello2", list.get(0));
      assertEquals(0,        list.indexOf("hello2"));
      assertEquals(0,        list.lastIndexOf("hello2"));
      assertTrue(list.contains("hello2"));

      // Add using incorrect secret key should fail
      try {
         list.add(null, "hello3");
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      try {
         list.add(new Object(), "hello3");
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      assertEquals(1,        list.size());
      assertEquals("hello2", list.get(0));
      assertEquals(0,        list.indexOf("hello2"));
      assertEquals(0,        list.lastIndexOf("hello2"));
      assertTrue(list.contains("hello2"));
      assertFalse(list.contains("hello3"));

      // Try removing with incorrect secret key
      try {
         list.remove(null, -1);
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }
      try {
         list.remove(new Object(), -1);
         fail("Expected IncorrectSecretKeyException.");
      } catch (IncorrectSecretKeyException exception) {
         // as expected
      }

      // Try removing with correct secret key, but invalid index
      try {
         list.remove(SECRET_KEY, -1);
         fail("Expected IndexOutOfBoundsException.");
      } catch (IndexOutOfBoundsException exception) {
         // as expected
      }
      try {
         list.remove(SECRET_KEY, 1);
         fail("Expected IndexOutOfBoundsException.");
      } catch (IndexOutOfBoundsException exception) {
         // as expected
      }
      assertEquals(1,        list.size());
      assertEquals("hello2", list.get(0));
      assertEquals(0,        list.indexOf("hello2"));
      assertEquals(0,        list.lastIndexOf("hello2"));
      assertTrue(list.contains("hello2"));
      assertFalse(list.contains("hello3"));

      // Really remove
      list.remove(SECRET_KEY, 0);
      assertEquals(0,        list.size());
      assertEquals(-1,       list.indexOf("hello2"));
      assertEquals(-1,       list.lastIndexOf("hello2"));
      assertFalse(list.contains("hello2"));
      assertFalse(list.contains("hello3"));
   }
}
