/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.client;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.client.DataElement;

/**
 * Tests for class <code>DataElement</code>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class DataElementTests extends TestCase {

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(DataElementTests.class);
   }

   /**
    * Constructs a new <code>DataElementTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public DataElementTests(String name) {
      super(name);
   }

   /**
    * Tests the behaviour of the <code>DataElement.QualifiedName</code> class.
    *
    * @throws Exception
    *    if an unexpected exception is thrown.
    */
   public void testDataElementQualifiedName() throws Exception {

      DataElement.QualifiedName qn1, qn2, qn3;

      String uri = "SomeURI";
      String localName = "SomeName";

      try {
         new DataElement.QualifiedName(null, null);
         fail("DataElement.QualifiedName(null, null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException e) {
         // as expected
      }

      try {
         new DataElement.QualifiedName(uri, null);
         fail("DataElement.QualifiedName(\"" + uri + "\", null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException e) {
         // as expected
      }

      qn1 = new DataElement.QualifiedName(null, localName);
      assertEquals(null,      qn1.getNamespaceURI());
      assertEquals(localName, qn1.getLocalName());

      qn2 = new DataElement.QualifiedName(null, localName);
      assertEquals(qn1, qn1);
      assertEquals(qn1, qn2);
      assertEquals(qn2, qn1);
      assertEquals(qn2, qn2);

      qn3 = new DataElement.QualifiedName("", localName);
      assertEquals(null,      qn1.getNamespaceURI());
      assertEquals(localName, qn1.getLocalName());
      assertEquals(qn1, qn2);
      assertEquals(qn1, qn3);
      assertEquals(qn2, qn1);
      assertEquals(qn2, qn3);
      assertEquals(qn3, qn1);
      assertEquals(qn3, qn2);

      qn1 = new DataElement.QualifiedName(uri, localName);
      assertEquals(uri,       qn1.getNamespaceURI());
      assertEquals(localName, qn1.getLocalName());

      qn2 = new DataElement.QualifiedName(uri, localName);
      assertEquals(qn1, qn2);
      assertEquals(qn2, qn1);
   }
}
