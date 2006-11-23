/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.collections;

import java.util.Iterator;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.BasicPropertyReader;

/**
 * Tests for class <code>BasicPropertyReader</code>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:mees.witteman@orange-ftgroup.com">Mees Witteman</a>
 * @author <a href="mailto:chris.gilbride@orange-ftgroup.com">Chris Gilbride</a>
 */
public class BasicPropertyReaderTests extends TestCase {

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
   // Methods
   //-------------------------------------------------------------------------

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
      assertNull(reader.get(testName));
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

   public void testGetNames()
    throws Exception  {
      BasicPropertyReader reader = new BasicPropertyReader();

      // first add something in the property reader
      String testVal = "value 1";
      String testName = "first";
      String testVal2 = "value 2";
      String testName2 = "second";
      reader.set(testName, testVal);
      reader.set(testName2, testVal2);

      // retrieve the iterator
      Iterator it = reader.getNames();
      // does it have any content
      assertTrue(it.hasNext());
      // get the first element
      String n3 = (String) it.next();
      String v3 = reader.get(n3);
      // must have another element
      assertTrue(it.hasNext());
      // get second element
      String n4 = (String) it.next();
      String v4 = reader.get(n4);
      // shouldn't have any more elements
      assertFalse(it.hasNext());

      // check the values returned
      assertTrue( (n3 == testName && n4 == testName2)
               || (n4 == testName && n3 == testName2) );
      if (n3.equals(testName)) {
         assertEquals(v3, testVal);
         assertEquals(v4, testVal2);
      } else {
         assertEquals(v3, testVal2);
         assertEquals(v4, testVal);
      }
   }

   public void testGetPropertiesMap() {
      BasicPropertyReader reader = new BasicPropertyReader();
      //TODO Implement getPropertiesMap().
   }

}
