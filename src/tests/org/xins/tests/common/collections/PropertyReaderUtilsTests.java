/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Iterator;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;
import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertyReaderUtils;

import org.xins.logdoc.LogdocStringBuffer;

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

   public void testPropertyReaderUtils_getIntProperty()
   throws Exception {

      BasicPropertyReader r1 = new BasicPropertyReader();
      PropertyReader      r0 = r1;

      try {
         PropertyReaderUtils.getIntProperty(null, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(r0, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(null, "propertyName");
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      final long MIN_VALUE           = (long) Integer.MIN_VALUE;
      final long MAX_VALUE           = (long) Integer.MAX_VALUE;
      final long LESS_THAN_MIN_VALUE = MIN_VALUE - 1L;
      final long MORE_THAN_MAX_VALUE = MAX_VALUE + 1L;

      r1.set("a", null);
      r1.set("b", "");
      r1.set("c", "-1");
      r1.set("d", "0");
      r1.set("e", "1");
      r1.set("f", Long.toString(MIN_VALUE));
      r1.set("g", Long.toString(MAX_VALUE));
      r1.set("h", Long.toString(LESS_THAN_MIN_VALUE));
      r1.set("i", Long.toString(MORE_THAN_MAX_VALUE));
      r1.set("j", "a");

      try {
         PropertyReaderUtils.getIntProperty(r0, "unsetProperty");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(r0, "a");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(r0, "b");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      assertEquals(-1, PropertyReaderUtils.getIntProperty(r0, "c"));
      assertEquals(0,  PropertyReaderUtils.getIntProperty(r0, "d"));
      assertEquals(1,  PropertyReaderUtils.getIntProperty(r0, "e"));
      assertEquals(Integer.MIN_VALUE, PropertyReaderUtils.getIntProperty(r0, "f"));
      assertEquals(Integer.MAX_VALUE, PropertyReaderUtils.getIntProperty(r0, "g"));

      try {
         PropertyReaderUtils.getIntProperty(r0, "h");
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(r0, "i");
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getIntProperty(r0, "j");
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }
   }

   public void testPropertyReaderUtils_getRequiredProperty()
   throws Exception {

      BasicPropertyReader r1 = new BasicPropertyReader();
      PropertyReader      r0 = r1;

      try {
         PropertyReaderUtils.getRequiredProperty(null, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getRequiredProperty(r0, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getRequiredProperty(null, "propertyName");
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      r1.set("a", null);
      r1.set("b", "");
      r1.set("c", "value");

      try {
         PropertyReaderUtils.getRequiredProperty(r0, "unsetProperty");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getRequiredProperty(r0, "a");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.getRequiredProperty(r0, "b");
         fail("Expected MissingRequiredPropertyException.");
      } catch (MissingRequiredPropertyException exception) {
         // as expected
      }

      assertEquals("value", PropertyReaderUtils.getRequiredProperty(r0, "c"));
   }

   public void testPropertyReaderUtils_createPropertyReader()
   throws Exception {

      try {
         PropertyReaderUtils.createPropertyReader(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      InputStream in0 = new ByteArrayInputStream("".getBytes("US-ASCII"));
      InputStream in1 = new ByteArrayInputStream("a=\nb= \nc=1\nd=1\ne=2\nf=3 \n\n\t\n".getBytes("US-ASCII"));

      PropertyReader r = PropertyReaderUtils.createPropertyReader(in0);
      assertEquals(0, r.size());

      r = PropertyReaderUtils.createPropertyReader(in1);
      assertEquals(4, r.size());

      assertEquals(null, r.get("a"));
      assertEquals(null, r.get("b"));
      assertEquals("1",  r.get("c"));
      assertEquals("1",  r.get("d"));
      assertEquals("2",  r.get("e"));
      assertEquals("3 ", r.get("f"));
   }

   /**
    * Tests the method
    * {PropertyReaderUtils#serialize(PropertyReader,LogdocStringBuffer)}.
    */
   public void testPropertyReaderUtils_serialize1()
   throws Exception {

      BasicPropertyReader r1 = new BasicPropertyReader();
      PropertyReader      r0 = r1;

      try {
         PropertyReaderUtils.serialize((PropertyReader)     null,
                                       (LogdocStringBuffer) null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         PropertyReaderUtils.serialize(r0, (LogdocStringBuffer) null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      LogdocStringBuffer buffer = new LogdocStringBuffer(20);
      assertEquals("", buffer.toString());

      try {
         PropertyReaderUtils.serialize(null, buffer);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      PropertyReaderUtils.serialize(r0, buffer);
      assertEquals("", buffer.toString());

      r1.set("a", "1");
      PropertyReaderUtils.serialize(r0, buffer);
      assertEquals("a=1", buffer.toString());

      buffer = new LogdocStringBuffer(20);
      r1.set("b", "2");
      PropertyReaderUtils.serialize(r0, buffer);
      String s = buffer.toString();
      String option1 = "a=1&b=2";
      String option2 = "b=2&a=1";
      if (! (option1.equals(s) || option2.equals(s))) {
         fail("Serialized form should be either \""
             + option1
             + "\" or \""
             + option2
             + "\".");
      }
   }
}
