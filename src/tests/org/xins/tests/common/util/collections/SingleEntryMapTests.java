/*
 * $Id$
 */
package org.xins.tests.common.util.collections;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.util.collections.SingleEntryMap;

/**
 * Tests for class <code>SingleEntryMapTests</code>.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class SingleEntryMapTests extends TestCase {

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
      return new TestSuite(SingleEntryMapTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>SingleEntryMapTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public SingleEntryMapTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      // empty
   }

   private void reset() {
      // empty
   }

   /**
    * Tests if creating a new Map fits the specifications.
    */
   public void testCreation() throws Throwable {

      String key = "test key";
      String value = "test value";
      String key2 = "test key2";
      String value2 = "test value2";
      
      // Test that a null argument fails for the key
      try {
         SingleEntryMap entry1 = new SingleEntryMap(null, value);
      } catch (NullPointerException exception) { 
         fail("A null key for the SingleEntryMap should be possible.");
      }

      // Test that a null argument for a value is OK
      try {
         SingleEntryMap entry2 = new SingleEntryMap(key, null);
      } catch (NullPointerException exception) {
         fail("A null value for the SingleEntryMap should be possible.");
      }
   }

   /**
    * Tests if the content of the Map is as expected.
    */
   public void testMapContent() throws Throwable {
    
      String key = "test key";
      String value = "test value";
      String key2 = "test key2";
      String value2 = "test value2";
      
      // Test the content of a SingleEntryMap 
      SingleEntryMap entry3 = new SingleEntryMap(key, value);
      assertTrue("The map has a different size than one.", entry3.size() == 1);
      assertTrue("The map does not contain the same key as expected.", entry3.containsKey("test key"));
      assertFalse("The map found a key that should be there.", entry3.containsKey(key2));
      assertTrue("The map does not contain the same value as expected.", entry3.containsValue("test value"));
      assertFalse("The map found a value that should be there.", entry3.containsValue(value2));

      assertTrue("The map does not contain the same value as expected.", entry3.get(key).equals(value));
      assertTrue("The map get a value from a not existing key.", entry3.get(key2) == null);
      
      entry3.put(key2, value2);
      
      assertTrue("The map has a different size than one.", entry3.size() == 1);
      assertTrue("The map does not contain the same key as expected.", entry3.containsKey(key2));
      assertFalse("The map found a key that should be there.", entry3.containsKey(key));
      assertTrue("The map does not contain the same value as expected.", entry3.containsValue(value2));
      assertFalse("The map found a value that should be there.", entry3.containsValue(value));

      assertTrue("The map does not contain the same value as expected.", entry3.get(key2).equals(value2));
      assertTrue("The map get a value from a not existing key.", entry3.get(key) == null);
   }
}
