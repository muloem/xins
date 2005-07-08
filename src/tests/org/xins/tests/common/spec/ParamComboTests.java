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
 * ParamCombo spec TestCase. The testcases use the <i>allinone</i> API 
 * to test the API specification.
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
    * Holds a reference to the exclusive parameter combo of the API
    * for further questioning.
    */
   private ParamCombo _exclusiveCombo;
   
   /**
    * Holds a reference to the inclusive parameter combo of the API
    * for further questioning.
    */
   private static ParamCombo _inclusiveCombo;
   
   /**
    * Holds a reference to the allornone parameter combo of the API
    * for further questioning.
    */
   private static ParamCombo _allOrNoneCombo;
   
   
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

      String functionName = "ParamCombo";
      Function function = allInOneAPI.getFunction(functionName);
      ParamCombo[] paramterCombo = function.getInputParamCombos();
      for (int i = 0; i < paramterCombo.length; i++) {
         ParamCombo combo = paramterCombo[i];
         if (combo.isExclusiveOr()) {
            _exclusiveCombo = paramterCombo[i];
         } else if (combo.isInclusiveOr()) {
            _inclusiveCombo = paramterCombo[i];
         } else if (combo.isAllOrNone()) {
            _allOrNoneCombo = paramterCombo[i];
         }
      }

   }

   /**
    * @see org.xins.common.spec.ParamCombo#isExclusiveOr()
    */
   public void testErrorCodeIsExclusiveOr() {
      assertTrue("In parameter combos for function 'ParamCombo', incorrect " +
         "isExclusive", _exclusiveCombo.isExclusiveOr());
      assertFalse("In parameter combos for function 'ParamCombo', incorrect " +
         "isExculsive", _inclusiveCombo.isExclusiveOr());
      assertFalse("In parameter combos for function 'ParamCombo', incorrect " +
         "isExclusive", _allOrNoneCombo.isExclusiveOr());
   }

   /**
    * @see org.xins.common.spec.ParamCombo#isInclusiveOr()
    */
   public void testErrorCodeIsInclusiveOr() {
      assertTrue("In parameter combos for function 'ParamCombo', incorrect " +
         "isInxclusive", _inclusiveCombo.isInclusiveOr());
      assertFalse("In parameter combos for function 'ParamCombo', incorrect " +
         "isInxclusive", _exclusiveCombo.isInclusiveOr());
      assertFalse("In parameter combos for function 'ParamCombo', incorrect " +
         "isInxclusive", _allOrNoneCombo.isInclusiveOr());
   }

   /**
    * @see org.xins.common.spec.ParamCombo#isAllOrNone()
    */
   public void testErrorCodeIsAllOrNode() {
      assertTrue("In parameter combos for function 'ParamCombo', incorrect " +
         "isAllorNone", _allOrNoneCombo.isAllOrNone());
      assertFalse("In parameter combos for function 'ParamCombo', incorrect " +
         "isAllorNone", _inclusiveCombo.isAllOrNone());
      assertFalse("In parameter combos for function 'ParamCombo', incorrect " +
         "isAllorNone", _exclusiveCombo.isAllOrNone());
   }

   /**
    * @see org.xins.common.spec.ParamCombo#getParameters()
    */
   public void testErrorCodeGetParameters() {
      assertEquals(3, _exclusiveCombo.getParameters().length);
      Parameter[] params = _exclusiveCombo.getParameters();
      List paramNames = new ArrayList();
      for (int i = 0; i < params.length; i++) {
         paramNames.add(params[i].getName());
      }
      assertTrue(paramNames.contains("birthDate"));
      assertTrue(paramNames.contains("birthYear"));
      assertTrue(paramNames.contains("age"));

      assertEquals(2, _inclusiveCombo.getParameters().length);
      assertEquals(3, _allOrNoneCombo.getParameters().length);
   }

}


