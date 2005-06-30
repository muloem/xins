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
import org.xins.common.spec.Function;

/**
 * API spec TestCase. The testcase assumes that the example api allinone is
 * the api being questioned for meta information like name, functions and so on.
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
    * Hold a reference to the API for further questioning.
    */
   private static API allInOneAPI;

   /**
    * Hu TODO.
    */
   private static List functionNameList;
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
   }


   /**
    * @see org.xins.common.spec.API#getName()
    */
   public void testAPIGetName() {
      assertEquals("allinone", allInOneAPI.getName());
   }

   /**
    * @see org.xins.common.spec.API#getOwner()
    */
   public void testAPIGetOwner() {
      assertEquals("johnd", allInOneAPI.getOwner());
   }

   /**
    * @see org.xins.common.spec.API#getDescription()
    */
   public void testAPIGetDescription() {
      assertEquals("API that uses all the features included in XINS.", allInOneAPI.getDescription());
   }

   /**
    * @see org.xins.common.spec.API#getFunctions()
    * @see org.xins.common.spec.API#getFunction(String)
    */
   public void testAPIGetFunctions() throws Exception {
      ArrayList list = new ArrayList();
      list.add("DataSection");
      list.add("DataSection2");
      list.add("DataSection3");
      list.add("DefinedTypes");
      list.add("InvalidResponse");
      list.add("Logdoc");
      list.add("ParamCombo");
      list.add("ResultCode");
      list.add("RuntimeProps");
      list.add("SimpleOutput");
      list.add("SimpleTypes");

      Function[] functions = allInOneAPI.getFunctions();

      for (int i = 0; i < functions.length; i++) {
         Function function = functions[i];
         assertTrue(list.contains(function.getName()));
      }

      try {
         allInOneAPI.getFunction("RubbishName");
         fail("Expected getFunction to throw an IllegalArgumentException");
      } catch (IllegalArgumentException e) {
         // consume, this is valid
      }

      assertEquals(allInOneAPI.getFunctions().length, list.size());
      int i = 0;
      try {
         for (i = 0; i < list.size(); i++) {
            String functionName = (String)list.get(i);
            assertEquals(allInOneAPI.getFunction(functionName).getName(),functionName);
         }
      } catch (IllegalArgumentException exc) {
         fail("Could not find the function " + list.get(i) + " in allInOneAPI.");
      }
   }








































}


