/*
 * $Id$
 */
package org.xins.common.types;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.xins.common.Log;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Patterns type. An enumeration type only accepts values that match a certain
 * pattern.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public abstract class PatternType extends Type {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Perl 5 pattern compiler.
    */
   private static final Perl5Compiler PATTERN_COMPILER = new Perl5Compiler();

   /**
    * Pattern matcher.
    */
   private static final Perl5Matcher PATTERN_MATCHER = new Perl5Matcher();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

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

      try {
         synchronized (PATTERN_COMPILER) {
            _pattern = PATTERN_COMPILER.compile(pattern, Perl5Compiler.READ_ONLY_MASK);
         }
      } catch (MalformedPatternException mpe) {
         throw new PatternCompileException(mpe.getMessage());
      }
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * Compiled pattern. This is the compiled version of {@link #_pattern}.
    * This field cannot be <code>null</code>.
    */
   private final Pattern _pattern;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   protected final boolean isValidValueImpl(String value) {
      try {
         return PATTERN_MATCHER.matches(value, _pattern);
      } catch (Throwable exception) {
         //LOG.error(PATTERN_MATCHER.getClass().getName() + ".matches(java.lang.String," + _pattern.getClass().getName() + ") has thrown an unexpected exception. Assuming the value \"" + value + "\" is invalid.", exception);
         Log.log_3100(exception, PATTERN_MATCHER.getClass().getName(), _pattern.getClass().getName(), value);
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
      return _pattern.getPattern();
   }
}
