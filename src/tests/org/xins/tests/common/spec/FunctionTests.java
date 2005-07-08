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

   /**
    * Holds a reference to the API for further questioning.
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
    * Tests that the getName() returns the correct name of a function
    * of the API.
    * @see org.xins.common.spec.Function#getName()
    */
   public void testFunctionsGetName() {

      String functionName = "DataSection";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("For allinonoe API, incorrect function name: " + function.getName(),
         functionName, function.getName());
   }

   /**
    * Tests that the getDescription() returns the correct description of a function
    * of the API.
    * @see org.xins.common.spec.Function#getDescription()
    */
   public void testFunctionsGetDescription() {

      String functionName = "DataSection";
      String functionDescription = "An example of a data section.";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("For allinonoe API's 'DataSection' function, incorrect function description: " + function.getDescription(),
         functionDescription, function.getDescription());
   }

   /**
    * Tests that getInputParameters() returns correct input parameters of a 
    * function of the API
    * @see org.xins.common.spec.Function#getInputParameters()
    */
   public void testFunctionsGetInputParameters() {

      String functionName = "SimpleOutput";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("For function 'SimpleOutput', incorrect number of input paramter: " + function.getInputParameters().length,
         0, function.getInputParameters().length);

      String functionName1 = "DefinedTypes";
      List parameters = new ArrayList();
      parameters.add("inputIP");
      parameters.add("inputSalutation");
      parameters.add("inputAge");
      parameters.add("inputList");

      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("For function 'DefinedTypes', incorrect number of input paramter: " + function.getInputParameters().length,
         parameters.size(), function1.getInputParameters().length);

      Parameter[] functionParameters = function1.getInputParameters();

      for (int i = 0; i < functionParameters.length; i++) {
         assertTrue("For allInOne API, incorrect function name: " + functionParameters[i].getName(),  
            parameters.contains(functionParameters[i].getName()));
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
      assertEquals("For function 'DataSection', incorrect number of output paramter: " + function.getOutputParameters().length,
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
      assertEquals("For function 'DefinedTypes', incorrect number of output paramter: " + functionParameters.length,
         parameters.size(), functionParameters.length);

      for (int i = 0; i < functionParameters.length; i++) {
         assertTrue("For allInOne API, incorrect function name: " + functionParameters[i].getName(),
            parameters.contains(functionParameters[i].getName()));
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
      assertEquals("For function 'DataSection', incorrect number of errorcodes: " + function.getErrorCodes().length,
         0, function.getErrorCodes().length);

      String functionName1 = "ResultCode";
      List errorCodes = new ArrayList();
      errorCodes.add("AlreadySet");
      //TODO need to check the function with multiple error codes.
      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("For function 'AlreadySet', incorrect number of errorcodes: " + function1.getErrorCodes().length,
         1, function1.getErrorCodes().length);

      ErrorCode[] functionErrorCodes = function1.getErrorCodes();

      for (int i = 0; i < functionErrorCodes.length; i++) {
         assertTrue("In fucntion 'AlreadySet', incorrect errorcode name: " + functionErrorCodes[i].getName(),
            errorCodes.contains(functionErrorCodes[i].getName()));
      }

   }

   /**
    * Tests that getInputDataSectionElements() returns correct input datasection
    * of a funtion of the API.
    * @see org.xins.common.spec.Function#getInputDataSectionElements()
    */
   public void testFunctionsGetInputDataSectionElements() {

      String functionName = "DataSection";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("For function 'DataSection', incorrect number of input datasection elements: " + function.getInputDataSectionElements().length,
         0, function.getInputDataSectionElements().length);

      String functionName1 = "DataSection3";
      List inputDataSectionElements = new ArrayList();
      inputDataSectionElements.add("address");
      //TODO need to check the function with multiple data section elements.
      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("For function 'DataSection3', incorrect number of input datasection elements: " + function1.getInputDataSectionElements().length,
         1, function1.getInputDataSectionElements().length);

      DataSectionElement[] functionInputDataSectionElements
         = function1.getInputDataSectionElements();

      for (int i = 0; i < functionInputDataSectionElements.length; i++) {
         assertTrue("For function 'DataSection3', incorrect input datasection name: " + functionInputDataSectionElements[i].getName(),
            inputDataSectionElements.contains(functionInputDataSectionElements[i].getName()));
      }

   }

   /**
    * Tests that getOutputDataSectionElements() returns correct output datasection
    * for a funtion of the API.
    * @see org.xins.common.spec.Function#getOutputDataSectionElements()
    */
   public void testFunctionsGetOutputDataSectionElements() {

      String functionName = "InvalidResponse";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("For function 'Invalidresponse', incorrect number of output datasection elements: " + function.getOutputDataSectionElements().length,
         0, function.getOutputDataSectionElements().length);

      String functionName1 = "DataSection3";
      List outputDataSectionElements = new ArrayList();
      outputDataSectionElements.add("packet");
      outputDataSectionElements.add("envelope");

      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("For function 'DataSection3', incorrect number of output datasection elements: " + function1.getOutputDataSectionElements().length,
         2, function1.getOutputDataSectionElements().length);

      DataSectionElement[] functionOutputDataSectionElements =
         function1.getOutputDataSectionElements();

      for (int i = 0; i < functionOutputDataSectionElements.length; i++) {
         assertTrue("For function 'DataSection3', incorrect output datasection name: " + functionOutputDataSectionElements[i].getName(),
            outputDataSectionElements.contains(functionOutputDataSectionElements[i].getName()));
      }

   }

   /**
    * Tests that getOutputDataSectionElements() returns correct output datasection
    * for a funtion of the API. This test case tests a function which has one 
    * datasection element and one sub-element for output.
    * @see org.xins.common.spec.Function#getOutputDataSectionElements()
    */
   public void testFunctionsGetOutputDataSecElementsWithOneElementAndSubElements() {

      String functionName = "DataSection2";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("For function 'DataSection2', incorrect number of output datasection elements: " + function.getOutputDataSectionElements().length,
         1, function.getOutputDataSectionElements().length);
   }

   
   
   /**
    * Tests that getInputParamCombos() returns the correct input parameter combo
    * of a function of the API.
    * @see org.xins.common.spec.Function#getInputParamCombos()
    */
   public void testFunctionsInputParamCombos() {

      String functionName = "InvalidResponse";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("For function 'InvalidResponse', incorrect number of input paramter combos: " + function.getInputParamCombos().length,
         0, function.getInputParamCombos().length);

      String functionName1 = "ParamCombo";
      int exclusiveCount = 0;
      int inclusiveCount = 0;
      int allCount = 0;

      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("For function 'ParamCombo', incorrect number of input paramter combos: " + function1.getInputParamCombos().length,
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
      assertEquals("For function 'ParamCombo', incorrect number of exclusive input paramter combo: " + exclusiveCount,
         1, exclusiveCount);
      assertEquals("For function 'ParamCombo', incorrect number of inclusive input paramter combo: " + inclusiveCount,
         1, inclusiveCount);
      assertEquals("For function 'ParamCombo', incorrect number of all input paramter combo: " + allCount,
         1, allCount);
   }

   /**
    * Tests that getOutputParamCombos() returns the correct output parameter combo
    * of a function of the API.
    * @see org.xins.common.spec.Function#getOutputParamCombos()
    */
   public void testFunctionsOutputParamCombos() {

      String functionName = "InvalidResponse";
      Function function = _allInOneAPI.getFunction(functionName);
      assertEquals("For function 'InvalidResponse', incorrect number of output paramter combos: " + function.getOutputParamCombos().length,
         0, function.getOutputParamCombos().length);

      String functionName1 = "ParamCombo";
      int exclusiveCount = 0;
      int inclusiveCount = 0;
      int allCount = 0;

      Function function1 = _allInOneAPI.getFunction(functionName1);
      assertEquals("For function 'ParamCombo', incorrect number of output paramter combos: " + function1.getOutputParamCombos().length,
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
      
      assertEquals("For function 'ParamCombo', incorrect number of exclusive input paramter combo: " + exclusiveCount,
         1, exclusiveCount);
      assertEquals("For function 'ParamCombo', incorrect number of inclusive input paramter combo: " + inclusiveCount,
         0, inclusiveCount);
      assertEquals("For function 'ParamCombo', incorrect number of all input paramter combo: " + allCount,
         1, allCount);
   }

}


