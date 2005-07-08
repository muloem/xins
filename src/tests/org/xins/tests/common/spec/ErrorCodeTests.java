/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import com.mycompany.allinone.capi.CAPI;

import junit.framework.TestCase;
import org.xins.common.service.TargetDescriptor;

import org.xins.common.spec.API;
import org.xins.common.spec.ErrorCode;
import org.xins.common.spec.Function;
import org.xins.common.spec.Parameter;
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

   /**
    * Holds a reference to the errorCode of a function of the API for 
    * further questioning.
    */
   private ErrorCode _errorCode;
   
   
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
      String functionName = "ResultCode";
      Function function = allInOneAPI.getFunction(functionName);
      _errorCode = function.getErrorCodes()[0];
   }

   /**
    * Tests that the getName() returns the correct name of the errorcode of a
    * function of the API.
    * @see org.xins.common.spec.ErrorCode#getName()
    */
   public void testErrorCodeGetName() {
      assertEquals("For function 'ResultCode', incorrect errorcode name: " + 
         _errorCode.getName(), "AlreadySet", _errorCode.getName());
   }

   /**
    * Tests that the getDescription() returns the correct description of the 
    * errorcode of a function of the API.
    * @see org.xins.common.spec.ErrorCode#getDescription()
    */
   public void testErrorCodeGetDescription() {
      assertEquals("For function 'ResultCode', incorrect errorcode description: "
         + _errorCode.getDescription(), "The parameter has already been given.",
         _errorCode.getDescription());
   }

   /**
    * Tests that the  getOutputParameters() returns the correct output parameters
    * of the errorcode of a function of the API.
    * @see org.xins.common.spec.ErrorCode#getOutputParameters()
    */
   public void testErrorCodeGetOutputParameters() {
      Parameter[] outputParams = _errorCode.getOutputParameters();
      Parameter outputParam = outputParams[0];
      
      assertEquals("For errorcode in function 'ResultCode', incorrect number of " +
         "parameter of the errorcode: " + outputParams.length 
         , 1, outputParams.length);
      assertEquals("For errorcode in function 'ResultCode', incorrect name of the" +
         " parameter of the errorcode: " + outputParam.getName(),
         "count", outputParam.getName());
      assertEquals("For errorcode in function 'ResultCode', incorrect description" +
         " of the parameter of the errorcode: " + outputParam.getDescription(),
         "The number of times that the parameter was already passed.", 
      outputParam.getDescription());
      assertTrue("For errorcode in function 'ResultCode', incorrect 'is required'" +
         " property of the paramter of the errorcode",
         outputParam.isRequired());
      assertTrue("For errorcode in function 'ResultCode', incorrect type of the " +
         "paramter of the errorcode",
         outputParam.getType() instanceof Int32);
   }

   /**
    * Tests that the  getOutputParameter(String) returns the correct output 
    * parameters of the errorcode of a function of the API when given an output
    * parameter name.
    * @see org.xins.common.spec.ErrorCode#getOutputParameter(String)()
    */
   public void testErrorCodeGetOutputParameter() {
      try {
         _errorCode.getOutputParameter("NoName"); 
         fail("For erorrcode in function 'ResultCode', output paramter call " +
            "should throw an exception, as no parameter with 'NoName' " +
            "does not exists");
      } catch (IllegalArgumentException e) {
         //expecting exception
      }
      try {
         Parameter outputParam = _errorCode.getOutputParameter("count");
         assertEquals("For errorcode in function 'ResultCode', incorrect name of" +
         		" the parameter of the errorcode: " + outputParam.getName(),
                "count", outputParam.getName());
      } catch (IllegalArgumentException e) {
        fail("For erorrcode in function 'ResultCode', output paramter call " +
           "should not throw an exception, as parameter 'count' " +
           "does exist.");
      }
   }
   
   /**
    * Tests the getOutputDataSection() return the correct output datasection of the
    * errorcode of a function of the API.
    * @see org.xins.common.spec.ErrorCode#getOutputDataSection()
    */
   public void testErrorCodeGetOutputDataSection() {
      //TODO an example has to be added in allinone, then the test shall be written
   }

}


