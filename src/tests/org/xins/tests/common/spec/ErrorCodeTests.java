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
 * API spec TestCase. The testcase assumes that the example api allinone is
 * the api being questioned for meta information like name, functions and so on.
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
    * Hold a reference to the API for further questioning.
    */
   private static API allInOneAPI;
   private static ErrorCode errorCode;
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
      TargetDescriptor target = new TargetDescriptor("http://127.0.0.1:8080/",
                                     5000,
                                     1000,
                                     4000);
      CAPI allInOne = new CAPI(target);
      allInOneAPI = allInOne.getAPISpecification();
      String functionName = "ResultCode";
      Function function = allInOneAPI.getFunction(functionName);
      errorCode = function.getErrorCodes()[0];
   }

   /**
    * @see org.xins.common.spec.ErrorCode#getName()
    */
   public void testErrorCodeGetName() {
      assertEquals("AlreadySet", errorCode.getName());
   }

   /**
    * @see org.xins.common.spec.ErrorCode#getDescription()
    */
   public void testErrorCodeGetDescription() {
      assertEquals("The parameter has already been given.", errorCode.getDescription());
   }

   /**
    * @see org.xins.common.spec.ErrorCode#getOutputParameters()
    */
   public void testErrorCodeGetOutputParameters() throws Exception {
      Parameter[] outputParam = errorCode.getOutputParameters();
      
      assertEquals(1, outputParam.length);
      assertEquals("count", outputParam[0].getName());
      assertEquals("The number of times that the parameter was already passed.", outputParam[0].getDescription());
      assertTrue(outputParam[0].isRequired());
      assertTrue(outputParam[0].getType() instanceof Int32);
   }

   /**
    * @see org.xins.common.spec.ErrorCode#getOutputDataSection()
    */
   public void testErrorCodeGetOutputDataSection() {
      //TODO an example has to be added in allinone, then the test shall be written
      fail("No test case defined in allinone API.");
   }

}


