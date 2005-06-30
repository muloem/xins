/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.xml;

import java.io.StringReader;
import java.util.List;

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
    * Tests the <code>ElementParser</code> class.
    */
   public void testElementParser() throws Exception {

      // Parse an XML string with namespaces
      String s = "<ns:a xmlns:ns=\"b\" c='2'><ns:e><g xmlns='f'/><h></h></ns:e></ns:a>";
      Element element = new ElementParser().parse(new StringReader(s));
      assertNotNull(element);

      // Parse root 'a' element
      assertEquals("a",  element.getLocalName());
      assertEquals("b",  element.getNamespaceURI());
      assertEquals(1,    element.getAttributeMap().size());
      assertEquals("2",  element.getAttribute("c"));
      assertEquals(0,    element.getChildElements("d").size());
      assertEquals(0,    element.getChildElements("d:e").size());
      assertEquals(null, element.getText());

      // Parse contained 'e' element
      List aChildren = element.getChildElements();
      assertEquals(1, aChildren.size());
      Element eChild = (Element) aChildren.get(0);
      assertEquals("e",  eChild.getLocalName());
      assertEquals("b",  eChild.getNamespaceURI());
      assertEquals(2,    eChild.getChildElements().size());
      assertEquals(0,    eChild.getChildElements("d:g").size());
      assertEquals(1,    eChild.getChildElements("h").size());
      assertEquals(null, eChild.getText());

      // Parse contained 'g' element
      List eChildren = eChild.getChildElements();
      assertEquals(2, eChildren.size());
      Element gChild = (Element) eChildren.get(0);
      Element hChild = (Element) eChildren.get(1);
      assertEquals("g",  gChild.getLocalName());
      assertEquals("f",  gChild.getNamespaceURI());
      assertEquals(0,    gChild.getChildElements().size());
      assertEquals(null, gChild.getText());
      assertEquals("h",  hChild.getLocalName());
      assertEquals(null, hChild.getNamespaceURI());
      assertEquals(0,    hChild.getChildElements().size());
      assertEquals(null, hChild.getText());
   }
}
