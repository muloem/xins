/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.tests.common.util.text;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * Tests for parsing of the regular expression in <code>PatternType</code>.
 *
 * @version $Revision$
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 */
public class PatternParserTests extends TestCase {

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
      return new TestSuite(PatternParserTests.class);
   }


   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructor
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>SimplePatternParserTests</code> test suite with
    * the specified name. The name will be passed to the superconstructor.
    *
    * @param name
    *    the name for this test suite.
    */
   public PatternParserTests(String name) {
      super(name);
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   public void testMatchPattern() throws Throwable {
      doTestMatchPattern(".*", "hello world", true);
      doTestMatchPattern("^([a-zA-Z0-9._\\-]*@((wanadoo)|(euronet))\\.nl)$", "freeclosed.seven@wanadoo.nl", true);
   }

   /**
    * Tests if an example matches a regular expression.
    *
    * @param re
    *    The Perl 5 regular expression.
    * @param example
    *    The example that should match or not the regular expression.
    * @param shouldMatch
    *    true if the example should match the regex, false if the example
    *    should not match the regex.
    */
   private void doTestMatchPattern(String re, String example, boolean shouldMatch)
   throws Throwable {
      Perl5Compiler compiler = new Perl5Compiler();
      Perl5Matcher matcher = new Perl5Matcher();

      Pattern pattern = null;
      try {
         pattern = compiler.compile(re, Perl5Compiler.READ_ONLY_MASK);
      } catch (MalformedPatternException mpe) {
         fail("Failed to compile the regular expression: "+re);
      }
      boolean match = matcher.matches(example, pattern);
      if (shouldMatch) {
         assertTrue("The example \"" + example + "\" does not match the pattern \""+re+"\"", match);
      } else {
         assertTrue("The example \"" + example + "\" does not match the pattern \""+re+"\"", !match);
      }
   }
}
