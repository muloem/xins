/*
 * $Id$
 */
package org.xins.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Combination of all XINS/Java tests.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class AllTests extends TestSuite {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns a test suite with all test cases.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      TestSuite suite = new TestSuite();
      suite.addTestSuite(org.xins.tests.client.FunctionCallerParserTests.class);
      suite.addTestSuite(org.xins.tests.common.util.net.URLEncodingTests.class);
      suite.addTestSuite(org.xins.tests.common.util.sd.DescriptorBuilderTests.class);
      suite.addTestSuite(org.xins.tests.common.util.text.HexConverterTests.class);
      suite.addTestSuite(org.xins.tests.common.util.text.ReplacerTests.class);
      suite.addTestSuite(org.xins.tests.server.BasicResponseValidatorTests.class);
      return suite;
   }


   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>AllTests</code> object with the specified name.
    * The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test case.
    */
   public AllTests(String name) {
      super(name);
   }
}
