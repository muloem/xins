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
   
   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------
	
   /**
    * The API specification of the <i>allinone</i> API.
    */
   private API _allInOneAPI;
	   

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
    * Tests that {@link DataSectionElement#getName() getName()} returns 
    * the correct name for a data section element for a function of the API.
    */
   public void testDataSectionGetName() throws Exception {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection").getOutputDataSectionElements()[0];
      assertEquals("Function 'DataSection' has an incorrect data section element " +
         "name: " + element.getName(), "user", element.getName());
   }

   /**
    * Tests that {@link DataSectionElement#getDescription() getDescription()} 
    * returns the correct description for a data section element for a function of 
    * the API. 
    */
   public void testDataSectionGetDescription() {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection").getOutputDataSectionElements()[0];
      assertEquals("Function 'DataSection' has an incorrect " +
         "data section element description: " + element.getDescription(),
         "A user.", element.getDescription());
   }

   /**
    * Tests that {@link DataSectionElement#getSubElements() getSubElements()} 
    * returns the correct sub-elements of a data section for a function of the API. 
    */
   public void testDataSectionGetSubElements() {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection2").getOutputDataSectionElements()[0];
      assertEquals("Data Element 'product' in the function 'DataSection2' has an " +
         "incorrect number of the sub-elements: " + element.getSubElements().length, 
         1, element.getSubElements().length);
      assertEquals("Data Element 'product' in the function 'DataSection2' has an " +
         "incorrect name of the sub-element: " + element.getSubElements()[0].getName(), 
         "product", element.getSubElements()[0].getName());
   }

   /**
    * Tests that {@link DataSectionElement#getAttributes() getAttributes()} 
    * returns the correct attributes for a data section of a funciton of the API.
    */
   public void testDataSectionGetAttributes() {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection2").getOutputDataSectionElements()[0];

      assertEquals(1, element.getAttributes().length);
      Parameter attribute = element.getAttributes()[0];
      assertEquals("The attribute in the output data section element for the " +
         "function 'DataSection2' has an incorrect name: " + attribute.getName(), 
         "destination", attribute.getName());
      assertEquals("The attribute in the output data section element for the " +
         "function 'DataSection2' has an incorrect description: "  
         + attribute.getDescription(),
         "The destination of the packet.", attribute.getDescription());
      assertTrue("The attribute in the output data section element for the " +
         "function 'DataSection2' has an incorrect 'is required' value: " , 
         attribute.isRequired());
      assertTrue("The attribute in the output data section element for the " +
         "function 'DataSection2' has an incorrect type: "  + attribute.getType(), 
         attribute.getType() instanceof Text);

      assertEquals("The output data section element for the function 'DataSection2'" +
         " has an incorrect number of the sub-elements: " + 
         element.getSubElements().length, 1, element.getSubElements().length);
   }

   /**
    * Tests that {@link DataSectionElement#getAttributes() getAttributes()} 
    * returns the correct sub-elements of attributes for a datasection of a funciton
    * of the API.
    */
   public void testDataSectionGetAttributesSubElement() {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection2").getOutputDataSectionElements()[0];
      DataSectionElement subElement = element.getSubElements()[0];
      Parameter[] subElementAttributes = subElement.getAttributes();

      for (int i = 0; i < subElementAttributes.length; i++) {
         Parameter attribute = subElementAttributes[i];
         if ("id".equals(attribute.getName())) {
            assertEquals("Attribute 'id' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "description: " + attribute.getDescription(), 
               "The id of the product.", attribute.getDescription());
            assertTrue("Attribute 'id' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "'is required' value: " , attribute.isRequired());
            assertTrue("Attribute 'id' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "type: ", attribute.getType() instanceof Int64);
         } else if ("price".equals(attribute.getName())) {
            assertEquals("Attribute 'price' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "description: " + attribute.getDescription(), 
               "The description of the product.", attribute.getDescription());
            assertFalse("Attribute 'price' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "'is required' value: ", attribute.isRequired());
            assertTrue("Attribute 'price' in the sub-element of the output data " +
               "section element of the function 'DataSection2' has an incorrect " +
               "type: ", attribute.getType() instanceof Int32);
         } else {
            fail("The sub-element of the output data section element of the " +
               "function 'DataSection2' contains an attribute" +
               attribute.getName() + " which should not be there.");
         }
      }
   }

   /**
    * Tests that {@link DataSectionElement#isPCDataAllowed() isPCDataAllowed()} 
    * returns the correct PC data allowed in a datasection for a function of 
    * the API.
    */
   public void testDataSectionIsPCDataAllowed() {
      DataSectionElement element =
         _allInOneAPI.getFunction("DataSection").getOutputDataSectionElements()[0];

      assertTrue("The output data section element for the function 'DataSection2'" +
         " has an incorrect 'PC data allowed' value", element.isPCDataAllowed());
   }
}
