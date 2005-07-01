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
import org.xins.common.spec.Function;
import org.xins.common.spec.ParamCombo;
import org.xins.common.spec.Parameter;

import com.mycompany.allinone.capi.CAPI;

/**
 * API spec TestCase. The testcase assumes that the example api allinone is
 * the api being questioned for meta information like name, functions and so on.
 *
 * @version $Revision$ $Date$
 * @author Mees Witteman (<a href="mailto:mees.witteman@nl.wanadoo.com">mees.witteman@nl.wanadoo.com</a>)
 * @author Tauseef Rehman (<a href="mailto:tauseef.rehman@nl.wanadoo.com">tauseef.rehman@nl.wanadoo.com</a>)
 */
public class ParamComboTests extends TestCase {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------


   /**
    * Hold a reference to the API for further questioning.
    */
   private static API allInOneAPI;
   private static ParamCombo[] parameterCombo;
   private static ParamCombo exclusiveCombo;
   private static ParamCombo inclusiveCombo;
   private static ParamCombo allornoneCombo;
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
      allInOneAPI = allInOne.getAPISpecification();

      String functionName = "ParamCombo";
      Function function = allInOneAPI.getFunction(functionName);
      parameterCombo = function.getInputParamCombos();
      for (int i = 0; i < parameterCombo.length; i++) {
         ParamCombo combo = parameterCombo[i];
         if (combo.isExclusiveOr()) {
            exclusiveCombo = parameterCombo[i];
         } else if (combo.isInclusiveOr()) {
            inclusiveCombo = parameterCombo[i];
         } else if (combo.isAllOrNone()) {
            allornoneCombo = parameterCombo[i];
         }
      }

   }

   /**
    * @see org.xins.common.spec.ParamCombo#isExclusiveOr()
    */
   public void testErrorCodeIsExclusiveOr() {
      assertTrue(exclusiveCombo.isExclusiveOr());
      assertFalse(inclusiveCombo.isExclusiveOr());
      assertFalse(allornoneCombo.isExclusiveOr());
   }

   /**
    * @see org.xins.common.spec.ParamCombo#isInclusiveOr()
    */
   public void testErrorCodeIsInclusiveOr() {
      assertTrue(inclusiveCombo.isInclusiveOr());
      assertFalse(exclusiveCombo.isInclusiveOr());
      assertFalse(allornoneCombo.isInclusiveOr());
   }

   /**
    * @see org.xins.common.spec.ParamCombo#isAllOrNone()
    */
   public void testErrorCodeIsAllOrNode() {
      assertTrue(allornoneCombo.isAllOrNone());
      assertFalse(inclusiveCombo.isAllOrNone());
      assertFalse(exclusiveCombo.isAllOrNone());
   }

   /**
    * @see org.xins.common.spec.ParamCombo#getParameters()
    */
   public void testErrorCodeGetParameters() {
      assertEquals(3, exclusiveCombo.getParameters().length);
      Parameter[] params = exclusiveCombo.getParameters();
      List paramNames = new ArrayList();
      for (int i = 0; i < params.length; i++) {
         paramNames.add(params[i].getName());
      }
      assertTrue(paramNames.contains("birthDate"));
      assertTrue(paramNames.contains("birthYear"));
      assertTrue(paramNames.contains("age"));

      assertEquals(2, inclusiveCombo.getParameters().length);
      assertEquals(3, allornoneCombo.getParameters().length);
   }

}


