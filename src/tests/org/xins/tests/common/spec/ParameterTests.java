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
import org.xins.common.spec.Function;
import org.xins.common.spec.Parameter;
import org.xins.common.types.standard.Text;

import com.mycompany.allinone.capi.CAPI;
import com.mycompany.allinone.types.Age;
import com.mycompany.allinone.types.IPAddress;
import com.mycompany.allinone.types.Salutation;
import com.mycompany.allinone.types.TextList;

/**
 * Parameter spec TestCase. The testcases use the <i>allinone</i> API 
 * to test the API specification.
 *
 * @version $Revision$ $Date$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 * @author Tauseef Rehman (<a href="mailto:tauseef.rehman@nl.wanadoo.com">tauseef.rehman@nl.wanadoo.com</a>)
 */
public class ParameterTests extends TestCase {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Holds a reference to the paramter of a function of the API for further 
    * questioning.
    */
   private Parameter _parameter;
   
   /**
    * Holds a reference to the user defined paramter of a function of the 
    * API for further questioning.
    */
   private Parameter[] _userDefinedParams;
   
   
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
      API allInOneAPI = allInOne.getAPISpecification();

      String functionName = "DataSection";
      Function function = allInOneAPI.getFunction(functionName);
      _parameter = function.getInputParameters()[0];
      _userDefinedParams = allInOneAPI.getFunction("DefinedTypes").getInputParameters();
   }

   /**
    * Tests that the getName() returns the correct name of the paramter 
    * of a function of the API
    * @see org.xins.common.spec.Parameter#getName()
    */
   public void testParameterGetName() {
      assertEquals("For function 'DataSection', incorrect name of the paramter: " + _parameter.getName(), 
         "inputText", _parameter.getName());
   }

   /**
    * Tests that the getDescription() returns the correct description of the
    * parameter of a function of the API.
    * @see org.xins.common.spec.Parameter#getDescription()
    */
   public void testParameterGetDescription() {
      assertEquals("For function 'DataSection', incorret description of the paramter: " + _parameter.getDescription(),
         "An example of input for a text.", _parameter.getDescription());
   }

   /**
    * Tests that isRequired() returns the correct flag for the parameter of
    * a function of the API.
    * @see org.xins.common.spec.Parameter#isRequired()
    */
   public void testParameterIsRequired() {
      assertFalse("For function 'DataSection', incorrect 'is required' flag: " + _parameter.isRequired(),
         _parameter.isRequired());
   }

   /**
    * Tests that getType() returns the correct type of the parameter of
    * a function of the API.
    * @see org.xins.common.spec.Parameter#getType()
    */
   public void testParameterGetType() {
      assertTrue("For function 'DataSection', incorrect type of paramter: " + _parameter.getType(),
         _parameter.getType() instanceof Text);
   }

   /**
    * Tests that getType() returns the correct user defined type of the 
    * parameter of a function of the API.
    * @see org.xins.common.spec.Parameter#getType()
    */
   public void testParameterGetTypeUserDefined() {
      for (int i = 0; i < _userDefinedParams.length; i++) {
         Parameter userDefinedParameter = _userDefinedParams[i];
         if ("inputIP".equals(userDefinedParameter.getName())) {
            assertEquals("For function 'DefinedTypes', incorrect description of the user defined type: " + userDefinedParameter.getDescription(),
               "An example of input for a pattern type.", userDefinedParameter.getDescription());
            assertEquals("For function 'DefinedTypes', incorrect name of the user defined type: " + userDefinedParameter.getType().getName(),
               "IPAddress", userDefinedParameter.getType().getName());
            assertTrue("For function 'DefinedTypes', incorrect type of the user defined type: " + userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof IPAddress);
            assertFalse("For function 'DefinedTypes', incorrect 'is required' flag for the user defined type: " + userDefinedParameter.isRequired(), 
               userDefinedParameter.isRequired());
         } else if ("inputSalutation".equals(userDefinedParameter.getName())) {
            assertEquals("For function 'DefinedTypes', incorrect description of the user defined type: " + userDefinedParameter.getDescription(),
               "An example of input for an enum type.", userDefinedParameter.getDescription());
            assertEquals("For function 'DefinedTypes', incorrect name of the user defined type: " + userDefinedParameter.getType().getName(),
               "Salutation", userDefinedParameter.getType().getName());
            assertTrue("For function 'DefinedTypes', incorrect type of the user defined type: " + userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof Salutation);
            assertTrue("For function 'DefinedTypes', incorrect 'is required' flag for the user defined type: " + userDefinedParameter.isRequired(),
               userDefinedParameter.isRequired());
         } else if ("inputAge".equals(userDefinedParameter.getName())) {
            assertEquals("For function 'DefinedTypes', incorrect description of the user defined type: " + userDefinedParameter.getDescription(),
               "An example of input for a int8 type with a minimum and maximum.", userDefinedParameter.getDescription());
            assertEquals("For function 'DefinedTypes', incorrect name of the user defined type: " + userDefinedParameter.getType().getName(),
               "Age", userDefinedParameter.getType().getName());
            assertTrue("For function 'DefinedTypes', incorrect type of the user defined type: " + userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof Age);
            assertTrue("For function 'DefinedTypes', incorrect 'is required' flag for the user defined type: " + userDefinedParameter.isRequired(),
               userDefinedParameter.isRequired());
         } else if ("inputList".equals(userDefinedParameter.getName())) {
            assertEquals("For function 'DefinedTypes', incorrect description of the user defined type: " + userDefinedParameter.getDescription(),
               "An example of input for a list.", userDefinedParameter.getDescription());
            assertEquals("For function 'DefinedTypes', incorrect name of the user defined type: " + userDefinedParameter.getType().getName(),
               "TextList", userDefinedParameter.getType().getName());
            assertTrue("For function 'DefinedTypes', incorrect type of the user defined type: " + userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof TextList);
            assertFalse("For function 'DefinedTypes', incorrect 'is required' flag for the user defined type: " + userDefinedParameter.isRequired(),
               userDefinedParameter.isRequired());
         } else {
            fail("Contains a parameter: " + userDefinedParameter.getName()
               + " which should not be there.");
         }
      }
   }
}