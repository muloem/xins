/*
 * $Id$
 */
package org.xins.tests.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.client.ActualFunctionCaller;
import org.xins.client.CallTargetGroup;
import org.xins.client.FunctionCaller;

/**
 * Tests for class <code>CallTargetGroup</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class CallTargetGroupTests extends TestCase {

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
      return new TestSuite(CallTargetGroupTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>CallTargetGroupTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public CallTargetGroupTests(String name) {
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

   public void testCreate() throws Throwable {

      // Pass null, null as arguments (should fail)
      try {
         CallTargetGroup.create(null, (List) null);
         fail("CallTargetGroup.create(null,null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      // Pass null, non-null as arguments (should fail)
      try {
         CallTargetGroup.create(null, new ArrayList());
         fail("CallTargetGroup.create(null,non-null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      // Pass non-null, null as arguments (should fail)
      try {
         CallTargetGroup.create(CallTargetGroup.RANDOM_TYPE, (List) null);
         fail("CallTargetGroup.create(null,non-null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      // Pass 2 ActualFunctionCallers with the same URL (should fail)
      List members = new ArrayList();
      URL url = new URL("http://sourceforge.net/something");
      members.add(new ActualFunctionCaller(url));
      members.add(new ActualFunctionCaller(url));
      try {
         CallTargetGroup.create(CallTargetGroup.RANDOM_TYPE, members);
         fail("CallTargetGroup.create() should throw an IllegalArgumentException if the member list contains duplicate URLs.");
      } catch (IllegalArgumentException exception) { /* as expected */ }

      // Create CallTargetGroup with 2 ActualFunctionCallers
      members = new ArrayList();
      ActualFunctionCaller afc1 = new ActualFunctionCaller(new URL("http://sf.net/1"));
      ActualFunctionCaller afc2 = new ActualFunctionCaller(new URL("http://sf.net/2"));
      members.add(afc1);
      members.add(afc2);
      CallTargetGroup ctg = CallTargetGroup.create(CallTargetGroup.RANDOM_TYPE, members);
      assertNotNull(ctg);
   }
}
