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

   public void testGetExpression() throws Throwable {

      doTestGetExpression("1.2.3.4/1");
      doTestGetExpression("0.0.0.0/0");
      doTestGetExpression("194.134.168.213/32");
      doTestGetExpression("194.13.1.213/20");
   }

   private void doTestGetExpression(String expression)
   throws Throwable {

      IPFilter filter = IPFilter.parseIPFilter(expression);
      assertNotNull(filter);

      assertEquals(expression, filter.getExpression());
      assertEquals(expression, filter.getExpression());
   }

   public void testParseIPFilter() throws Throwable {

      try {
         IPFilter.parseIPFilter(null);
         fail("IPFilter.parseIPFilter(null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      // Test invalid patterns
      doTestParseIPFilter("abcd");
      doTestParseIPFilter("abcd/24");
      doTestParseIPFilter("a.b.c.d/12");
      doTestParseIPFilter("1.");
      doTestParseIPFilter("1.2.");
      doTestParseIPFilter("1.2.3");
      doTestParseIPFilter("1.2.3.");
      doTestParseIPFilter("1.2.3.4");
      doTestParseIPFilter("1./");
      doTestParseIPFilter("1.2./");
      doTestParseIPFilter("1.2.3/");
      doTestParseIPFilter("1.2.3./");
      doTestParseIPFilter("1.2.3.4/");
      doTestParseIPFilter("1./0");
      doTestParseIPFilter("1.2./0");
      doTestParseIPFilter("1.2.3/0");
      doTestParseIPFilter("1.2.3./0");
      doTestParseIPFilter("1.2.3.4/a");
      doTestParseIPFilter("1.2.3.4/-1");
      doTestParseIPFilter(" 1.2.3.4/0");
      doTestParseIPFilter("1.2.3.4/0 ");
      doTestParseIPFilter(" 1.2.3.4/0 ");
      doTestParseIPFilter("1.2.3.4/5a");
      doTestParseIPFilter("1.2.3.4a/5");
      doTestParseIPFilter("1.2.3.4//5");
      doTestParseIPFilter("01.2.3.4/32");
      doTestParseIPFilter("1.02.3.4/32");
      doTestParseIPFilter("1.2.3.4/00");
      doTestParseIPFilter("1.2.3.4/01");
      doTestParseIPFilter("1.2.3.4/032");
      doTestParseIPFilter("1.2.3.4/33");
      doTestParseIPFilter("1.2.3.4/1234567890123456");
      doTestParseIPFilter("1.2.3.4.5/0");

      // Test valid patterns
      for (int i = 0; i <= 32; i++) {
         doTestParseIPFilter("1.2.3.4",         i);
         doTestParseIPFilter("1.20.3.4",        i);
         doTestParseIPFilter("10.20.30.40",     i);
         doTestParseIPFilter("1.102.3.4",       i);
         doTestParseIPFilter("194.134.168.213", i);
      }
   }

   private void doTestParseIPFilter(String expression)
   throws Throwable {

      try {
         IPFilter.parseIPFilter(expression);
         fail("IPFilter.parse(\"" + expression + "\") should throw a ParseException.");
      } catch (ParseException exception) {
         // as expected
      }
   }

   private void doTestParseIPFilter(String baseIP, int mask)
   throws Throwable {

      String expression = baseIP + '/' + String.valueOf(mask);
      IPFilter filter = IPFilter.parseIPFilter(expression);
      assertNotNull(filter);

      assertEquals(baseIP,              filter.getBaseIP());
      assertEquals(mask,                filter.getMask());
      assertEquals(baseIP + '/' + mask, filter.toString());
   }

   public void testIsAuthorized() throws Throwable {

      IPFilter filter = IPFilter.parseIPFilter("194.134.168.213/32");
      assertNotNull(filter);

      try {
         filter.match(null);
         fail("IPFilter.match(null) should throw an IllegalArgumentException.");
         return;
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      doTestIsAuthorized(filter, "abcd",                     false, false);
      doTestIsAuthorized(filter, "abcd",                     false, false);
      doTestIsAuthorized(filter, "abcd/24",                  false, false);
      doTestIsAuthorized(filter, "a.b.c.d",                  false, false);
      doTestIsAuthorized(filter, "a.b.c.d/12",               false, false);
      doTestIsAuthorized(filter, "1.2.3.4/",                 false, false);
      doTestIsAuthorized(filter, "1.2.3.4/a",                false, false);
      doTestIsAuthorized(filter, "1.2.3.4/-1",               false, false);
      doTestIsAuthorized(filter, "1.2.3.4/0",                false, false);
      doTestIsAuthorized(filter, "1.2.3.4/5",                false, false);
      doTestIsAuthorized(filter, "1.2.3.4/5a",               false, false);
      doTestIsAuthorized(filter, "1.2.3.4a/5",               false, false);
      doTestIsAuthorized(filter, "1.2.3.4//5",               false, false);
      doTestIsAuthorized(filter, "1.2.3.4/32",               false, false);
      doTestIsAuthorized(filter, "1.2.3.4/33",               false, false);
      doTestIsAuthorized(filter, "1.2.3.4/1234567890123456", false, false);
      doTestIsAuthorized(filter, "1.",                       false, false);
      doTestIsAuthorized(filter, "1.2",                      false, false);
      doTestIsAuthorized(filter, "1.2.",                     false, false);
      doTestIsAuthorized(filter, "1.2.3",                    false, false);
      doTestIsAuthorized(filter, "1.2.3.",                   false, false);
      doTestIsAuthorized(filter, "1.2.34.",                  false, false);
      doTestIsAuthorized(filter, "1.2.3.4",                  true,  false);
      doTestIsAuthorized(filter, "1.2.3.4.5",                false, false);
      doTestIsAuthorized(filter, "01.2.3.4/32",              false, false);
      doTestIsAuthorized(filter, "1.02.3.4/32",              false, false);
      doTestIsAuthorized(filter, "1.102.3.4/32",             false, false);
      doTestIsAuthorized(filter, "1.2.3.4/00",               false, false);
      doTestIsAuthorized(filter, "1.2.3.4/01",               false, false);
      doTestIsAuthorized(filter, "194.103.168.213",          true,  false);
      doTestIsAuthorized(filter, "194.134.168.213",          true,  true);
      doTestIsAuthorized(filter, "194.134.168.212",          true,  false);
      doTestIsAuthorized(filter, "194.134.168.214",          true,  false);

      filter = IPFilter.parseIPFilter("194.134.168.213/31");
      assertNotNull(filter);

      doTestIsAuthorized(filter, "194.134.168.213",          true,  true);
      doTestIsAuthorized(filter, "194.134.168.212",          true,  true);
      doTestIsAuthorized(filter, "194.134.168.211",          true,  false);
      doTestIsAuthorized(filter, "194.134.168.214",          true,  false);
   }

   private void doTestIsAuthorized(IPFilter filter,
                                   String   ip,
                                   boolean  validIP,
                                   boolean  shouldBeAuth)
   throws Throwable {

      if (validIP) {
         boolean auth = filter.match(ip);
         if (shouldBeAuth && !auth) {
            fail("IPFilter.match(\"" + ip + "\") should return true for \"" + filter + "\".");
         } else if (!shouldBeAuth && auth) {
            fail("IPFilter.match(\"" + ip + "\") should return false for \"" + filter + "\".");
         }
      } else {
         try {
            filter.match(ip);
            fail("IPFilter.match(\"" + ip + "\") should throw a ParseException.");
            return;
         } catch (ParseException exception) {
            // as expected
         }
      }
   }
}
