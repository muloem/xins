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
 * DataSectionElement spec TestCase. The testcases use the <i>allinone</i> API 
 * to test the API specification.
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
    * Holds a reference to the allInone API for further questioning.
    */
   private API _allInOneAPI;
   
   
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
      _allInOneAPI = allInOne.getAPISpecification();

   }

   /**
    * Tests that the getOutputDataSectionElements() returns the correct 
    * datasection for a function of the API.
    * @see org.xins.common.spec.DataSectionElement#getName()
    */
   public void testDataSectionGetName() throws Exception {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection").getOutputDataSectionElements()[0];
      assertEquals("For function 'DataSection', incorrect datasection name: " + element.getName(), 
         "user", element.getName());
   }

   /**
    * Tests that the getDescription() returns the correct description for a 
    * datasection element for a function of the API. 
    * @see org.xins.common.spec.DataSectionElement#getDescription()
    */
   public void testDataSectionGetDescription() {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection").getOutputDataSectionElements()[0];
      assertEquals("For function 'DataSection', Incorrect description of the datasection: " + element.getDescription(),
         "A user.", element.getDescription());
   }

   /**
    * Tests that the getSubElements() returns the correct sub-elements of
    * a datasection for a function of the API. 
    * @see org.xins.common.spec.DataSectionElement#getSubElements()
    */
   public void testDataSectionGetSubElements() {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection2").getOutputDataSectionElements()[0];
      assertEquals("In dataelement 'product' for function 'DataSection2', incorrect number of sub-elements: " + element.getSubElements().length, 
         1, element.getSubElements().length);
      assertEquals("In dataelement 'product' for function 'DataSection2', incorrect name of the sub-element: " + element.getSubElements()[0].getName(), 
         "product", element.getSubElements()[0].getName());
   }

   /**
    * Tests that the getAttributes() returns the correct attributes for a 
    * datasection of a funciton of the API.
    * @see org.xins.common.spec.DataSectionElement#getAttributes()
    */
   public void testDataSectionGetAttributes() {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection2").getOutputDataSectionElements()[0];

      assertEquals(1, element.getAttributes().length);
      Parameter attribute = element.getAttributes()[0];
      assertEquals("In output datasection for function 'DataSection2', incorrect attribute name: " + attribute.getName(), 
         "destination", attribute.getName());
      assertEquals("In output datasection for function 'DataSection2', incorrect attribute description: " + attribute.getDescription(),
         "The destination of the packet.", attribute.getDescription());
      assertTrue("In output datasection for function 'DataSection2', incorrect attribute's 'is required' property: ", 
         attribute.isRequired());
      assertTrue("In output datasection for function 'DataSection2', incorrect attribute type: " + attribute.getType(), 
         attribute.getType() instanceof Text);

      assertEquals("In output datasection for function 'DataSection2', incorrect number subelements: " + element.getSubElements().length,
         1, element.getSubElements().length);
   }

   /**
    * Tests that the getAttributes() returns the correct sub-elemets of attributes 
    * for a datasection of a funciton of the API.
    * @see org.xins.common.spec.DataSectionElement#getAttributes()
    */
   public void testDataSectionGetAttributesSubElement() {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection2").getOutputDataSectionElements()[0];
      DataSectionElement subElement = element.getSubElements()[0];
      Parameter[] subElementAttributes = subElement.getAttributes();

      for (int i = 0; i < subElementAttributes.length; i++) {
         Parameter attribute = subElementAttributes[i];
         if ("id".equals(attribute.getName())) {
            assertEquals("In subelement of output datasection for function 'DataSection2', incorrect attribute 'id' description: " + attribute.getDescription(), 
               "The id of the product.", attribute.getDescription());
            assertTrue("In subelement of output datasection for function 'DataSection2', incorrect attribute 'id's 'is required' property: ", 
               attribute.isRequired());
            assertTrue("In subelement of output datasection for function 'DataSection2', incorrect attribute 'id's type: ", 
               attribute.getType() instanceof Int64);
         } else if ("price".equals(attribute.getName())) {
            assertEquals("In subelement of output datasection for function 'DataSection2', incorrect attribute 'price' description: " + attribute.getDescription(), 
               "The description of the product.", attribute.getDescription());
            assertFalse("In subelement of output datasection for function 'DataSection2', incorrect attribute 'price's 'is required' property: ", 
               attribute.isRequired());
            assertTrue("In subelement of output datasection for function 'DataSection2', incorrect attribute 'price's type: ", 
               attribute.getType() instanceof Int32);
         } else {
            fail("Contains an attribute " + attribute.getName() + " which should not be there.");
         }
      }
   }

   /**
    * Tests that isPCDataAllowed() returns the correct PC data allowed in a 
    * datasection for a function of the API.
    * @see org.xins.common.spec.DataSectionElement#isPCDataAllowed()
    */
   public void testDataSectionIsPCDataAllowed() {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection").getOutputDataSectionElements()[0];

      assertTrue("PC data allowed property is incorrect",
         element.isPCDataAllowed());
   }
}
