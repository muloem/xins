/*
 * $Id$
 */
package org.xins.util.text;

import java.util.ArrayList;
import java.util.List;
import org.xins.util.text.FastStringBuffer;
import org.xins.util.MandatoryArgumentChecker;

/**
 * Fast character string tokenizer. This tokenizer needs a separator to be
 * specified. It will allow whitespace characters after the separator. Any
 * empty tokens will be returned as <code>null</code>.
 *
 * <p><a name="whitespace"></a>The following characters are considered
 * whitespace:
 *
 * <ul>
 *   <li><code>'\t'</code>, tab,             character  9 (0x09).
 *   <li><code>'\n'</code>, newline,         character 10 (0x0a).
 *   <li><code>'\r'</code>, carriage return, character 13 (0x0d).
 *   <li><code>' '</code>,  space,           character 32 (0x20).
 * </ul>
 *
 * <p>For example, if the string <code>"Hello, there, I'm  ,,  ,bored "</code>
 * is tokenized with <code>','</code> as the separator, the returned array
 * will be equivalent to:
 *
 * <blockquote><code>{ "Hello", "there", "I'm  ", null, null, "bored " }</code></blockquote>
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.105
 */
public final class FastTokenizer extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The tab character, <code>'\t'</code>.
    */
   private static final char CHAR_TAB_9 = '\t';

   /**
    * The line feed character, <code>'\n'</code>.
    */
   private static final char CHAR_LF_10 = '\n';

   /**
    * The carriage return character, <code>'\r'</code>.
    */
   private static final char CHAR_CR_13 = '\r';

   /**
    * The space character, <code>' '</code>.
    */
   private static final char CHAR_SPACE_32 = ' ';


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Tokenizes the specified string.
    *
    * @param s
    *    the string to be tokenized, not <code>null</code>.
    *
    * @param separator
    *    the separator, cannot be <a href="#whitespace">whitespace</a>.
    *
    * @return
    *    the tokens, or <code>null</code> if there are none; thus the returned
    *    array will always have a size greater than 0.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null || separator == '\t' || separator == '\n'
    *                       || separator == '\r' || separator == ' '</code>.
    */
   public static String[] tokenize(String s, char separator)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("s", s);
      if (separator <= CHAR_SPACE_32) {
         if (separator == CHAR_TAB_9) {
            throw new IllegalArgumentException("separator == '\\t'");
         } else if (separator == CHAR_LF_10) {
            throw new IllegalArgumentException("separator == '\\n'");
         } else if (separator == CHAR_CR_13) {
            throw new IllegalArgumentException("separator == '\\r'");
         } else if (separator == CHAR_SPACE_32) {
            throw new IllegalArgumentException("separator == ' '");
         }
      }

      // Loop through all characters
      int count = s.length();
      List result = new ArrayList(5);
      FastStringBuffer token = new FastStringBuffer(32);
      for (int i = 0; i < count; i++) {
/*
         char c = s.charAt(i);
         if (c == separator) {
            result.add(token.getLength() == 0 ? null : token.toString());

            // Skip all whitespace
            if (++i < count) {
               do {
                  c = s.charAt(i++);
               } while (i < count && c == CHAR_TAB_9 || c == CHAR_LF_10 || c == CHAR_CR_13 || c == CHAR_SPACE_32);
            }

            // Correct index so that it's picked up as part of the next token
            if (i < count) {
               i--;
            }
         } else {
            token.append(c);
         }
*/
      }

      final String[] EMPTY_STRING_ARRAY = new String[0];
      return (String[]) result.toArray(EMPTY_STRING_ARRAY);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>FastTokenizer</code>.
    */
   private FastTokenizer() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
