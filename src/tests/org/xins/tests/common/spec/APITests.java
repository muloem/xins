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

   /**
    * Holds a reference to the allInone API for further questioning.
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
    * Tests that the getName() returns the correct name of the API.
    * @see org.xins.common.spec.API#getName()
    */
   public void testAPIGetName() {
      assertEquals("Incorrect API name: " + _allInOneAPI.getName(), 
         "allinone", _allInOneAPI.getName());
   }

   /**
    * Tests that the getOwner() returns the correct owner of the API.
    * @see org.xins.common.spec.API#getOwner()
    */
   public void testAPIGetOwner() {
      assertEquals("Incorrect Owner of the API: " + _allInOneAPI.getOwner(), 
         "johnd", _allInOneAPI.getOwner());
   }

   /**
    * Tests that the getDescription() returns the correct description of the API.
    * @see org.xins.common.spec.API#getDescription()
    */
   public void testAPIGetDescription() {
      assertEquals("Incorrect description of the API: " + 
         _allInOneAPI.getDescription(), 
         "API that uses all the features included in XINS.", 
         _allInOneAPI.getDescription());
   }

   /**
    * Tests that the getFunctions() and the getFunction(String) returns the 
    * correct functions of the API.
    * @see org.xins.common.spec.API#getFunctions()
    * @see org.xins.common.spec.API#getFunction(String)
    *
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

      assertEquals("Incorrect number of functions in the API: " + 
         _allInOneAPI.getFunctions().length, 
         _allInOneAPI.getFunctions().length, list.size());
      int i = 0;
      try {
         for (i = 0; i < list.size(); i++) {
            String functionName = (String) list.get(i);
            assertEquals("Incorrect function name in the API: " + _allInOneAPI.getFunction(functionName).getName(),
               _allInOneAPI.getFunction(functionName).getName(), functionName);
         }
      } catch (IllegalArgumentException exc) {
         fail("Could not find the function " + list.get(i) + " in allInOneAPI.");
      }
   }
   
   /**
    * Tests the backword compatability with older version of xins.
    * The system is actualy not backword compatible so it throws an exception.
    */
   public void testCompatibility() {
   	
      try {
      TargetDescriptor target = new TargetDescriptor("http://www.xins.org");
      com.mycompany.myproject.capi.CAPI capi = 
         new com.mycompany.myproject.capi.CAPI(target);
      capi.getAPISpecification();
      } catch (InvalidSpecificationException e) {
         //Expecting exception	
      } catch (Exception e) {
         fail("Unexpected exception occurs: " + e.getMessage());      	
      }
   }
   
}


