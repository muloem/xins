/*
 * $Id$
 */
package org.xins.util.text;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Pattern;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Simple pattern parser.
 *
 * <h3>Format</h3>
 *
 * <p>A simple pattern is a text string that may contain letters, digits,
 * underscores, hyphens, dots and the wildcard characters <code>'*'</code>
 * (asterisk) and <code>'?'</code> (question mark).
 *
 * <p>The location of an asterisk indicates any number of characters is
 * allowed. The location of a question mark indicates exactly one character is
 * expected.
 *
 * <p>To allow matching of simple patterns, a simple pattern is first compiled
 * into a Perl 5 regular expression. Every asterisk is converted to
 * <code>".*"</code>, while every question mark is converted to
 * <code>"."</code>.
 *
 * <h3>Examples</h3>
 *
 * <p>Examples of conversions from a simple pattern to a Perl 4 regular
 * expression:
 *
 * <table>
 *    <tr><th>Simple pattern</th><th>Perl 5 regex equivalent</th></tr>
 *    <tr><td></td>              <td>^$</td>                     </tr>
 *    <tr><td>*</td>             <td>^.*$</td>                   </tr>
 *    <tr><td>?</td>             <td>^.$</td>                    </tr>
 *    <tr><td>_Get*</td>         <td>^_Get.*$</td>               </tr>
 *    <tr><td>_Get*i?n</td>      <td>^_Get.*i.n$</td>            </tr>
 *    <tr><td>*on</td>           <td>^.*on$</td>                 </tr>
 * </table>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 * @author Peter Troon (<a href="mailto:peter.troon@nl.wanadoo.com">peter.troon@nl.wanadoo.com</a>)
 *
 * @since XINS 0.152
 */
public class SimplePatternParser extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The dot character.
    */
   private static final char DOT = '.';

   /**
    * The asterisk character.
    */
   private static final char ASTERISK = '*';

   /**
    * The question mark character.
    */
   private static final char QUESTION_MARK = '?';

   /**
    * The accent circunflex character.
    */
   private static final char CIRCUNFLEX = '^';

   /**
    * The dollar sign character.
    */
   private static final char DOLLAR_SIGN = '$';

   /**
    * The regular expression that is used to replace all occurrences of an
    * asterisk within the pattern by a dot and an asterisk.
    */
   private static final String ASTERISK_REGEXP = "\\*";

   /**
    * The wilcard (dot and asterisk) to which each asterisk that appears 
    * within the pattern is converted.
    */
   private static final String PERL5_ASTERISK_WILDCARD = ".*";


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Creates a new <code>SimplePatternParser</code> object.
    */
   public SimplePatternParser() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Converts the specified simple pattern to a Perl 5 regular expression.
    *
    * @param simplePattern
    *    the simple pattern, cannot be <code>null</code>.
    *
    * @return
    *    the Perl 5 regular expression, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>simplePattern == null</code>.
    *
    * @throws ParseException
    *    if provided simplePattern is invalid or could not be parsed.
    */
   public Perl5Pattern parseSimplePattern(String simplePattern)
   throws IllegalArgumentException, ParseException {

      MandatoryArgumentChecker.check("simplePattern", simplePattern);

      if (isValidPattern(simplePattern) == false) {
         throw new ParseException("The pattern '" + simplePattern + "' is invalid.");
      }

      simplePattern = convertToPerl5RegularExpression(simplePattern);

      Perl5Pattern perl5pattern = null;
      Perl5Compiler perl5compiler = new Perl5Compiler();

      boolean parseError = false;
      try {
         Pattern pattern = perl5compiler.compile(simplePattern);
         if (pattern instanceof Perl5Pattern) {
            perl5pattern = (Perl5Pattern) pattern;
         } else {
            parseError = true;
         }
      } catch (
         MalformedPatternException mpe) {
         parseError = true;
      }

      if (parseError == true) {
         throw new ParseException("An error occurred while parsing the pattern '" + simplePattern + "'.");
      }

      return perl5pattern;
   }

   /**
    * Converts the pattern to a Perl 5 Regular Expression. This means that
    * every asterisk is replaced by a dot and an asterisk, every question mark
    * is replaced by a dot, an accent circunflex is prepended to the pattern
    * and a dollar sign is appended to the pattern.
    *
    * @param pattern
    *    the pattern to be converted, may not be <code>null</code>.
    *
    * @return
    *    the converted pattern, not <code>null</code>.
    *
    * @throws NullPointerException
    *    if <code>pattern == null</code>.
    */
   private String convertToPerl5RegularExpression(String pattern)
   throws NullPointerException {
      pattern = pattern.replaceAll(ASTERISK_REGEXP, PERL5_ASTERISK_WILDCARD);
      pattern = pattern.replace(QUESTION_MARK, DOT);
      pattern = CIRCUNFLEX + pattern + DOLLAR_SIGN;
      return pattern;
   }

   /**
    * Determines whether the provided pattern is valid.
    *
    * @param pattern
    *    the pattern to be validated, not <code>null</code>.
    *
    * @return
    *    boolean with the value <code>true</code> if the pattern is valid,
    *    otherwise <code>false</code>.
    *
    * @throws NullPointerException
    *    if <code>pattern == null</code>.
    */
   private boolean isValidPattern(String pattern)
   throws NullPointerException {
      char[] patternContents = pattern.toCharArray();
      int patternContentsLength = patternContents.length;
      boolean valid = true;

      for (int i= 0;i < patternContentsLength && valid == true; i++) {
         char currChar = patternContents[i];
         if (i < patternContentsLength - 1) {
            if (currChar == ASTERISK || currChar == QUESTION_MARK || currChar == CIRCUNFLEX || currChar == DOLLAR_SIGN) {
               char nextChar = patternContents[i + 1];
               if (nextChar == ASTERISK || nextChar == QUESTION_MARK || nextChar == CIRCUNFLEX || nextChar == DOLLAR_SIGN) {
                  valid = false;
               }
            }
         }
      }

      return valid;
   }
}
