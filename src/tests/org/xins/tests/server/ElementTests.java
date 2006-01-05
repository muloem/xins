/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.collections.PropertyReader;
import org.xins.server.Element;

/**
 * Tests for class <code>org.xins.server.Element</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class ElementTests extends TestCase {

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
      return new TestSuite(ElementTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ElementTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ElementTests(String name) {
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

   public void testServerElement() throws Throwable {

      // Pass null to the Element constructor (should fail)
      try {
         new Element(null);
         fail("Expected Element(null) to throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Test a simple empty element and a clone of it
      Element[] e = new Element[2];
      e[0] = new Element("root");
      e[1] = (Element) e[0].clone();
      for (int i = 0; i < 2; i++) {
         assertNull   (        e[i].getParent()                         );
         assertEquals ("root", e[i].getType()                           );
         assertNotNull(        e[i].getAttributes()                     );
         assertEquals (0,      e[i].getAttributes().size()              );
         assertFalse  (        e[i].getAttributes().getNames().hasNext());
         assertNull   (        e[i].getChildren()                       );
         assertNull   (        e[i].getText()                           );
      }

      // Test parent
      Element parent = new Element("father");
      Element child  = new Element("son");
      parent.add(child);
      assertEquals(parent, child.getParent());

      // Test attributes
      Element employee = new Element("person");
      employee.addAttribute("id",   "42"  );
      employee.addAttribute("name", "Fred");

      PropertyReader attr = employee.getAttributes();
      assertNotNull(        attr       );
      assertEquals (2,      attr.size());
      assertEquals ("42",   attr.get("id")  );
      assertEquals ("Fred", attr.get("name"));
   }
}
