/*
 * $Id$
 */
package org.xins.tests.client;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
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

   public void testParse() throws ParseException {

      // Pass null as argument (should fail)
      try {
         _parser.parse(null);
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

      final String xml =
         "<group type='ordered'>" +
            "<group type='random'>" +
               "<group type='round robin'>" +
                  "<api url='http://10.0.0.1/google'/>" +
                  "<api url='http://10.0.0.2/google'/>" +
               "</group>" +
               "<api url='http://www.google.nl/xins'/>" +
            "</group>" +
            "<api url='http://www.google.com/xins'/>" +
         "</group>";

      FunctionCaller caller = _parser.parse(xml);
   }
}
