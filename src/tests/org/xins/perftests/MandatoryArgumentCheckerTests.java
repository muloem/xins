/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.perftests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xins.common.MandatoryArgumentChecker;

/**
 * Performance tests for class <code>MandatoryArgumentChecker</code>.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public class MandatoryArgumentCheckerTests extends TestCase {

   /**
    * Returns a test suite with all test cases defined by this class.
    *
    * @return
    *    the test suite, never <code>null</code>.
    */
   public static Test suite() {
      return new TestSuite(MandatoryArgumentCheckerTests.class);
   }

   private static final int ROUNDS = 100000000;

   /**
    * Constructs a new <code>MandatoryArgumentCheckerTests</code> test suite
    * with the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public MandatoryArgumentCheckerTests(String name) {
      super(name);
   }

   public void testMandatoryArgumentChecker() throws Exception {

      for (int i = 0; i < ROUNDS; i++) {
         MandatoryArgumentChecker.check("something", "not null");
         MandatoryArgumentChecker.check("a", "", "b", "c");
      }
   }
}
