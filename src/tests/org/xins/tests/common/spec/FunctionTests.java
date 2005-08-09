/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.spec.API;
import org.xins.common.spec.DataSectionElement;
import org.xins.common.spec.ErrorCode;
import org.xins.common.spec.Function;
import org.xins.common.spec.ParamCombo;
import org.xins.common.spec.Parameter;

import com.mycompany.allinone.capi.CAPI;

/**
 * Function spec TestCase. The testcases use the <i>allinone</i> API 
 * to test the API specification.
 * 
 * @version $Revision$ $Date$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 * @author Tauseef Rehman (<a href="mailto:tauseef.rehman@nl.wanadoo.com">tauseef.rehman@nl.wanadoo.com</a>)
 */
public class FunctionTests extends TestCase {

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
    * Tests that the {@link Function#getName() getName()} returns the correct name
    * of a function of the API.
    */
   public void testFunctionsGetName() {

      String functionName = "DataSection";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection' has an incorrect name: " + 
         function.getName(), functionName, function.getName());
   }

   /**
    * Tests that the {@link Function#getDescription() getDescription()} returns 
    * the correct description of a function of the API.
    */
   public void testFunctionsGetDescription() {

      String functionName = "DataSection";
      String functionDescription = "An example of a data section.";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection' has an incorrect description: " +
         function.getDescription(),
         functionDescription, function.getDescription());
   }

   /**
    * Tests that {@link Function#getInputParameters() getInputParameters()} returns
    * correct input parameters of a function of the API
    */
   public void testFunctionsGetInputParameters() {

      String functionName = "SimpleOutput";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'SimpleOutput' has an incorrect number of input" +
         " parameters: " + function.getInputParameters().length,
         0, function.getInputParameters().length);

      String functionName1 = "DefinedTypes";
      List parameters = new ArrayList();
      parameters.add("inputIP");
      parameters.add("inputSalutation");
      parameters.add("inputAge");
      parameters.add("inputList");

      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'DefinedTypes' has an incorrect number of input" +
         " parameters: " + function.getInputParameters().length,
         parameters.size(), function1.getInputParameters().length);

      Parameter[] functionParameters = function1.getInputParameters();

      for (int i = 0; i < functionParameters.length; i++) {
         assertTrue("Function 'DefinedTypes' does not contain the input parameter: " + 
            functionParameters[i].getName(),  
            parameters.contains(functionParameters[i].getName()));
      }

   }
   
   /**
    * Tests that getInputParameter(String) returns correct input parameters 
    * for a function of the API when given an input parameter name.
    * @see org.xins.common.spec.Function#getInputParameter(String)
    */
   public void testFunctionsGetInputParameter() {

      String functionName = "SimpleOutput";
      Function function = _allInOneAPI.getFunction(functionName);
      try {
         function.getInputParameter("NoName");
         fail("Function 'SimpleOutput' contains an input parameter 'NoName' " + 
            "which was not specified.");
      } catch (IllegalArgumentException e) {
         //expecting exception
      }
      
      String functionName1 = "DefinedTypes";
      List parameters = new ArrayList();
      parameters.add("inputIP");
      parameters.add("inputSalutation");
      parameters.add("inputAge");
      parameters.add("inputList");

      Function function1 = _allInOneAPI.getFunction(functionName1);
      String parameter = null;
      for (int i = 0; i < parameters.size(); i++) {
         try {
            parameter = (String) parameters.get(i);
            function1.getInputParameter(parameter);
            assertEquals("The input parameter of the function 'DefinedTypes' has " +
               " an incorrect name: " +
               function1.getInputParameter(parameter).getName(),
               parameter, function1.getInputParameter(parameter).getName());
            
         } catch (IllegalArgumentException e) {
            fail("The input parameter" + parameter + " of the function " +
               "'DefinedTypes' not found.");
         }
      }
   }

   /**
    * Tests that getOutputParameters() returns correct output parameters of a 
    * function of the API
    * @see org.xins.common.spec.Function#getOutputParameters()
    */
   public void testFunctionsGetOutputParameters() {

      String functionName = "DataSection";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection' has an incorrect number of output" +
         " parameters: " + function.getOutputParameters().length,
         0, function.getOutputParameters().length);

      String functionName1 = "DefinedTypes";
      List parameters = new ArrayList();
      parameters.add("outputIP");
      parameters.add("outputSalutation");
      parameters.add("outputAge");
      parameters.add("outputList");
      parameters.add("outputProperties");

      Function function1 = _allInOneAPI.getFunction(functionName1);

      Parameter[] functionParameters = function1.getOutputParameters();
      assertEquals("Function 'DefinedTypes' has an incorrect number of output" +
         " parameters: " + functionParameters.length,
         parameters.size(), functionParameters.length);

      for (int i = 0; i < functionParameters.length; i++) {
         assertTrue("Function 'DefinedTypes' does not contain the output parameter: " + 
            functionParameters[i].getName(),
            parameters.contains(functionParameters[i].getName()));
      }

   }

   /**
    * Tests that getOutputParameter(String) returns correct output parameters 
    * for a function of the API when given an output parameter name.
    * @see org.xins.common.spec.Function#getOutputParameter(String)
    */
   public void testFunctionsGetOutputParameter() {

      String functionName = "DataSection";
      Function function = _allInOneAPI.getFunction(functionName);
      try {
         function.getInputParameter("NoName");
         fail("Function 'DataSection' contains an output parameter 'NoName' " + 
            "which was not specified.");
      } catch (IllegalArgumentException e) {
         //expecting exception
      }
      
      String functionName1 = "DefinedTypes";
      List parameters = new ArrayList();
      parameters.add("outputIP");
      parameters.add("outputSalutation");
      parameters.add("outputAge");
      parameters.add("outputList");
      parameters.add("outputProperties");

      Function function1 = _allInOneAPI.getFunction(functionName1);
      String parameter = null;
      for (int i = 0; i < parameters.size(); i++) {
         try {
            parameter = (String) parameters.get(i);
            function1.getOutputParameter(parameter);
            assertEquals("The output parameter of the function 'DefinedTypes' has " +
                    " an incorrect name: " +
                    function1.getOutputParameter(parameter).getName(),
                    parameter, function1.getOutputParameter(parameter).getName());
            
         } catch (IllegalArgumentException e) {
            fail("The output parameter" + parameter + " of the function " +
               "'DefinedTypes' not found.");
         }
      }
   }
   
   /**
    * Tests that getErrorCodes() return correct errorcodes for a function 
    * of the API.
    * @see org.xins.common.spec.Function#getErrorCodes()
    */
   public void testFunctionsGetErrorCodes() {

      String functionName = "DataSection";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection' has an incorrect number of " +
         "error codes: " + function.getErrorCodes().length,
         0, function.getErrorCodes().length);

      String functionName1 = "ResultCode";
      List errorCodes = new ArrayList();
      errorCodes.add("AlreadySet");
      //TODO need to check the function with multiple error codes.
      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'AlreadySet' has an incorrect number of error codes: "
         + function1.getErrorCodes().length, 1, function1.getErrorCodes().length);

      ErrorCode[] functionErrorCodes = function1.getErrorCodes();

      for (int i = 0; i < functionErrorCodes.length; i++) {
         assertTrue("The error code in function 'AlreadySet' has an incorrect name: "
            + functionErrorCodes[i].getName(),
            errorCodes.contains(functionErrorCodes[i].getName()));
      }

   }

   /**
    * Tests that getInputDataSectionElements() returns the correct input data
    * section of a funtion of the API.
    * @see org.xins.common.spec.Function#getInputDataSectionElements()
    */
   public void testFunctionsGetInputDataSectionElements() {

      String functionName = "DataSection";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection' has an incorrect number of input " +
         "data section elements: " + function.getInputDataSectionElements().length,
         0, function.getInputDataSectionElements().length);

      String functionName1 = "DataSection3";
      List inputDataSectionElements = new ArrayList();
      inputDataSectionElements.add("address");
      //TODO need to check the function with multiple data section elements.
      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'DataSection3' has an incorrect number of input " +
         "data section elements: " + function1.getInputDataSectionElements().length,
         1, function1.getInputDataSectionElements().length);

      DataSectionElement[] functionInputDataSectionElements
         = function1.getInputDataSectionElements();

      for (int i = 0; i < functionInputDataSectionElements.length; i++) {
         assertTrue("The input data section element of the function 'DataSection3'" +
            " has an incorrect name: " + 
            functionInputDataSectionElements[i].getName(),
            inputDataSectionElements.contains(functionInputDataSectionElements[i].getName()));
      }

   }

   /**
    * Tests that getOutputDataSectionElements() returns  the correct output data 
    * section for a funtion of the API.
    * @see org.xins.common.spec.Function#getOutputDataSectionElements()
    */
   public void testFunctionsGetOutputDataSectionElements() {

      String functionName = "InvalidResponse";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'Invalidresponse' has an incorrect number of " +
         "output data section elements: " + 
         function.getOutputDataSectionElements().length,
         0, function.getOutputDataSectionElements().length);

      String functionName1 = "DataSection3";
      List outputDataSectionElements = new ArrayList();
      outputDataSectionElements.add("packet");
      outputDataSectionElements.add("envelope");

      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'DataSection3' has an incorrect number of output " +
         "data section elements: " + 
         function1.getOutputDataSectionElements().length,
         2, function1.getOutputDataSectionElements().length);

      DataSectionElement[] functionOutputDataSectionElements =
         function1.getOutputDataSectionElements();

      for (int i = 0; i < functionOutputDataSectionElements.length; i++) {
         assertTrue("The output data section element of the function 'DataSection3'" +
            " has an incorrect name: "
            + functionOutputDataSectionElements[i].getName(),
            outputDataSectionElements.contains(functionOutputDataSectionElements[i].getName()));
      }

   }

   /**
    * Tests that getOutputDataSectionElements() returns the correct output data
    * section for a funtion of the API. This test case tests a function which has 
    * one data section element and one sub-element for output.
    * @see org.xins.common.spec.Function#getOutputDataSectionElements()
    */
   public void testFunctionsGetOutputDataSecElementsWithOneElementAndSubElements() {

      String functionName = "DataSection2";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'DataSection2' has an incorrect number of output " +
         "data section elements: " + function.getOutputDataSectionElements().length,
         1, function.getOutputDataSectionElements().length);
   }

   
   
   /**
    * Tests that getInputParamCombos() returns the correct input param combo
    * of a function of the API.
    * @see org.xins.common.spec.Function#getInputParamCombos()
    */
   public void testFunctionsInputParamCombos() {

      String functionName = "InvalidResponse";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'InvalidResponse' has an incorrect number of input " +
         "paramter combos: " + function.getInputParamCombos().length,
         0, function.getInputParamCombos().length);

      String functionName1 = "ParamCombo";
      int exclusiveCount = 0;
      int inclusiveCount = 0;
      int allCount = 0;

      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'ParamCombo' has an incorrect number of input " +
         "paramter combos: " + function1.getInputParamCombos().length,
         3, function1.getInputParamCombos().length);

      ParamCombo[] functionInputParamCombos = function1.getInputParamCombos();
      for (int i = 0; i < functionInputParamCombos.length; i++) {
         ParamCombo combo = functionInputParamCombos[i];
         if (combo.isExclusiveOr()) {
            exclusiveCount++;
         } else if (combo.isInclusiveOr()) {
            inclusiveCount++;
         } else if (combo.isAllOrNone()) {
            allCount++;
         }
      }
      assertEquals("Function 'ParamCombo' has an incorrect number of exclusive " +
         "input paramter combos: " + exclusiveCount, 1, exclusiveCount);
      assertEquals("Function 'ParamCombo' has an incorrect number of inclusive " +
         "input paramter combos: " + inclusiveCount, 1, inclusiveCount);
      assertEquals("Function 'ParamCombo' has an incorrect number of all input " +
         "paramter combos: " + allCount, 1, allCount);
   }

   /**
    * Tests that getOutputParamCombos() returns the correct output param combo
    * of a function of the API.
    * @see org.xins.common.spec.Function#getOutputParamCombos()
    */
   public void testFunctionsOutputParamCombos() {

      String functionName = "InvalidResponse";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("Function 'InvalidResponse' has an incorrect number of " +
         "output paramter combos: " + function.getOutputParamCombos().length,
         0, function.getOutputParamCombos().length);

      String functionName1 = "ParamCombo";
      int exclusiveCount = 0;
      int inclusiveCount = 0;
      int allCount = 0;

      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("Function 'ParamCombo' has an incorrect number of " +
         "output paramter combos: " + function1.getOutputParamCombos().length,
         2, function1.getOutputParamCombos().length);

      ParamCombo[] functionOutputParamCombos = function1.getOutputParamCombos();
      for (int i = 0; i < functionOutputParamCombos.length; i++) {
         ParamCombo combo = functionOutputParamCombos[i];
         if (combo.isExclusiveOr()) {
            exclusiveCount++;
         } else if (combo.isInclusiveOr()) {
            inclusiveCount++;
         } else if (combo.isAllOrNone()) {
            allCount++;
         }
      }
      
      assertEquals("Function 'ParamCombo' has an incorrect number of exclusive " +
         "input paramter combos: " + exclusiveCount, 1, exclusiveCount);
      assertEquals("Function 'ParamCombo' has an incorrect number of inclusive " +
         "input paramter combos: " + inclusiveCount, 0, inclusiveCount);
      assertEquals("Function 'ParamCombo' has an incorrect number of all input " +
         "paramter combos: " + allCount, 1, allCount);
   }

}


