/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.constraint;

import java.util.HashMap;
import java.util.List;

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
      ParamContext ctx = new ParamContext(NAME, null);
      ConstraintViolation violation = rpc.check(ctx);
      assertNotNull(violation);
      assertEquals(rpc, violation.getConstraint());

      // Test with non-null value
      ctx = new ParamContext(NAME, "SomeValue");
      assertNull(rpc.check(ctx));

      // Extra test: Empty string is not null
      ctx = new ParamContext(NAME, "");
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
      ParamContext ctx = new ParamContext(NAME, null);
      assertNull(tpc.check(ctx));

      // Test with empty string value (should fail)
      ctx = new ParamContext(NAME, "");
      ConstraintViolation violation = tpc.check(ctx);
      assertEquals(tpc, violation.getConstraint());

      // Test with Boolean object with value "false" (should fail)
      ctx = new ParamContext(NAME, java.lang.Boolean.FALSE);
      violation = tpc.check(ctx);
      assertEquals(tpc, violation.getConstraint());

      // Test with Integer with value "0" (should succeed)
      ctx = new ParamContext(NAME, new Integer(0));
      assertNull(tpc.check(ctx));

      // Test with Integer with value "-1" (should succeed)
      ctx = new ParamContext(NAME, new Integer(-1));
      assertNull(tpc.check(ctx));
   }

   /**
    * Tests the <code>AllOrNoneParamComboConstraint</code> class.
    */
   public void testAllOrNoneParamComboConstraint()
   throws Exception {

      final String NAME1 = "Name1";
      final String NAME2 = "Name2";
      final String NAME3 = "Name3";

      // Test constructor failure
      try {
         new AllOrNoneParamComboConstraint(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new AllOrNoneParamComboConstraint(new String[0]);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new AllOrNoneParamComboConstraint(new String[] { NAME1 });
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new AllOrNoneParamComboConstraint(new String[] { NAME1, NAME1 });
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }

      // Test constructor, expecting success
      String[] n = new String[] { NAME1, NAME2 };
      AllOrNoneParamComboConstraint a = new AllOrNoneParamComboConstraint(n);
      List names = a.getParameterNames();
      assertNotNull(names);
      assertEquals(n.length, names.size());
      assertEquals(n[0], names.get(0));
      assertEquals(n[1], names.get(1));

      // Test constraint validation: None set (should succeed)
      ConstraintContext ctx = new ParamContext(NAME1, null);
      assertNull(a.check(ctx));

      // Test constraint validation: All set (should succeed)
      HashMap map = new HashMap();
      map.put(NAME1, "a");
      map.put(NAME2, "b");
      map.put(NAME3, "c");
      ctx = new MapContext(map);
      assertNull(a.check(ctx));

      // Test constraint validation: Only one set (should fail)
      ctx = new ParamContext(NAME1, "a");
      ConstraintViolation violation = a.check(ctx);
      assertEquals(a, violation.getConstraint());
   }

   /**
    * Tests the <code>ExclusiveOrParamComboConstraint</code> class.
    */
   public void testExclusiveOrParamComboConstraint()
   throws Exception {

      final String NAME1 = "Name1";
      final String NAME2 = "Name2";
      final String NAME3 = "Name3";

      // Test constructor failure
      try {
         new ExclusiveOrParamComboConstraint(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new ExclusiveOrParamComboConstraint(new String[0]);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new ExclusiveOrParamComboConstraint(new String[] { NAME1 });
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new ExclusiveOrParamComboConstraint(new String[] { NAME1, NAME1 });
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }

      // Test constructor, expecting success
      String[] n = new String[] { NAME1, NAME2 };
      ExclusiveOrParamComboConstraint a = new ExclusiveOrParamComboConstraint(n);
      List names = a.getParameterNames();
      assertNotNull(names);
      assertEquals(n.length, names.size());
      assertEquals(n[0], names.get(0));
      assertEquals(n[1], names.get(1));

      // Test constraint validation: None set (should fail)
      ConstraintContext ctx = new ParamContext(NAME1, null);
      ConstraintViolation violation = a.check(ctx);
      assertEquals(a, violation.getConstraint());

      // Test constraint validation: All set (should fail)
      HashMap map = new HashMap();
      map.put(NAME1, "a");
      map.put(NAME2, "b");
      map.put(NAME3, "c");
      ctx = new MapContext(map);
      violation = a.check(ctx);
      assertEquals(a, violation.getConstraint());

      // Test constraint validation: Only one set (should succeed)
      ctx = new ParamContext(NAME1, "a");
      assertNull(a.check(ctx));
   }

   /**
    * Tests the <code>InclusiveOrParamComboConstraint</code> class.
    */
   public void testInclusiveOrParamComboConstraint()
   throws Exception {

      final String NAME1 = "Name1";
      final String NAME2 = "Name2";
      final String NAME3 = "Name3";

      // Test constructor failure
      try {
         new InclusiveOrParamComboConstraint(null);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new InclusiveOrParamComboConstraint(new String[0]);
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new InclusiveOrParamComboConstraint(new String[] { NAME1 });
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }
      try {
         new InclusiveOrParamComboConstraint(new String[] { NAME1, NAME1 });
         fail("Expected IllegalArgumentException.");
      } catch (IllegalArgumentException iae) {
         // as expected
      }

      // Test constructor, expecting success
      String[] n = new String[] { NAME1, NAME2 };
      InclusiveOrParamComboConstraint a = new InclusiveOrParamComboConstraint(n);
      List names = a.getParameterNames();
      assertNotNull(names);
      assertEquals(n.length, names.size());
      assertEquals(n[0], names.get(0));
      assertEquals(n[1], names.get(1));

      // Test constraint validation: None set (should fail)
      ConstraintContext ctx = new ParamContext(NAME1, null);
      ConstraintViolation violation = a.check(ctx);
      assertEquals(a, violation.getConstraint());

      // Test constraint validation: All set (should succeed)
      HashMap map = new HashMap();
      map.put(NAME1, "a");
      map.put(NAME2, "b");
      map.put(NAME3, "c");
      ctx = new MapContext(map);
      assertNull(a.check(ctx));

      // Test constraint validation: Only one set (should succeed)
      ctx = new ParamContext(NAME1, "a");
      assertNull(a.check(ctx));
   }


   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   private class ParamContext
   extends Object
   implements ConstraintContext {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      private ParamContext(String name, Object value) {
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

   private class MapContext
   extends Object
   implements ConstraintContext {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      private MapContext(HashMap map) {
         _map = map;
      }

      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      private final HashMap _map;


      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------

      public Object getParameter(String name) {
         return _map.get(name);
      }
   }
}
