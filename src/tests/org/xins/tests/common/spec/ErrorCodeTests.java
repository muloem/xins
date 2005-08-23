/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import com.mycompany.allinone.capi.CAPI;
import java.util.Map;

import junit.framework.TestCase;
import org.xins.common.service.TargetDescriptor;

import org.xins.common.spec.APISpec;
import org.xins.common.spec.DataSectionElementSpec;
import org.xins.common.spec.EntityNotFoundException;
import org.xins.common.spec.ErrorCodeSpec;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.ParameterSpec;
import org.xins.common.types.standard.Int32;

/**
 * ErrorCode spec TestCase. The testcases use the <i>allinone</i> API 
 * to test the API specification.
 *
 * @version $Revision$ $Date$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 * @author Tauseef Rehman (<a href="mailto:tauseef.rehman@nl.wanadoo.com">tauseef.rehman@nl.wanadoo.com</a>)
 */
public class ErrorCodeTests extends TestCase {

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
    * The Error Code specification of the <i>ResultCode</i> function.
    */
   private ErrorCodeSpec _errorCode;


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
      APISpec allInOneAPI = allInOne.getAPISpecification();
      String functionName = "ResultCode";
      FunctionSpec function = allInOneAPI.getFunction(functionName);
      _errorCode = function.getErrorCode("AlreadySet");
   }

   /**
    * Tests that the {@link ErrorCode#getName() getName()} returns the correct 
    * name of the error code of a function of the API.
    */
   public void testErrorCodeGetName() {
      assertEquals("Function 'ResultCode' has an incorrect error code name: " + 
         _errorCode.getName(), "AlreadySet", _errorCode.getName());
   }

   /**
    * Tests that the {@link ErrorCode#getDescription() getDescription()} returns 
    * the correct description of the error code of a function of the API.
    */
   public void testErrorCodeGetDescription() {
      assertEquals("Function 'ResultCode' has an incorrect error code description: "
         + _errorCode.getDescription(), "The parameter has already been given.",
         _errorCode.getDescription());
   }

   /**
    * Tests that the {@link ErrorCode#getOutputParameters() getOutputParameters()}
    * returns the correct output parameters of the error code of a function of 
    * the API.
    */
   public void testErrorCodeGetOutputParameters() throws Exception {
      Map outputParams = _errorCode.getOutputParameters();
      ParameterSpec outputParam = _errorCode.getOutputParameter("count");
      
      assertEquals("The error code in the function 'ResultCode' has an incorrect " +
         "number of the parameters: " + outputParams.size(), 1, outputParams.size());
      assertEquals("The output parameter of the error code in the function " +
         "'ResultCode' has an incorrect name: " + outputParam.getName(),
         "count", outputParam.getName());
      assertEquals("The output parameter of the error code in the function " +
         "'ResultCode' has an incorrect description: " + outputParam.getDescription(),
         "The number of times that the parameter was already passed.", 
         outputParam.getDescription());
      assertTrue("The output parameter of the error code in the function " +
         "'ResultCode' has an 'is required' value: ",
         outputParam.isRequired());
      assertTrue("The output parameter of the error code in the function " +
         "'ResultCode' has an incorrect type: ",
         outputParam.getType() instanceof Int32);
   }

   /**
    * Tests that the  {@link ErrorCode#getOutputParameter(String) getOutputParameter(String)}
    * returns the correct output parameters of the error code of a function of 
    * the API when given an output parameter name.
    */
   public void testErrorCodeGetOutputParameter() {
      try {
         _errorCode.getOutputParameter("NoName"); 
         fail("The erorr code in the function 'ResultCode' contains an output " +
            "paramter which is not specified.");
      } catch (EntityNotFoundException e) {
         //expecting exception
      }
      try {
         ParameterSpec outputParam = _errorCode.getOutputParameter("count");
         assertEquals("The ouput parameter of the error code in the function " +
            "'ResultCode', has an incorrect name: " + outputParam.getName(),
            "count", outputParam.getName());
      } catch (EntityNotFoundException e) {
        fail("The erorr code in the function 'ResultCode' does not contain an " +
           "output paramter 'count' which was specified.");
      }
   }
   
   /**
    * Tests the {@link ErrorCode#getOutputDataSectionElements() getOutputDataSection()}
    * return the correct output datasection of the error code of a function of 
    * the API.
    */
/*
   public void testErrorCodeGetOutputDataSectionElements() {
      //TODO an example has to be added in allinone, then the test shall be written
   }
*/
   
   /**
    * Tests the {@link ErrorCodeSpec#getOutputDataSectionElement(String) getOutputDataSection()}
    * return the correct output datasection of the error code of a function of 
    * the API when specified with a name.
    */
/*
   public void testErrorCodeGetOutputDataSectionElements() {
      //TODO an example has to be added in allinone, then the test shall be written
   }
*/

}


