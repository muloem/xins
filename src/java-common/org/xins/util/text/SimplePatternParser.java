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
 * Simple pattern parser. A simple pattern is a text string that may contain
 * the wildcard characters <code>'*'</code> (asterisk) and <code>'?'</code>
 * (question mark).
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
    */
   public Perl5Pattern parseSimplePattern(String simplePattern)
   throws IllegalArgumentException, ParseException {

      MandatoryArgumentChecker.check("simplePattern", simplePattern);

      if (isValidPattern(simplePattern) == false) {
         throw new ParseException("The pattern '" + simplePattern + "' is invalid.");
      }

      simplePattern = createPerl5RegularExpression(simplePattern);

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


   private String createPerl5RegularExpression(String pattern) {
      pattern = pattern.replaceAll("\\*", ".*");
      pattern = pattern.replace('?', '.');
      pattern = '^' + pattern + '$';
      return pattern;
   }



   private boolean isValidPattern(String pattern) {
      char[] patternContents = pattern.toCharArray();
      int patternContentsLength = patternContents.length;
      boolean valid = true;

      for (int i= 0;i < patternContentsLength && valid == true; i++) {
         char currChar = patternContents[i];
         if (i < patternContentsLength - 1) {
            if (currChar == '*' || currChar == '?' || currChar == '^' || currChar == '$') {
               char nextChar = patternContents[i + 1];
               if (nextChar == '*' || nextChar == '?' || nextChar == '^' || nextChar == '$') {
                  valid = false;
               }
            }
         }
      }

      return valid;
   }
}
