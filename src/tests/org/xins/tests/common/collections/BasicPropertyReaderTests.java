/*
 * $Id$
 */
package org.xins.tests.common.collections;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;

/**
 * Tests for class <code>BasicPropertyReader</code>.
 *
 * @version $Revision$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 * @author Chris Gilbride (<a href="mailto:chris.gilbride@nl.wanadoo.com">Chris.gilbride@nl.wanadoo.com</a>)
 */
public class BasicPropertyReaderTests extends TestCase {

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
      return new TestSuite(BasicPropertyReaderTests.class);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>BasicPropertyReader</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public BasicPropertyReaderTests(String name) {
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

   /*
    * @see TestCase#tearDown()
    */
   protected void tearDown()
      throws Exception {
      super.tearDown();
   }

   public void testSet() {
      BasicPropertyReader reader = new BasicPropertyReader();
      try {
         reader.set(null, null);
         fail("BasicPropertyReader.set(null, null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      try {
         reader.set(null, "test");
         fail("BasicPropertyReader.set(null, \"test\") should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
      
      String testVal = "value";
      String testName = "first";
      // Check that the value testVal has been set in the BasicPropertyReader
      reader.set(testName, testVal);
      assertEquals(testVal, reader.get(testName));
   }

   public void testRemove() {
      BasicPropertyReader reader = new BasicPropertyReader();
      try {
         reader.remove(null);
         fail("BasicPropertyReader.remove(null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // first add something in the property reader
      String testVal = "value";
      String testName = "first";
      reader.set(testName, testVal);
      // check it's there
      assertEquals(testVal, reader.get(testName));
      // now remove it
      reader.remove(testName);
      assertEquals(null, reader.get(testName));
   }

   public void testGet() {
      BasicPropertyReader reader = new BasicPropertyReader();
      try {
         reader.get(null);
         fail("BasicPropertyReader.get(null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }
   }

   public void testGetNames() {
      BasicPropertyReader reader = new BasicPropertyReader();
      //TODO Implement getNames().
   }

   public void testGetPropertiesMap() {
      BasicPropertyReader reader = new BasicPropertyReader();
      //TODO Implement getPropertiesMap().
   }

}
