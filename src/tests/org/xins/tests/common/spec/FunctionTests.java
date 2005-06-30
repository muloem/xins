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

import org.xins.common.spec.API;
import org.xins.common.spec.DataSectionElement;
import org.xins.common.spec.ErrorCode;
import org.xins.common.spec.Function;
import org.xins.common.spec.ParamCombo;
import org.xins.common.spec.Parameter;

/**
 * API spec TestCase. The testcase assumes that the example api allinone is
 * the api being questioned for meta information like name, functions and so on.
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
      allInOneAPI = new API(null);
   }


   /**
    * @see org.xins.common.spec.Function#getName()
    */
   public void testFunctionsGetName() throws Exception {

      String functionName = "DataSection";
      Function function = allInOneAPI.getFunction(functionName);
      assertEquals(functionName, function.getName());
   }

   /**
    * @see org.xins.common.spec.Function#getDescription()
    */
   public void testFunctionsGetDescription() throws Exception {

      String functionName = "DataSection";
      String functionDescription = "An example of a data section.";
      Function function = allInOneAPI.getFunction(functionName);
      assertEquals(functionDescription, function.getDescription());
   }

   /**
    * @see org.xins.common.spec.Function#getInputParameters()
    */
   public void testFunctionsGetInputParameters() throws Exception {

      String functionName = "SimpleOutput";
      Function function = allInOneAPI.getFunction(functionName);
      assertEquals(0, function.getInputParameters().length);

      String functionName1 = "DefinedTypes";
      List parameters = new ArrayList();
      parameters.add("inputIP");
      parameters.add("inputSalutation");
      parameters.add("inputAge");
      parameters.add("inputList");

      Function function1 = allInOneAPI.getFunction(functionName1);
      assertEquals(4, function1.getInputParameters().length);

      Parameter[] functionParameters = function1.getInputParameters();
      assertEquals(parameters.size(), functionParameters.length);

      for (int i = 0; i < functionParameters.length; i++) {
         assertTrue(parameters.contains(functionParameters[i].getName()));
      }

   }

   /**
    * @see org.xins.common.spec.Function#getOutputParameters()
    */
   public void testFunctionsGetOutputParameters() throws Exception {

      String functionName = "DataSection";
      Function function = allInOneAPI.getFunction(functionName);
      assertEquals(0, function.getOutputParameters().length);

      String functionName1 = "DefinedTypes";
      List parameters = new ArrayList();
      parameters.add("outputIP");
      parameters.add("outputSalutation");
      parameters.add("outputAge");
      parameters.add("outputList");

      Function function1 = allInOneAPI.getFunction(functionName1);
      assertEquals(4, function1.getOutputParameters().length);

      Parameter[] functionParameters = function1.getOutputParameters();
      assertEquals(parameters.size(), functionParameters.length);

      for (int i = 0; i < functionParameters.length; i++) {
         assertTrue(parameters.contains(functionParameters[i].getName()));
      }

   }

   /**
    * @see org.xins.common.spec.Function#getErrorCodes()
    */
   public void testFunctionsGetErrorCodes() throws Exception {

      String functionName = "DataSection";
      Function function = allInOneAPI.getFunction(functionName);
      assertEquals(0, function.getErrorCodes().length);

      String functionName1 = "ResultCode";
      List errorCodes = new ArrayList();
      errorCodes.add("AlreadySet");
      //TODO need to check the function with multiple error codes.
      Function function1 = allInOneAPI.getFunction(functionName1);
      assertEquals(1, function1.getErrorCodes().length);

      ErrorCode[] functionErrorCodes = function1.getErrorCodes();
      assertEquals(errorCodes.size(), functionErrorCodes.length);

      for (int i = 0; i < functionErrorCodes.length; i++) {
         assertTrue(errorCodes.contains(functionErrorCodes[i].getName()));
      }

   }

   /**
    * @see org.xins.common.spec.Function#getInputDataSectionElements()
    */
   public void testFunctionsGetInputDataSectionElements() throws Exception {

      String functionName = "DataSection";
      Function function = allInOneAPI.getFunction(functionName);
      assertEquals(0, function.getInputDataSectionElements().length);

      String functionName1 = "DataSection3";
      List inputDataSectionElements = new ArrayList();
      inputDataSectionElements.add("address");
      //TODO need to check the function with multiple data section elements.
      Function function1 = allInOneAPI.getFunction(functionName1);
      assertEquals(1, function1.getInputDataSectionElements().length);

      DataSectionElement[] functionInputDataSectionElements
         = function1.getInputDataSectionElements();
      assertEquals(inputDataSectionElements.size(), functionInputDataSectionElements.length);

      for (int i = 0; i < functionInputDataSectionElements.length; i++) {
         assertTrue(inputDataSectionElements.contains(functionInputDataSectionElements[i].getName()));
      }

   }

   /**
    * @see org.xins.common.spec.Function#getOutputDataSectionElements()
    */
   public void testFunctionsGetOutputDataSectionElements() throws Exception {

      String functionName = "InvalidResponse";
      Function function = allInOneAPI.getFunction(functionName);
      assertEquals(0, function.getOutputDataSectionElements().length);

      String functionName1 = "DataSection3";
      List outputDataSectionElements = new ArrayList();
      outputDataSectionElements.add("packet");
      outputDataSectionElements.add("envelope");

      Function function1 = allInOneAPI.getFunction(functionName1);
      assertEquals(2, function1.getOutputDataSectionElements().length);

      DataSectionElement[] functionOutputDataSectionElements =
         function1.getOutputDataSectionElements();
      assertEquals(outputDataSectionElements.size(), functionOutputDataSectionElements.length);

      for (int i = 0; i < functionOutputDataSectionElements.length; i++) {
         assertTrue(outputDataSectionElements.contains(functionOutputDataSectionElements[i].getName()));
      }

   }

   /**
    * @see org.xins.common.spec.Function#getInputParamCombos()
    */
   public void testFunctionsInputParamCombos() throws Exception {

      String functionName = "InvalidResponse";
      Function function = allInOneAPI.getFunction(functionName);
      assertEquals(0, function.getInputParamCombos().length);

      String functionName1 = "ParamCombo";
      int exclusiveCount = 0;
      int inclusiveCount = 0;
      int allCount = 0;

      Function function1 = allInOneAPI.getFunction(functionName1);
      assertEquals(3, function1.getInputParamCombos().length);

      ParamCombo[] functionInputParamCombos = function1.getInputParamCombos();
      for (int i = 0; i < functionInputParamCombos.length; i++) {
         ParamCombo combo = functionInputParamCombos[i];
         if (combo.isExclusiveOr()) {
            exclusiveCount++;
         } else if(combo.isInclusiveOr()) {
            inclusiveCount++;
         } else if(combo.isAllOrNone()) {
            allCount++;
         }
      }
      assertEquals(1, exclusiveCount);
      assertEquals(1, inclusiveCount);
      assertEquals(1, allCount);
   }

   /**
    * @see org.xins.common.spec.Function#getOutputParamCombos()
    */
   public void testFunctionsOutputParamCombos() throws Exception {

      String functionName = "InvalidResponse";
      Function function = allInOneAPI.getFunction(functionName);
      assertEquals(0, function.getOutputParamCombos().length);

      String functionName1 = "ParamCombo";
      int exclusiveCount = 0;
      int inclusiveCount = 0;
      int allCount = 0;

      Function function1 = allInOneAPI.getFunction(functionName1);
      assertEquals(2, function1.getOutputParamCombos().length);

      ParamCombo[] functionOutputParamCombos = function1.getOutputParamCombos();
      for (int i = 0; i < functionOutputParamCombos.length; i++) {
         ParamCombo combo = functionOutputParamCombos[i];
         if (combo.isExclusiveOr()) {
            exclusiveCount++;
         } else if(combo.isInclusiveOr()) {
            inclusiveCount++;
         } else if(combo.isAllOrNone()) {
            allCount++;
         }
      }
      assertEquals(1, exclusiveCount);
      assertEquals(0, inclusiveCount);
      assertEquals(1, allCount);
   }

}


