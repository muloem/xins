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
    * The input parameter specification of the <i>DataSection</i> function.
    */
   private Parameter _parameter;
	   
   /**
    * The user defined input parameters specification of 
    * the <i>DefinedTypes</i> function.
    */
   private Parameter[] _userDefinedParams;
	   
	   
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
      _userDefinedParams = 
         allInOneAPI.getFunction("DefinedTypes").getInputParameters();
   }

   /**
    * Tests that {@link Parameter#getName() getName()} returns the correct 
    * name of the paramter of a function of the API
    */
   public void testParameterGetName() {
      assertEquals("Function 'DataSection' has an incorrect parameter name: " +
         _parameter.getName(), "inputText", _parameter.getName());
   }

   /**
    * Tests that the {@link Parameter#getDescription() getDescription()} returns
    * the correct description of the parameter of a function of the API.
    */
   public void testParameterGetDescription() {
      assertEquals("Function 'DataSection' has an incorrect parameter description: " +
         _parameter.getDescription(),
         "An example of input for a text.", _parameter.getDescription());
   }

   /**
    * Tests that {@link Parameter#isRequired() isRequired()} returns the correct 
    * flag for the parameter of a function of the API.
    */
   public void testParameterIsRequired() {
      assertFalse("Function 'DataSection' has an incorrect 'is required' value: " 
         + _parameter.isRequired(), _parameter.isRequired());
   }

   /**
    * Tests that {@link Parameter#getType() getType()} returns the correct type of 
    * the parameter of a function of the API.
    */
   public void testParameterGetType() {
      assertTrue("Function 'DataSection' has an incorrect parameter type: " + 
         _parameter.getType(), _parameter.getType() instanceof Text);
   }

   /**
    * Tests that {@link Parameter#getType() getType()} returns the correct 
    * user defined type of the parameter of a function of the API.
    */
   public void testParameterGetTypeUserDefined() {
      for (int i = 0; i < _userDefinedParams.length; i++) {
         Parameter userDefinedParameter = _userDefinedParams[i];
         if ("inputIP".equals(userDefinedParameter.getName())) {
            assertEquals("User defined type 'inputIP' of the function " +
               "'DefinedTypes' has an incorrect description: " + 
               userDefinedParameter.getDescription(),
               "An example of input for a pattern type.", 
               userDefinedParameter.getDescription());
            
            assertEquals("User defined type 'inputIP' of the function " +
               "'DefinedTypes' has an incorrect name: " + 
               userDefinedParameter.getType().getName(),
               "IPAddress", userDefinedParameter.getType().getName());
            
            assertTrue("User defined type 'inputIP' of the function " +
               "'DefinedTypes' has an incorrect type: " + 
               userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof IPAddress);
            
            assertFalse("User defined type 'inputIP' of the function " +
               "'DefinedTypes' has an incorrect 'is required' value: " + 
               userDefinedParameter.isRequired(), 
               userDefinedParameter.isRequired());
            
         } else if ("inputSalutation".equals(userDefinedParameter.getName())) {
            assertEquals("User defined type 'inputSalutation' of the function " +
               "'DefinedTypes' has an incorrect description" + 
               userDefinedParameter.getDescription(),
               "An example of input for an enum type.", 
               userDefinedParameter.getDescription());
            
            assertEquals("User defined type 'inputSalutation' of the function " +
               "'DefinedTypes' has an incorrect name: " +
               userDefinedParameter.getType().getName(),
               "Salutation", userDefinedParameter.getType().getName());
            
            assertTrue("User defined type 'inputSalutation' of the function " +
               "'DefinedTypes' has an incorrect type: " +
               userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof Salutation);
            
            assertTrue("User defined type 'inputSalutation' of the function " +
               "'DefinedTypes' has an incorrect 'is required' value: " +
               userDefinedParameter.isRequired(),
               userDefinedParameter.isRequired());
            
         } else if ("inputAge".equals(userDefinedParameter.getName())) {
            assertEquals("User defined type 'inputAge' of the function " +
               "'DefinedTypes' has an incorrect description: " +
               userDefinedParameter.getDescription(),
               "An example of input for a int8 type with a minimum and maximum.", 
			   userDefinedParameter.getDescription());
            
            assertEquals("User defined type 'inputAge' of the function " +
               "'DefinedTypes' has an incorrect name: " +
               userDefinedParameter.getType().getName(),
               "Age", userDefinedParameter.getType().getName());
            
            assertTrue("User defined type 'inputAge' of the function " +
               "'DefinedTypes' has an incorrect type: " +
               userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof Age);
            
            assertTrue("User defined type 'inputAge' of the function " +
               "'DefinedTypes' has an incorrect 'is required' value: " +
               userDefinedParameter.isRequired(),
               userDefinedParameter.isRequired());
            
         } else if ("inputList".equals(userDefinedParameter.getName())) {
            assertEquals("User defined type 'inputList' of the function " +
               "'DefinedTypes' has an incorrect description: " +
               userDefinedParameter.getDescription(),
               "An example of input for a list.", 
               userDefinedParameter.getDescription());
            
            assertEquals("User defined type 'inputList' of the function " +
               "'DefinedTypes' has an incorrect name:" +
               userDefinedParameter.getType().getName(),
               "TextList", userDefinedParameter.getType().getName());
            
            assertTrue("User defined type 'inputList' of the function " +
               "'DefinedTypes' has an incorrect type: " +
               userDefinedParameter.getType().getName(),
               userDefinedParameter.getType() instanceof TextList);
            assertFalse("User defined type 'inputList' of the function " +
               "'DefinedTypes' has an incorrect 'is required' value: " +
               userDefinedParameter.isRequired(),
               userDefinedParameter.isRequired());
         } else {
            fail("Function 'DefinedTypes' contains a user defined type : "
               + userDefinedParameter.getName() + " which is not specified.");
         }
      }
   }
}