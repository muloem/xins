/*
 * $Id$
 */
package org.xins.tests.common.util.service;

import java.util.Properties;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
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

   public void testBuild() throws Throwable {

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
      _properties.setProperty(base, DescriptorBuilder.SERVICE_DESCRIPTOR_TYPE + DescriptorBuilder.DELIMITER + url + DescriptorBuilder.DELIMITER + String.valueOf(timeOut));
      Descriptor d = DescriptorBuilder.build(_propertyReader, base);
      assertNotNull(d);
      assertEquals(TargetDescriptor.class, d.getClass());
      TargetDescriptor target = (TargetDescriptor) d;
      assertEquals(url,     target.getURL());
      assertEquals(timeOut, target.getTimeOut());
   }
}
