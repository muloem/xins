/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.xml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.xml.Element;

/**
 * Tests for class <code>Element</code>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class ElementTests extends TestCase {

   /**
     * Constructs a new <code>ElementTests</code> test suite with
     * the specified name. The name will be passed to the superconstructor.
     * 
     * 
     * @param name
     *    the name for this test suite.
     */
   public ElementTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ElementTests.class);
   }

   /**
    * Tests the behaviour of the <code>Element.QualifiedName</code> class.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testDataElementQualifiedName() throws Exception {

      Element.QualifiedName qn1, qn2, qn3;

      String uri = "SomeURI";
      String localName = "SomeName";

      try {
         new Element.QualifiedName(null, null);
         fail("Element.QualifiedName(null, null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException e) {
         // as expected
      }

      try {
         new Element.QualifiedName(uri, null);
         fail("Element.QualifiedName(\"" + uri + "\", null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException e) {
         // as expected
      }

      qn1 = new Element.QualifiedName(null, localName);
      assertEquals(null,      qn1.getNamespaceURI());
      assertEquals(localName, qn1.getLocalName());

      qn2 = new Element.QualifiedName(null, localName);
      assertEquals(qn1, qn1);
      assertEquals(qn1, qn2);
      assertEquals(qn2, qn1);
      assertEquals(qn2, qn2);

      qn3 = new Element.QualifiedName("", localName);
      assertEquals(null,      qn1.getNamespaceURI());
      assertEquals(localName, qn1.getLocalName());
      assertEquals(qn1, qn2);
      assertEquals(qn1, qn3);
      assertEquals(qn2, qn1);
      assertEquals(qn2, qn3);
      assertEquals(qn3, qn1);
      assertEquals(qn3, qn2);

      qn1 = new Element.QualifiedName(uri, localName);
      assertEquals(uri,       qn1.getNamespaceURI());
      assertEquals(localName, qn1.getLocalName());

      qn2 = new Element.QualifiedName(uri, localName);
      assertEquals(qn1, qn2);
      assertEquals(qn2, qn1);
   }
}
