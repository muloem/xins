/*
 * $Id$
 */
package org.xins.tests.client;

import java.net.InetAddress;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.client.ActualFunctionCaller;
import org.xins.client.CallTargetGroup;
import org.xins.client.FunctionCaller;
import org.xins.client.FunctionCallerParser;
import org.xins.client.ParseException;

/**
 * Tests for class <code>FunctionCallerParser</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class FunctionCallerParserTests extends TestCase {

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
      return new TestSuite(FunctionCallerParserTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>FunctionCallerParserTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public FunctionCallerParserTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   private FunctionCallerParser _parser;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      reset();
   }

   private void reset() {
      _parser = new FunctionCallerParser();
   }

   public void testParse_String() throws Throwable {

      // Pass null as argument (should fail)
      try {
         _parser.parse((String) null);
         fail("FunctionCallerParser.parse(null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Pass invalid XML
      try {
         _parser.parse("<<");
         fail("FunctionCallerParser.parse(\"<<\") should throw a ParseException.");
      } catch (ParseException exception) {
         // as expected
      }

      // Pass invalid XML structure
      try {
         _parser.parse("<garbage/>");
         fail("FunctionCallerParser.parse(\"<garbage/>\") should throw a ParseException.");
      } catch (ParseException exception) {
         // as expected
      }

      // Pass unknown host name
      try {
         _parser.parse("<api url='http://unknownhost.xins'/>");
         fail("FunctionCallerParser.parse(\"<api url='http://unknownhost.xins'/>\") should throw a ParseException due to the unknown host.");
      } catch (ParseException exception) {
         // as expected
      }

      // Good XML
      final String xml =
         "<group type='ordered'>" +                         // group #0
            "<group type='random'>" +                       // group #1
               "<group type='random'>" +                    // group #2
                  "<api url='http://10.0.0.1/google'/>" +   // api #0
                  "<api url='http://10.0.0.2/google'/>" +   // api #1
                  "<api url='http://10.0.0.3/google'/>" +   // api #2
               "</group>" +
               "<api url='http://www.google.nl/xins'/>" +   // api #3
            "</group>" +
            "<api url='http://www.google.com/xins'/>" +     // api #4
         "</group>";

      FunctionCaller caller = _parser.parse(xml);

      // group #0
      CallTargetGroup group0 = (CallTargetGroup) caller;
      assertEquals(CallTargetGroup.ORDERED_TYPE, group0.getType());
      List group0members = group0.getMembers();
      assertEquals(2, group0members.size());

      // group #1
      CallTargetGroup group1 = (CallTargetGroup) group0members.get(0);
      assertEquals(CallTargetGroup.RANDOM_TYPE, group1.getType());
      List group1members = group1.getMembers();
      assertEquals(2, group1members.size());

      // group #2
      CallTargetGroup group2 = (CallTargetGroup) group1members.get(0);
      assertEquals(CallTargetGroup.RANDOM_TYPE, group2.getType());
      List group2members = group2.getMembers();
      assertEquals(3, group2members.size());

      // api #0
      ActualFunctionCaller api0 = (ActualFunctionCaller) group2members.get(0);
      assertEquals("http://10.0.0.1/google", api0.getURL().toString());

      // api #1
      ActualFunctionCaller api1 = (ActualFunctionCaller) group2members.get(1);
      assertEquals("http://10.0.0.2/google", api1.getURL().toString());

      // api #2
      ActualFunctionCaller api2 = (ActualFunctionCaller) group2members.get(2);
      assertEquals("http://10.0.0.3/google", api2.getURL().toString());

      // api #3
      String api3ip = InetAddress.getByName("www.google.nl").getHostAddress();
      ActualFunctionCaller api3 = (ActualFunctionCaller) group1members.get(1);
      assertEquals("http://" + api3ip + "/xins", api3.getURL().toString());

      // api #4
      String api4ip = InetAddress.getByName("www.google.com").getHostAddress();
      ActualFunctionCaller api4 = (ActualFunctionCaller) group0members.get(1);
      assertEquals("http://" + api4ip + "/xins", api4.getURL().toString());
   }
}
