/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.spec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.xins.common.service.TargetDescriptor;
import org.xins.common.spec.APISpec;
import org.xins.common.spec.FunctionSpec;
import org.xins.common.spec.ParamComboSpec;
import org.xins.common.spec.ParameterSpec;

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
    * The exclusive input param combo specification of the 
    * <i>ParamCombo</i> function.
    */
   private ParamComboSpec _exclusiveCombo;

   /**
    * The inclusive input param combo specification of the 
    * <i>ParamCombo</i> function.
    */
   private ParamComboSpec _inclusiveCombo;

   /**
    * The all-or-none input param combo specification of the 
    * <i>ParamCombo</i> function.
    */
   private ParamComboSpec _allOrNoneCombo;


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

      String functionName = "ParamCombo";
      FunctionSpec function = allInOneAPI.getFunction(functionName);
      Iterator itParamCombo = function.getInputParamCombos().iterator();
      while (itParamCombo.hasNext()) {
         ParamComboSpec combo = (ParamComboSpec) itParamCombo.next();
         if (combo.isExclusiveOr()) {
            _exclusiveCombo = combo;
         } else if (combo.isInclusiveOr()) {
            _inclusiveCombo = combo;
         } else if (combo.isAllOrNone()) {
            _allOrNoneCombo = combo;
         }
      }
   }

   /**
    * Tests that {@link ParamCombo#isExclusiveOr() isExclusiveOr()} returns 
    * the correct exclusive flag for a param combo.
    */
   public void testErrorCodeIsExclusiveOr() {
      assertTrue("Function 'ParamCombo' has an incorrect exclusive param combo: ",
         _exclusiveCombo.isExclusiveOr());
      assertFalse("Function 'ParamCombo' has an incorrect inclusive param combo: ",
         _inclusiveCombo.isExclusiveOr());
      assertFalse("Function 'ParamCombo' has an incorrect all-or-none param combo: ",
         _allOrNoneCombo.isExclusiveOr());
   }

   /**
    * Tests that {@link ParamCombo#isInclusiveOr() isInclusiveOr()} returns 
    * the correct inclusive flag for a param combo.
    */
   public void testErrorCodeIsInclusiveOr() {
      assertTrue("Function 'ParamCombo' has an incorrect exclusive param combo: ",
         _inclusiveCombo.isInclusiveOr());
      assertFalse("Function 'ParamCombo' has an incorrect inclusive param combo: ",
         _exclusiveCombo.isInclusiveOr());
      assertFalse("Function 'ParamCombo' has an incorrect all-or-none param combo: ",
         _allOrNoneCombo.isInclusiveOr());
   }

   /**
    * Tests that {@link ParamCombo#isAllOrNone() isAllOrNone()} returns 
    * the correct all-or-none flag for a param combo.
    */
   public void testErrorCodeIsAllOrNode() {
      assertTrue("Function 'ParamCombo' has an incorrect exclusive param combo: ",
         _allOrNoneCombo.isAllOrNone());
      assertFalse("Function 'ParamCombo' has an incorrect inclusive param combo: ",
         _inclusiveCombo.isAllOrNone());
      assertFalse("Function 'ParamCombo' has an incorrect all-or-none param combo: ",
         _exclusiveCombo.isAllOrNone());
   }

   /**
    * Tests that {@link ParamCombo#getParameters() getParameters()} returns 
    * the correct parameters for a param combo.
    */
   public void testErrorCodeGetParameters() {
      assertEquals(3, _exclusiveCombo.getParameters().size());
      Set paramNames = _exclusiveCombo.getParameters().keySet();
      
      assertTrue("The exclusive input param combo of the function 'ParamCombo'" +
         " does not contain the paramter 'birthDate'",
         paramNames.contains("birthDate"));
      assertTrue("The exclusive input param combo of the function 'ParamCombo'" +
         " does not contain the paramter 'birthYear'",
         paramNames.contains("birthYear"));
      assertTrue("The exclusive input param combo of the function 'ParamCombo'" +
         " does not contain the paramter 'age'",
         paramNames.contains("age"));

      assertEquals("The inclusive input param combo of the function " +
         "'ParamCombo' has an incorrect number of parameters.",
         2, _inclusiveCombo.getParameters().size());
      assertEquals("The all-or-none input param combo of the function " +
         "'ParamCombo' has an incorrect number of parameters.",
         3, _allOrNoneCombo.getParameters().size());
   }

}


