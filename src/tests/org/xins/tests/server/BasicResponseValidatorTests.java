/*
 * $Id$
 */
package org.xins.tests.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xins.server.BasicResponseValidator;
import org.xins.server.InvalidResponseException;

/**
 * Tests for class <code>BasicResponseValidator</code>.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public class BasicResponseValidatorTests extends TestCase {

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
      return new TestSuite(BasicResponseValidatorTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>BasicResponseValidatorTests</code> test suite
    * with the specified name. The name will be passed to the
    * superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public BasicResponseValidatorTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   private BasicResponseValidator _validator;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Performs setup for the tests.
    */
   protected void setUp() {
      _validator = BasicResponseValidator.SINGLETON;
   }

   private void reset() {
      // empty
   }

   public void testStartResponse() throws Throwable {
      _validator.startResponse(true, null);
      _validator.cancelResponse();

      _validator.startResponse(false, null);
      _validator.cancelResponse();

      _validator.startResponse(false, "InternalError");
      _validator.cancelResponse();
   }

   public void testParameter() throws Throwable {

      _validator.startResponse(true, null);

      final String name = "name";
      final String value = "value";

      // Should be okay
      _validator.param(name, value);

      // Duplicate parameter names, should fail
      try {
         _validator.param(name, value);
         fail("BasicResponseValidator.param(String,String) should throw an InvalidResponseException if called twice with the same parameter name.");
      } catch (InvalidResponseException exception) {
         // as expected
      }

      // We should now be able to do startResponse() again because it failed
      // and should have cleaned up
      _validator.startResponse(true, null);
      _validator.param(name, value);

      // End the response with endResponse() and try again
      _validator.endResponse();
      _validator.startResponse(true, null);
      _validator.param(name, value);

      // End the response with cancelResponse() and try again
      _validator.cancelResponse();
      _validator.startResponse(true, null);
      _validator.param(name, value);
   }
}
