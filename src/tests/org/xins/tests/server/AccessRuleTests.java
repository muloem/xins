/*
 * $Id$
 */
package org.xins.tests.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.server.AccessRule;
import org.xins.server.IPFilter;
import org.xins.util.text.FastStringBuffer;
import org.xins.util.text.ParseException;

/**
 * Tests for class <code>AccessRule</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class AccessRuleTests extends TestCase {

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
      return new TestSuite(AccessRuleTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>AccessRuleTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public AccessRuleTests(String name) {
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

   public void testParseAccessRule() throws Throwable {

      try {
         AccessRule.parseAccessRule(null);
         fail("AccessRule.parseAccessRule(null) should throw an IllegalArgumentException.");
      } catch (IllegalArgumentException exception) {
         // as expected
      }

      doTestParseAccessRule("1.2.3.4");
   }

   private void doTestParseAccessRule(String ip)
   throws Throwable {
      for (int mask = 0; mask <= 32; mask++) {

         doTestParseAccessRule(false, ip, mask);
         doTestParseAccessRule(true, ip, mask);
      }
   }

   private void doTestParseAccessRule(boolean allow, String ip, int mask)
   throws Throwable {
      doTestParseAccessRule(allow, ip, mask, " ",        " ");
      doTestParseAccessRule(allow, ip, mask, "\t",       "\t");
      doTestParseAccessRule(allow, ip, mask, " ",        "\t");
      doTestParseAccessRule(allow, ip, mask, " \t\n\r ", "\t\n\r");
      doTestParseAccessRule(allow, ip, mask, "\n ",      "\r");
   }

   private void doTestParseAccessRule(boolean allow, String ip, int mask, String whitespace1, String whitespace2)
   throws Throwable {

      FastStringBuffer buffer = new FastStringBuffer(250);
      if (allow) {
         buffer.append("allow");
      } else {
         buffer.append("deny");
      }
      buffer.append(whitespace1);
      buffer.append(ip);
      buffer.append('/');
      buffer.append(mask);
      buffer.append(whitespace2);
      buffer.append("*");

      AccessRule rule = AccessRule.parseAccessRule(buffer.toString());
      assertNotNull(rule);
      assertEquals(allow, rule.isAllowRule());

      IPFilter ipFilter = rule.getIPFilter();
      assertNotNull(ipFilter);

      assertEquals(ip,   ipFilter.getBaseIP());
      assertEquals(mask, ipFilter.getMask());
   }
}
