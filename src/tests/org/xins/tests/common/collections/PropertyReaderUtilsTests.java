/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections;

import java.lang.Exception;
import java.util.Iterator;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;

/**
 * Tests for class <code>PropertyReaderUtils</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class PropertyReaderUtilsTests extends TestCase {

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
      return new TestSuite(PropertyReaderUtilsTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>PropertyReaderUtilsTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public PropertyReaderUtilsTests(String name) {
      super(name);
   }

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testPropertyReaderUtils_EMPTY_PROPERTY_READER() {
      PropertyReader r = PropertyReaderUtils.EMPTY_PROPERTY_READER;
      assertNotNull(r);
      assertEquals(0, r.size());
   }

   public void testPropertyReaderUtils_getBooleanProperty()
   throws Exception {

      BasicPropertyReader r1 = new BasicPropertyReader();
      PropertyReader      r0 = r1;

      try {
         PropertyReaderUtils.getBooleanProperty(null, null, false);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(null, null, true);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(r0, null, false);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(r0, null, true);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(null, "propertyName", false);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(null, "propertyName", true);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      r1.set("a", "true");
      r1.set("b", "false");
      r1.set("c", "");
      r1.set("d", null);
      r1.set("e", "something");
      r1.set("f", "TRUE");

      assertEquals(true,  PropertyReaderUtils.getBooleanProperty(r0, "a", false));
      assertEquals(false, PropertyReaderUtils.getBooleanProperty(r0, "b", true));
      assertEquals(true,  PropertyReaderUtils.getBooleanProperty(r0, "c", true));
      assertEquals(false, PropertyReaderUtils.getBooleanProperty(r0, "c", false));
      assertEquals(true,  PropertyReaderUtils.getBooleanProperty(r0, "d", true));
      assertEquals(false, PropertyReaderUtils.getBooleanProperty(r0, "d", false));

      try {
         PropertyReaderUtils.getBooleanProperty(r0, "e", true);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(r0, "e", false);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(r0, "f", true);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getBooleanProperty(r0, "f", false);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }
   }
}
