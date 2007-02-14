/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.types;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.Utils;

import org.xins.logdoc.ExceptionUtils;

/**
 * Abstract base class for pattern types. A pattern type only accepts values
 * that match a certain regular expression.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public abstract class PatternType extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Perl 5 pattern compiler.
    */
   private static final Perl5Compiler PATTERN_COMPILER = new Perl5Compiler();


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>PatternType</code> instance. The name of the type
    * needs to be specified. The value class (see
    * {@link Type#getValueClass()}) is set to {@link String String.class}.
    *
    * @param name
    *    the name of the type, not <code>null</code>.
    *
    * @param pattern
    *    the regular expression the values must match, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>name == null || pattern == null</code>.
    *
    * @throws PatternCompileException
    *    if the specified pattern is considered invalid.
    */
   protected PatternType(String name, String pattern)
   throws IllegalArgumentException, PatternCompileException {
      super(name, String.class);

      if (pattern == null) {
         throw new IllegalArgumentException("pattern == null");
      }

      // Compile the regular expression to a Pattern object
      try {
         synchronized (PATTERN_COMPILER) {
            _pattern = PATTERN_COMPILER.compile(pattern,
                                                Perl5Compiler.READ_ONLY_MASK);
         }

      // Handle pattern compilation error
      } catch (MalformedPatternException mpe) {
         PatternCompileException e = new PatternCompileException(pattern);
         ExceptionUtils.setCause(e, mpe);
         throw e;
      }

      // Store the original pattern string
      _patternString = pattern;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Pattern string. This is the uncompiled version of {@link #_pattern}.
    * This field cannot be <code>null</code>.
    */
   private final String _patternString;

   /**
    * Compiled pattern. This is the compiled version of
    * {@link #_patternString}. This field cannot be <code>null</code>.
    */
   private final Pattern _pattern;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected final boolean isValidValueImpl(String value) {

      // Determine if the value matches the pattern
      try {
         Perl5Matcher patternMatcher = new Perl5Matcher();
         return patternMatcher.matches(value, _pattern);

      // If the call causes an exception, then log that exception and assume
      // the value does not match the pattern
      } catch (Throwable exception) {
         String detail = "Assuming the value \"" + value
               + "\" is invalid for the pattern \"" + _patternString + "\".";
         Utils.logProgrammingError(detail, exception);
         return false;
      }
   }

   protected final Object fromStringImpl(String value) {
      return value;
   }

   public final String toString(Object value)
   throws IllegalArgumentException, ClassCastException, TypeValueException {
      MandatoryArgumentChecker.check("value", value);
      String s = (String) value;
      if (!isValidValueImpl(s)) {
         throw new TypeValueException(this, s);
      }
      return s;
   }

   /**
    * Returns the pattern.
    *
    * @return
    *    the pattern, not <code>null</code>.
    */
   public String getPattern() {
      return _patternString;
   }
}
