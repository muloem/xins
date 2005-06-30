/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import junit.framework.TestCase;

import org.xins.common.spec.API;
import org.xins.common.spec.Function;
import org.xins.common.spec.Parameter;
import org.xins.common.types.standard.Text;

/**
 * API spec TestCase. The testcase assumes that the example api allinone is
 * the api being questioned for meta information like name, functions and so on.
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
    * Hold a reference to the API for further questioning.
    */
   private static API allInOneAPI;
   private static Parameter parameter;
   private static Parameter[] userDefinedParams;
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
      allInOneAPI = new API(null); // TODO
      String functionName = "DataSection";
      Function function = allInOneAPI.getFunction(functionName);
      parameter = function.getInputParameters()[0];
      userDefinedParams = allInOneAPI.getFunction("DefinedTypes").getInputParameters();
   }

   /**
    * @see org.xins.common.spec.Parameter#getName()
    */
   public void testParameterGetName() {
      assertEquals("inputText", parameter.getName());
   }

   /**
    * @see org.xins.common.spec.Parameter#getDescription()
    */
   public void testParameterGetDescription() {
      assertEquals("An example of input for a text.", parameter.getDescription());
   }

   /**
    * @see org.xins.common.spec.Parameter#isRequired()
    */
   public void testParameterIsRequired() {
      assertFalse(parameter.isRequired());
   }

   /**
    * @see org.xins.common.spec.Parameter#getType()
    */
   public void testParameterGetType() throws Exception {
      assertTrue(parameter.getType() instanceof Text);
   }

   /**
    * @see org.xins.common.spec.Parameter#getType()
    */
   public void testParameterGetTypeUserDefined() throws Exception {
      for (int i = 0; i < userDefinedParams.length; i++) {
         Parameter userDefinedParameter = userDefinedParams[i];
         if ("inputIP".equals(userDefinedParameter.getName())) {
            assertEquals("An example of input for a pattern type.", userDefinedParameter.getDescription());
            assertEquals("IPAddress", userDefinedParameter.getType().getName());
            assertFalse(userDefinedParameter.isRequired());
         } else if ("inputSalutation".equals(userDefinedParameter.getName())) {
            assertEquals("An example of input for an enum type.", userDefinedParameter.getDescription());
            assertEquals("Salutation", userDefinedParameter.getType().getName());
            assertTrue(userDefinedParameter.isRequired());
         } else if ("inputAge".equals(userDefinedParameter.getName())) {
            assertEquals("An example of input for a int8 type with a minimum and maximum.", userDefinedParameter.getDescription());
            assertEquals("Age", userDefinedParameter.getType().getName());
            assertTrue(userDefinedParameter.isRequired());
         } else if ("inputList".equals(userDefinedParameter.getName())) {
            assertEquals("An example of input for a list.", userDefinedParameter.getDescription());
            assertEquals("TextList", userDefinedParameter.getType().getName());
            assertFalse(userDefinedParameter.isRequired());
         }else {
            fail("Contains a parameter: " + userDefinedParameter.getName() 
               + " which should not be there.");
         }

      }
   }

}


