/*
 * $Id$
 */
package org.xins.tests.client;

import java.net.URL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.client.ActualFunctionCaller;

/**
 * Tests for class <code>ActualFunctionCaller</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class ActualFunctionCallerTests extends TestCase {

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
      return new TestSuite(ActualFunctionCallerTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ActualFunctionCallerTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ActualFunctionCallerTests(String name) {
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

   public void testGetCRC32String() throws Throwable {
      ActualFunctionCaller afc;

      String[] urls = new String[] {
         "http://1.2.3.4/",      "http://1.2.3.4/a",   "http://10.10.10.10/",
         "http://10.10.10.10/a", "http://3.2.1.0/a/b", "http://sf.net/a/b/c",
         "http://12.34.56.78/a", "http://0.9.8.7/a/b", "http://sf.net/abcde",
         "http://112.134.156.178/"
      };

      for (int i = 0; i < urls.length; i++) {
         afc = new ActualFunctionCaller(new URL(urls[i]));
         String crcString = afc.getCRC32String();
         assertEquals(16, crcString.length());
         assertEquals("00000000", crcString.substring(0, 8));
      }
   }
}
