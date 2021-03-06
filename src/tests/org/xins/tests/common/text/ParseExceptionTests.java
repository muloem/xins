/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.text;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.logdoc.ExceptionUtils;
import org.xins.common.text.ParseException;

/**
 * Tests for class <code>ParseException</code>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class ParseExceptionTests extends TestCase {

   /**
    * Constructs a new <code>ParseExceptionTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public ParseExceptionTests(String name) {
      super(name);
   }

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(ParseExceptionTests.class);
   }

   public void testParseException() throws Throwable {
      ParseException p = new ParseException();
      assertEquals(null, p.getMessage());
      assertEquals(null, p.getDetail());
      assertEquals(null, ExceptionUtils.getCause(p));

      p = new ParseException(null, null, null);
      assertEquals(null, p.getMessage());
      assertEquals(null, p.getDetail());
      assertEquals(null, ExceptionUtils.getCause(p));

      Exception cause = new Exception();
      String message = "'nough said.";
      String detail  = "Zoom zoom zoom";
      p = new ParseException(message, cause, detail);
      assertEquals(message, p.getMessage());
      assertEquals(detail,  p.getDetail());
      assertEquals(cause,   ExceptionUtils.getCause(p));
   }
}
