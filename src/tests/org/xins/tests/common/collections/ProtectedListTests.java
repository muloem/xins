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

import org.apache.log4j.PropertyConfigurator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.ProtectedList;

/**
 * Tests for class <code>ProtectedList</code>.
 *
 * @version $Revision$ $Date$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class ProtectedListTests extends TestCase {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The secret key for the test protected list.
    */
   private final static Object KEY = new Object();

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

   /*
    * @see TestCase#setUp()
    */
   protected void setUp()
      throws Exception {
      super.setUp();
      Properties settings = new Properties();
      settings.setProperty("log4j.rootLogger",                                "DEBUG, console");
      settings.setProperty("log4j.appender.console",                          "org.apache.log4j.ConsoleAppender");
      settings.setProperty("log4j.appender.console.layout",                   "org.apache.log4j.PatternLayout");
      settings.setProperty("log4j.appender.console.layout.ConversionPattern", "%d %t %-5p [%c] %m%n");
      settings.setProperty("log4j.logger.httpclient.wire",                    "WARN");
      settings.setProperty("log4j.logger.org.apache.commons.httpclient",      "WARN");
      PropertyConfigurator.configure(settings);
   }

   public void testList() {
      ProtectedList list = new ProtectedList(KEY);

      assertEquals("The size of the list is incorrect.", 0, list.size());

      try {
         list.add("hello1");
         fail("BasicPropertyReader.set(null, null) should throw an IllegalArgumentException.");
      } catch (Exception exception) {
         // as expected
      }

      try {
         list.add(KEY, "hello2");
      } catch (Exception exception) {
         fail("BasicPropertyReader.set(null, null) should throw an IllegalArgumentException.");
      }

      try {
         list.add(new Object(), "hello3");
         fail("BasicPropertyReader.set(null, null) should throw an IllegalArgumentException.");
      } catch (Exception exception) {
         // as expected
      }

      assertEquals("The size of the list is incorrect.", 1, list.size());
      assertEquals("The value of the element in the list is incorrect.", "hello2", list.get(0));
   }

}
