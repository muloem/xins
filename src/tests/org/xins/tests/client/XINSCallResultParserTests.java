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
                         "<product available='true' name=\"FOO\" />" + 
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

      // First element should have 2 attributes
      children = dataElement.getChildren();
      DataElement child1 = (DataElement) children.next();
      assertEquals(2, iteratorSize(child1.getAttributes()));

      // TODO

      // Second element should have 2 attributes
      DataElement child2 = (DataElement) children.next();
      assertEquals(2, iteratorSize(child2.getAttributes()));

      // TODO
   }
}
