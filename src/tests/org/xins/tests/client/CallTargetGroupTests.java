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

      // Test
      assertNotNull(ctg);
   }

   public void testGetActualFunctionCallers()
   throws Throwable {

      // Create CallTargetGroup with 2 ActualFunctionCallers
      List members = new ArrayList();
      ActualFunctionCaller afc1 = new ActualFunctionCaller(new URL("http://sf.net/1"));
      ActualFunctionCaller afc2 = new ActualFunctionCaller(new URL("http://sf.net/2"));
      members.add(afc1);
      members.add(afc2);
      CallTargetGroup ctg = CallTargetGroup.create(CallTargetGroup.RANDOM_TYPE, members);

      // Test
      List members2 = ctg.getActualFunctionCallers();
      assertEquals(members, members2);
   }

   public void testGetActualFunctionCaller_java_lang_String()
   throws Throwable {

      // Create CallTargetGroup with 2 ActualFunctionCallers
      List members = new ArrayList();
      String url1 = "http://1.2.3.4/1";
      String url2 = "http://1.2.3.4/2";
      ActualFunctionCaller afc1 = new ActualFunctionCaller(new URL(url1));
      ActualFunctionCaller afc2 = new ActualFunctionCaller(new URL(url2));
      members.add(afc1);
      members.add(afc2);
      CallTargetGroup ctg = CallTargetGroup.create(CallTargetGroup.RANDOM_TYPE, members);

      // Test 1
      assertEquals(url1, afc1.getURL().toString());
      assertEquals(url2, afc2.getURL().toString());

      // Test 2
      assertEquals(afc1, ctg.getActualFunctionCaller(url1));
      assertEquals(afc2, ctg.getActualFunctionCaller(url2));
   }

   public void testGetActualFunctionCaller_long()
   throws Throwable {

      // Create CallTargetGroup with 2 ActualFunctionCallers
      List members = new ArrayList();
      ActualFunctionCaller afc1 = new ActualFunctionCaller(new URL("http://sf.net/1"));
      ActualFunctionCaller afc2 = new ActualFunctionCaller(new URL("http://sf.net/2"));
      long crc1 = afc1.getCRC32();
      long crc2 = afc2.getCRC32();
      members.add(afc1);
      members.add(afc2);
      CallTargetGroup ctg = CallTargetGroup.create(CallTargetGroup.RANDOM_TYPE, members);

      assertEquals(afc1, ctg.getActualFunctionCaller(crc1));
      assertEquals(afc2, ctg.getActualFunctionCaller(crc2));
   }

   public void testGetActualFunctionCallerByCRC32()
   throws Throwable {

      // Create CallTargetGroup with 2 ActualFunctionCallers
      List members = new ArrayList();
      ActualFunctionCaller afc1 = new ActualFunctionCaller(new URL("http://sf.net/1"));
      ActualFunctionCaller afc2 = new ActualFunctionCaller(new URL("http://sf.net/2"));
      String crc1 = afc1.getCRC32String();
      String crc2 = afc2.getCRC32String();
      members.add(afc1);
      members.add(afc2);
      CallTargetGroup ctg = CallTargetGroup.create(CallTargetGroup.RANDOM_TYPE, members);

      assertEquals(afc1, ctg.getActualFunctionCallerByCRC32(crc1));
      assertEquals(afc2, ctg.getActualFunctionCallerByCRC32(crc2));
   }
}
