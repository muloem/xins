/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import java.util.Iterator;
import java.util.Map;
import junit.framework.TestCase;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.spec.APISpec;
import org.xins.common.spec.DataSectionElementSpec;
import org.xins.common.spec.ParameterSpec;
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
   private APISpec _allInOneAPI;

   /**
    * The first data section element of the DataSection function.
    */
   private DataSectionElementSpec _userElement;

   /**
    * The first data section element of the DataSection2 function.
    */
   private DataSectionElementSpec _packetElement;


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
      _userElement = _allInOneAPI.getFunction("DataSection").getOutputDataSectionElement("user");
      _packetElement = _allInOneAPI.getFunction("DataSection2").getOutputDataSectionElement("packet");
   }

   /**
    * Tests that {@link DataSectionElement#getName() getName()} returns 
    * the correct name for a data section element for a function of the API.
    */
   public void testDataSectionGetName() throws Exception {
      assertEquals("Function 'DataSection' has an incorrect data section element " +
         "name: " + _userElement.getName(), "user", _userElement.getName());
   }

   /**
    * Tests that {@link DataSectionElement#getDescription() getDescription()} 
    * returns the correct description for a data section element for a function of 
    * the API. 
    */
   public void testDataSectionGetDescription() {
      assertEquals("Function 'DataSection' has an incorrect " +
         "data section element description: " + _userElement.getDescription(),
         "A user.", _userElement.getDescription());
   }

   /**
    * Tests that {@link DataSectionElement#getSubElements() getSubElements()} 
    * returns the correct sub-elements of a data section for a function of the API. 
    */
   public void testDataSectionGetSubElements() throws Exception {
      assertEquals("Data Element 'packet' in the function 'DataSection2' has an " +
         "incorrect number of the sub-elements: " + _packetElement.getSubElements().size(), 
         1, _packetElement.getSubElements().size());
      assertEquals("Data Element 'product' in the function 'DataSection2' has an " +
         "incorrect name of the sub-element: " + _packetElement.getSubElement("product").getName(), 
         "product", _packetElement.getSubElement("product").getName());
   }

   /**
    * Tests that {@link DataSectionElement#getAttributes() getAttributes()} 
    * returns the correct attributes for a data section of a function of the API.
    */
   public void testDataSectionGetAttributes() throws Exception {
      assertEquals(1, _packetElement.getAttributes().size());
      ParameterSpec attribute = _packetElement.getAttribute("destination");
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
         _packetElement.getSubElements().size(), 1, _packetElement.getSubElements().size());
   }

   /**
    * Tests that {@link DataSectionElement#getAttributes() getAttributes()} 
    * returns correct attributes for the sub-element in a data section
    * of the API.
    */
   public void testDataSectionGetAttributesSubElement() throws Exception {
      DataSectionElementSpec subElement = _packetElement.getSubElement("product");
      Map subElementAttributes = subElement.getAttributes();
      Iterator itSubElementAttributes = subElementAttributes.values().iterator();

      while (itSubElementAttributes.hasNext()) {
         ParameterSpec attribute = (ParameterSpec) itSubElementAttributes.next();
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
    * returns the correct PC data allowed in a data section for a function of 
    * the API.
    */
   public void testDataSectionIsPCDataAllowed() {
      assertTrue("The output data section element for the function 'DataSection'" +
         " has an incorrect 'PC data allowed' value", _userElement.isPCDataAllowed());
   }
}
