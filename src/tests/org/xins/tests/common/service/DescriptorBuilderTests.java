/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.service;

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.InvalidPropertyValueException;
import org.xins.common.collections.MissingRequiredPropertyException;
import org.xins.common.collections.PropertyReader;
import org.xins.common.collections.PropertiesPropertyReader;

import org.xins.common.service.Descriptor;
import org.xins.common.service.DescriptorBuilder;
import org.xins.common.service.GroupDescriptor;
import org.xins.common.service.TargetDescriptor;

/**
 * Tests for class <code>DescriptorBuilder</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class DescriptorBuilderTests extends TestCase {

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
      return new TestSuite(DescriptorBuilderTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>DescriptorBuilderTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public DescriptorBuilderTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   private Properties _properties;
   private PropertyReader _propertyReader;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      _properties     = new Properties();
      _propertyReader = new PropertiesPropertyReader(_properties);
   }

   private void reset() {
      _properties.clear();
   }

   public void testDescriptorBuilder() throws Throwable {

      // Pass null to constructor
      try {
         DescriptorBuilder.build(null, null);
         fail("Expected build(null, null) to throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      try {
         DescriptorBuilder.build(null, "Hello");
         fail("Expected build(null, non-null) to throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      try {
         DescriptorBuilder.build(_propertyReader, null);
         fail("Expected build(non-null, null) to throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      try {
         DescriptorBuilder.build(_propertyReader, "ldap");
         fail("Expected build(non-null, nonexistent-property) to throw an IllegalArgumentException.");
      } catch (MissingRequiredPropertyException exception) { /* as expected */ }

      // Simple example
      String url = "http://somehost.somecompany.com:3003/something/else";
      long timeOut = 1500;
      String base = "server";
      String prop = DescriptorBuilder.TARGET_DESCRIPTOR_TYPE
                  + DescriptorBuilder.DELIMITER
                  + url
                  + DescriptorBuilder.DELIMITER
                  + String.valueOf(timeOut);
      _properties.setProperty(base, prop);
      Descriptor d = DescriptorBuilder.build(_propertyReader, base);
      assertNotNull(d);
      assertEquals(TargetDescriptor.class, d.getClass());
      TargetDescriptor target = (TargetDescriptor) d;
      assertEquals(url,     target.getURL());
      assertEquals(timeOut, target.getTotalTimeOut());

      // GroupDescriptor with no members
      String gprop = DescriptorBuilder.GROUP_DESCRIPTOR_TYPE
                   + DescriptorBuilder.DELIMITER
                   + GroupDescriptor.RANDOM_TYPE;
      _properties.setProperty(base, gprop);
      try {
         DescriptorBuilder.build(_propertyReader, base);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      // GroupDescriptor with no members, with delimiter at end
      gprop += DescriptorBuilder.DELIMITER;
      _properties.setProperty(base, gprop);
      try {
         DescriptorBuilder.build(_propertyReader, base);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      // GroupDescriptor with one non-existing member
      gprop += "t1";
      _properties.setProperty(base, gprop);
      try {
         DescriptorBuilder.build(_propertyReader, base);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }

      // GroupDescriptor with one existing member
      _properties.setProperty(base + ".t1", prop);
      try {
         DescriptorBuilder.build(_propertyReader, base);
         fail("Expected InvalidPropertyValueException.");
      } catch (InvalidPropertyValueException exception) {
         // as expected
      }
   }
}
