/*
 * $Id$
 */
package org.xins.tests.client;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.PropertyReader;

import org.xins.client.DataElement;
import org.xins.client.XINSCallResultData;
import org.xins.client.XINSCallResultParser;

/**
 * Tests for class <code>XINSCallResultParser</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class XINSCallResultParserTests extends TestCase {

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
      return new TestSuite(XINSCallResultParserTests.class);
   }

   /**
    * Determines the number of elements returned by the specified
    * <code>Iterator</code>.
    *
    * @param iterator
    *    the {@link Iterator} to determine the number of elements in, or
    *    <code>null</code>.
    *
    * @return
    *    the number of elements in the {@link Iterator}, or <code>0</code> if
    *    <code>iterator == null</code>.
    */
   private static int iteratorSize(Iterator iterator) {

      // Short-circuit if argument is null
      if (iterator == null) {
         return 0;
      }

      // Loop through the elements
      int count = 0;
      while (iterator.hasNext()) {
         iterator.next();
         count++;
      }

      return count;
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>XINSCallResultParserTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public XINSCallResultParserTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testParseXINSCallResultData() throws Throwable {

      // Prepare the string to parse
      final String encoding = "UTF-8";
      final String xml = "<?xml version=\"1.0\" encoding='" + encoding + "' ?>" +
                         " <result><data>" +
                         "<product available='false' name=\"FOO\" />" + 
                         " <product available=\"true\"  name=\"BAR\" />" +
                         "</data></result>";
      final byte[] bytes = xml.getBytes(encoding);

      // Parse
      XINSCallResultParser parser = new XINSCallResultParser();
      XINSCallResultData result = parser.parse(bytes);

      // The parser should not return null
      assertNotNull(result);

      // There should be no error code
      assertNull(result.getErrorCode());

      // There should be no parameters
      PropertyReader params = result.getParameters();
      assertTrue(params == null || params.size() == 0);

      // There should be a data section
      DataElement dataElement = result.getDataElement();
      assertNotNull(dataElement);

      // Root element should be <data/>
      assertEquals("data", dataElement.getName());

      // There should be no attributes in the root element
      Iterator dataElementAttributes = dataElement.getAttributes();
      assertEquals(0, iteratorSize(dataElementAttributes));

      // There should be 2 child elements
      Iterator children = dataElement.getChildren();
      assertEquals(2, iteratorSize(children));

      // Get both child elements
      children = dataElement.getChildren();
      DataElement childOne = (DataElement) children.next();
      DataElement childTwo = (DataElement) children.next();

      // First element should have 2 attributes
      assertEquals(2, iteratorSize(childOne.getAttributes()));

      // There should be an 'available' attribute and a 'name' attribute
      Iterator childOneAttributes = childOne.getAttributes();
      String attrOne1 = (String) childOneAttributes.next();
      String attrOne2 = (String) childOneAttributes.next();
      assertTrue(attrOne1.equals("available") || attrOne1.equals("name"));
      assertTrue(attrOne2.equals("available") || attrOne2.equals("name"));
      assertFalse(attrOne1.equals(attrOne2));

      // The 'name' attribute must be 'FOO'
      assertEquals("FOO", childOne.get("name"));

      // The 'available' attribute must be 'false'
      assertEquals("false", childOne.get("available"));

      // Second element should have 2 attributes
      assertEquals(2, iteratorSize(childTwo.getAttributes()));

      // There should be an 'available' attribute and a 'name' attribute
      Iterator childTwoAttributes = childTwo.getAttributes();
      String attrTwo1 = (String) childTwoAttributes.next();
      String attrTwo2 = (String) childTwoAttributes.next();
      assertTrue(attrTwo1.equals("available") || attrTwo1.equals("name"));
      assertTrue(attrTwo2.equals("available") || attrTwo2.equals("name"));
      assertFalse(attrTwo1.equals(attrTwo2));

      // The 'name' attribute must be 'BAR'
      assertEquals("BAR", childTwo.get("name"));

      // The 'available' attribute must be 'false'
      assertEquals("true", childTwo.get("available"));
   }
}
