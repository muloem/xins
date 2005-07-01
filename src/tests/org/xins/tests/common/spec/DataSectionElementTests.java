/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import junit.framework.TestCase;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.spec.API;
import org.xins.common.spec.DataSectionElement;
import org.xins.common.spec.Parameter;
import org.xins.common.types.standard.Int32;
import org.xins.common.types.standard.Int64;
import org.xins.common.types.standard.Text;

import com.mycompany.allinone.capi.CAPI;

/**
 * API spec TestCase. The testcase assumes that the example api allinone is
 * the api being questioned for meta information like name, functions and so on.
 *
 * @version $Revision$ $Date$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 * @author Tauseef Rehman (<a href="mailto:tauseef.rehman@nl.wanadoo.com">tauseef.rehman@nl.wanadoo.com</a>)
 */
public class DataSectionElementTests extends TestCase {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------


   /**
    * Hold a reference to the API for further questioning.
    */
   private static API allInOneAPI;
   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp()
   throws Exception {
      TargetDescriptor target = new TargetDescriptor("http://www.xins.org");
      CAPI allInOne = new CAPI(target);
      allInOneAPI = allInOne.getAPISpecification();

   }

   /**
    * @see org.xins.common.spec.DataSectionElement#getName()
    */
   public void testDataSectionGetName() throws Exception {
      DataSectionElement element =
         allInOneAPI.getFunction("DataSection").getOutputDataSectionElements()[0];
      assertEquals("user", element.getName());
   }

   /**
    * @see org.xins.common.spec.DataSectionElement#getDescription()
    */
   public void testDataSectionGetDescription() {
      DataSectionElement element =
         allInOneAPI.getFunction("DataSection").getOutputDataSectionElements()[0];
      assertEquals("A user.", element.getDescription());
   }

   /**
    * @see org.xins.common.spec.DataSectionElement#getSubElements()
    */
   public void testDataSectionGetSubElements() {
      DataSectionElement element =
         allInOneAPI.getFunction("DataSection2").getOutputDataSectionElements()[0];
      assertEquals(1, element.getSubElements().length);
      assertEquals("product", element.getSubElements()[0].getName());
   }

   /**
    * @see org.xins.common.spec.DataSectionElement#getAttributes()
    */
   public void testDataSectionGetAttributes() {
      DataSectionElement element =
         allInOneAPI.getFunction("DataSection2").getOutputDataSectionElements()[0];

      assertEquals(1, element.getAttributes().length);
      Parameter attribute = element.getAttributes()[0];
      assertEquals("destination", attribute.getName());
      assertEquals("The destination of the packet.", attribute.getDescription());
      assertTrue(attribute.isRequired());
      assertTrue(attribute.getType() instanceof Text);

      assertEquals(1, element.getSubElements().length);
   }

   /**
    * @see org.xins.common.spec.DataSectionElement#getAttributes()
    */
   public void testDataSectionGetAttributesSubElement() {
      DataSectionElement element =
         allInOneAPI.getFunction("DataSection2").getOutputDataSectionElements()[0];
      DataSectionElement subElement = element.getSubElements()[0];
      Parameter[] subElementAttributes = subElement.getAttributes();

      for (int i = 0; i < subElementAttributes.length; i++) {
         Parameter attribute = subElementAttributes[i];
         if ("id".equals(attribute.getName())) {
            assertEquals("The id of the product.", attribute.getDescription());
            assertTrue(attribute.isRequired());
            assertTrue(attribute.getType() instanceof Int64);
         } else if ("price".equals(attribute.getName())) {
            assertEquals("The description of the product.", attribute.getDescription());
            assertFalse(attribute.isRequired());
            assertTrue(attribute.getType() instanceof Int32);
         } else {
            fail("Contains an attribute " + attribute.getName() + " which should not be there.");
         }
      }
   }

   /**
    * @see org.xins.common.spec.DataSectionElement#isPCDataAllowed()
    */
   public void testDataSectionIsPCDataAllowed() {
      DataSectionElement element =
         allInOneAPI.getFunction("DataSection").getOutputDataSectionElements()[0];

      assertTrue(element.isPCDataAllowed());
   }
}
