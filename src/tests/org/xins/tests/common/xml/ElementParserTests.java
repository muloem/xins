/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.xml;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.xml.ElementParser;
import org.xins.common.xml.Element;

/**
 * Tests for class <code>ElementParserTests</code>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class ElementParserTests extends TestCase {

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
      return new TestSuite(ElementParserTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ElementParserTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ElementParserTests(String name) {
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
    * Tests the <code>ElementParser</code> class.
    */
   public void testElementParser() throws Exception {
      String s = "<a xmlns=\"b\" c='2'/>";
      StringReader in = new StringReader(s);

      ElementParser parser = new ElementParser();

      Element element = parser.parse(in);
      assertNotNull(element);
      assertEquals("a",  element.getLocalName());
      assertEquals("b",  element.getNamespaceURI());
      assertEquals(1,    element.getAttributeMap().size());
      assertEquals("2",  element.getAttribute("c"));
      assertEquals(0,    element.getChildElements().size());
      assertEquals(null, element.getText());
   }
}
