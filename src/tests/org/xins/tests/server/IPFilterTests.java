/*
 * $Id$
 */
package org.xins.tests.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.server.IPFilter;
import org.xins.util.text.ParseException;

/**
 * Tests for class <code>IPFilter</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class IPFilterTests extends TestCase {

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
      return new TestSuite(IPFilterTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>IPFilterTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public IPFilterTests(String name) {
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

   public void testParseFilter() throws Throwable {

      try {
         IPFilter.parseFilter(null);
         fail("IPFilter.parseFilter(null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      doTestParseFilter("abcd",       false);
      doTestParseFilter("abcd/24",    false);
      doTestParseFilter("1.2.3.4",    false);
      doTestParseFilter("1.2.3.4/",   false);
      doTestParseFilter("1.2.3.4/a",  false);
      doTestParseFilter("1.2.3.4/-1", false);
      doTestParseFilter("1.2.3.4/0",  true);
      doTestParseFilter("1.2.3.4/5",  true);
      doTestParseFilter("1.2.3.4/32", true);
      doTestParseFilter("1.2.3.4/33", false);
   }

   private void doTestParseFilter(String expression, boolean shouldFail)
   throws Throwable {

      if (shouldFail) {
         try {
            IPFilter.parseFilter(expression);
            fail("IPFilter.parse(\"" + expression + "\") should throw a ParseException.");
         } catch (ParseException exception) {
            // as expected
         }
      } else {
         IPFilter filter = IPFilter.parseFilter(expression);
         assertNotNull(filter);
      }
   }
}
