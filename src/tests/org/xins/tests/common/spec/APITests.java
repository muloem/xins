/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.spec.API;
import org.xins.common.spec.Function;
import org.xins.common.spec.InvalidSpecificationException;

import com.mycompany.allinone.capi.CAPI;

/**
 * API spec TestCase. The testcases use the <i>allinone</i> API to test 
 * the API specification.
 *
 * @version $Revision$ $Date$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 * @author Tauseef Rehman (<a href="mailto:tauseef.rehman@nl.wanadoo.com">tauseef.rehman@nl.wanadoo.com</a>)
 */
public class APITests extends TestCase {

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
    * Tests that {@link API#getName() getName()} returns the correct 
    * name of the API.
    */
   public void testAPIGetName() {
      assertEquals("The API has an incorrect name: " + _allInOneAPI.getName(), 
         "allinone", _allInOneAPI.getName());
   }

   /**
    * Tests that {@link API#getOwner()() getOwner()} returns the correct 
    * owner of the API.
    */
   public void testAPIGetOwner() {
      assertEquals("The API has an incorrect owner: " + _allInOneAPI.getOwner(), 
         "johnd", _allInOneAPI.getOwner());
   }

   /**
    * Tests that {@link API#getDescription() getDescription()} returns the 
    * correct description of the API.
    */
   public void testAPIGetDescription() {
      assertEquals("The API has an incorrect description: " + 
         _allInOneAPI.getDescription(), 
         "API that uses all the features included in XINS.", 
         _allInOneAPI.getDescription());
   }

   /**
    * Tests that {@link API#getFunctions() getFunctions()} and the 
    * {@link API#getFunction(String) getFunction(String)} returns the 
    * correct functions of the API.
    */
   public void testAPIGetFunctions() {
      ArrayList list = new ArrayList();
      list.add("DataSection");
      list.add("DataSection2");
      list.add("DataSection3");
      list.add("DefinedTypes");
      list.add("InvalidResponse");
      list.add("Logdoc");
      list.add("ParamCombo");
      list.add("ParamComboNotAll");
      list.add("ResultCode");
      list.add("RuntimeProps");
      list.add("SimpleOutput");
      list.add("SimpleTypes");

      Function[] functions = _allInOneAPI.getFunctions();

      for (int i = 0; i < functions.length; i++) {
         Function function = functions[i];
         assertTrue(list.contains(function.getName()));
      }

      try {
         _allInOneAPI.getFunction("RubbishName");
         fail("Expected getFunction to throw an IllegalArgumentException");
      } catch (IllegalArgumentException e) {
         // consume, this is valid
      }

      assertEquals("The API has an incorrect number of the functions: " + 
         _allInOneAPI.getFunctions().length, 
         _allInOneAPI.getFunctions().length, list.size());
      int i = 0;
      try {
         for (i = 0; i < list.size(); i++) {
            String functionName = (String) list.get(i);
            assertEquals("The function in the API has an incorrect name:" +
               _allInOneAPI.getFunction(functionName).getName(),
               _allInOneAPI.getFunction(functionName).getName(), functionName);
         }
      } catch (IllegalArgumentException exc) {
         fail("Could not find the function " + list.get(i) + " in allInOneAPI.");
      }
   }
   
   /**
    * Tests the backward compatibility with older version of xins.
    * The system is actualy not backward compatible so it throws an exception.
    */
   public void testCompatibility() {
   	
      try {
         TargetDescriptor target = new TargetDescriptor("http://www.xins.org");
         com.mycompany.myproject.capi.CAPI capi = 
            new com.mycompany.myproject.capi.CAPI(target);
         capi.getAPISpecification();
         fail("Calling an older version of CAPI should throw an exception.");
      } catch (InvalidSpecificationException e) {
         //Expecting exception	
      } catch (Exception e) {
         fail("Unexpected exception occurs: " + e.getMessage());      	
      }      	
   }
   
}


