/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.constraint;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.constraint.*;

import org.xins.common.types.standard.Int32;
import org.xins.common.types.Type;

/**
 * Tests for constraint classes.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class ConstraintTests extends TestCase {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ConstraintTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ConstraintTests</code> test suite with the
    * specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ConstraintTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      // empty
   }

   private void reset() {
      // empty
   }

   /**
    * Tests the <code>RequiredParamConstraint</code> class.
    */
   public void testRequiredParamConstraint()
   throws Exception {

      // Test constructor failure
      try {
         new RequiredParamConstraint(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new RequiredParamConstraint("");
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }

      // Test constructor success and getParameterName() method
      final String NAME = "SomeName";
      RequiredParamConstraint rpc = new RequiredParamConstraint(NAME);
      assertEquals(NAME, rpc.getParameterName());

      // Test with null value
      TestContext ctx = new TestContext(NAME, null);
      ConstraintViolation violation = rpc.check(ctx);
      assertNotNull(violation);
      assertEquals(rpc, violation.getConstraint());

      // Test with non-null value
      ctx = new TestContext(NAME, "SomeValue");
      assertNull(rpc.check(ctx));

      // Extra test: Empty string is not null
      ctx = new TestContext(NAME, "");
      assertNull(rpc.check(ctx));
   }

   /**
    * Tests the <code>TypedParamConstraint</code> class.
    */
   public void testTypedParamConstraint()
   throws Exception {

      final String NAME = "SomeName";
      final Type TYPE = Int32.SINGLETON;

      // Test constructor failure
      try {
         new TypedParamConstraint(null, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new TypedParamConstraint("", null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new TypedParamConstraint(NAME, null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new TypedParamConstraint(null, TYPE);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new TypedParamConstraint("", TYPE);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }

      // Test constructor success
      TypedParamConstraint tpc = new TypedParamConstraint(NAME, TYPE);
      assertEquals(NAME, tpc.getParameterName());
      assertEquals(TYPE, tpc.getType());

      // Test with null parameter value (should succeed)
      TestContext ctx = new TestContext(NAME, null);
      assertNull(tpc.check(ctx));

      // Test with empty string value (should fail)
      ctx = new TestContext(NAME, "");
      ConstraintViolation violation = tpc.check(ctx);
      assertEquals(tpc, violation.getConstraint());

      // Test with Boolean object with value "false" (should fail)
      ctx = new TestContext(NAME, java.lang.Boolean.FALSE);
      violation = tpc.check(ctx);
      assertEquals(tpc, violation.getConstraint());

      // Test with Integer with value "0" (should succeed)
      ctx = new TestContext(NAME, new Integer(0));
      assertNull(tpc.check(ctx));

      // Test with Integer with value "-1" (should succeed)
      ctx = new TestContext(NAME, new Integer(-1));
      assertNull(tpc.check(ctx));
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   private class TestContext
   extends Object
   implements ConstraintContext {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      private TestContext(String name, Object value) {
         _name  = name;
         _value = value;
      }

      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      private final String _name;
      private final Object _value;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      public Object getParameter(String name) {
         return (name == _name) ? _value : null;
      }
   }
}
