/*
 * $Id$
 */
package org.xins.util.net;

import org.xins.util.MandatoryArgumentChecker;
import org.xins.util.text.FastStringBuffer;
import org.xins.util.text.FormatException;
import org.xins.util.text.NonASCIIException;

/**
 * URL encoding utility functions. This class supports both encoding and
 * decoding. Only 7-bit ASCII characters are supported. All characters higher
 * than 127 (0x7f) will cause the encode or decode operation to fail.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.106
 */
public final class URLEncoding extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   private static final int CHAR_ZERO = (int) '0';
   private static final int CHAR_NINE = (int) '9';
   private static final int CHAR_LOWER_A = (int) 'a';
   private static final int CHAR_LOWER_F = (int) 'f';
   private static final int CHAR_UPPER_A = (int) 'A';
   private static final int CHAR_UPPER_F = (int) 'F';

   /**
    * Mappings from unencoded (array index) to encoded values (array
    * elements). The size of this array is 127.
    */
   private static final String[] UNENCODED_TO_ENCODED;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   static {
      UNENCODED_TO_ENCODED = new String[127];
      for (int i = 0; i < 127; i++) {
         char c = (char) i;
         if ((c >= 'a' && c <= 'z')   || (c >= 'A' && c <= 'Z')   || (c >= '0' && c <= '9')
          || (c == '-') || (c == '_') || (c == '.') || (c == '*')) {
            UNENCODED_TO_ENCODED[i] = String.valueOf(c);
         } else if (c == ' ') {
            UNENCODED_TO_ENCODED[i] = "+";
         } else {
            char[] data = new char[3];
            data[0] = '%';
            data[1] = Character.toUpperCase(Character.forDigit((i >> 4) & 0xF, 16));
            data[2] = Character.toUpperCase(Character.forDigit( i       & 0xF, 16));
            UNENCODED_TO_ENCODED[i] = new String(data);
         }
      }
   }

   /**
    * URL encodes the specified character string.
    *
    * @param s
    *    the string to URL encode, not <code>null</code>.
    *
    * @return
    *    URL encoded version of the specified character string, never
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null</code>
    *
    * @throws NonASCIIException
    *    if <code>s.charAt(<em>n</em>) &gt; 127</code>,
    *    where <code>0 &lt;= <em>n</em> &lt; s.length</code>.
    */
   public static String encode(String s)
   throws IllegalArgumentException, NonASCIIException {

      // Check preconditions
      MandatoryArgumentChecker.check("s", s);

      // Construct a buffer
      int length = s.length();
      FastStringBuffer buffer = new FastStringBuffer(length * 2);

      // Loop through the string and just append whatever we find
      // in UNENCODED_TO_ENCODED
      int c = -99;
      try {
         for (int i = 0; i < length; i++) {
            c = (int) s.charAt(i);
            buffer.append(UNENCODED_TO_ENCODED[c]);
         }
      } catch (IndexOutOfBoundsException exception) {
         throw new NonASCIIException((char) c);
      }

      return buffer.toString();
   }

   /**
    * Decodes the specified URL encoded character string.
    *
    * @param s
    *    the URL encoded string to decode, not <code>null</code>.
    *
    * @return
    *    unencoded version of the specified URL encoded character string,
    *    never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null</code>.
    *
    * @throws FormatException
    *    if any of the following conditions is true:
    *    <ul>
    *        <li><code>s.{@link String#charAt(int) charAt}(<em>i</em>) &gt; (char) 127</code>
    *            (non-ASCII character in encoded string, where <code>0 &lt;= <em>i</em> &lt; s.length</code>).
    *        <li><code>s.{@link String#charAt(int) charAt}(s.{@link String#length() length}() - 1)</code>
    *            (last character is a percentage sign)
    *        <li><code>s.{@link String#charAt(int) charAt}(s.{@link String#length() length}() - 2)</code>
    *            (before-last character is a percentage sign)
    *        <li><code>s.{@link String#charAt(int) charAt}(<em>n</em>) == '%'
    *                  &amp;&amp; !(           {@link org.xins.util.text.HexConverter}.{@link org.xins.util.text.HexConverter#isHexDigit(char) isDigit}(s.{@link String#charAt(int) charAt}(<em>n</em> + 1))
    *                               &amp;&amp; {@link org.xins.util.text.HexConverter}.{@link org.xins.util.text.HexConverter#isHexDigit(char) isDigit}(s.{@link String#charAt(int) charAt}(<em>n</em> + 2)))</code>
    *            (percentage sign is followed by 2 characters of which at least one is not a hexadecimal digit)
    *    </ul>
    *
    * @throws NonASCIIException
    *    if a decoded character is found that has a value &gt; 127.
    */
   public static final String decode(String s)
   throws IllegalArgumentException, FormatException, NonASCIIException {

      // Check preconditions
      MandatoryArgumentChecker.check("s", s);

      // If the string is empty, return the original string
      int length = s.length();
      if (length == 0) {
         return s;
      }

      // Last character cannot be a percentage sign
      if (s.charAt(length - 1) == '%') {
         throw new FormatException(s, "Last character is a percentage sign.");
      }

      // If the string is only one character, return the original string
      if (length == 1) {
         int c = (int) s.charAt(0);
         if (c > 127) {
            throw new FormatException(s, "Character at position 0 has value " + c + '.');
         } else if (c == '+') {
            return " ";
         } else {
            return s;
         }
      }

      // Before-last character cannot be a percentage sign
      if (s.charAt(length - 2) == '%') {
         throw new FormatException(s, "Before-last character is a percentage sign.");
      }

      // Loop through the string
      FastStringBuffer buffer = new FastStringBuffer(length * 2);
      int index = 0;
      int last = length - 3;
      while (index <= last) {

         // Get the character
         char c = s.charAt(index);
         int charAsInt = (int) c;

         // Encoded character must be ASCII
         if (charAsInt > 127) {
            throw new FormatException(s, "Character at position " + index + " has value " + charAsInt + '.');

         // Special case: Recognize plus sign as a space
         } else if (c == '+') {
            buffer.append(' ');

         // Catch encoded characters
         } else if (c == '%') {
            int decodedValue;

            charAsInt = (int) s.charAt(++index);
            if (charAsInt >= CHAR_ZERO && charAsInt <= CHAR_NINE) {
               decodedValue = charAsInt - CHAR_ZERO;
            } else if (charAsInt >= CHAR_LOWER_A && charAsInt <= CHAR_LOWER_F) {
               decodedValue = charAsInt - CHAR_LOWER_A + 10;
            } else if (charAsInt >= CHAR_UPPER_A && charAsInt <= CHAR_UPPER_F) {
               decodedValue = charAsInt - CHAR_UPPER_A + 10;
            } else {
               throw new FormatException(s, "Character at position " + index + " is not a hex digit. Value is " + charAsInt + '.');
            }

            decodedValue *= 16;

            charAsInt = (int) s.charAt(++index);
            if (charAsInt >= CHAR_ZERO && charAsInt <= CHAR_NINE) {
               decodedValue += charAsInt - CHAR_ZERO;
            } else if (charAsInt >= CHAR_LOWER_A && charAsInt <= CHAR_LOWER_F) {
               decodedValue += charAsInt - CHAR_LOWER_A + 10;
            } else if (charAsInt >= CHAR_UPPER_A && charAsInt <= CHAR_UPPER_F) {
               decodedValue += charAsInt - CHAR_UPPER_A + 10;
            } else {
               throw new FormatException(s, "Character at position " + index + " is not a hex digit. Value is " + charAsInt + '.');
            }

            if (decodedValue > 127) {
               throw new NonASCIIException((char) decodedValue);
            }

            buffer.append((char) decodedValue);

         // Append the character
         } else {
            buffer.append(c);
         }

         // Proceed to the next character
         index++;
      }

      // Check and append before-last character
      if (index == length - 2) {
         char c        = s.charAt(index);
         int charAsInt = (int) c;
         if (charAsInt > 127) {
            throw new FormatException(s, "Character at position " + index + " has value " + charAsInt + '.');
         } else if (c == '+') {
            c = ' ';
         }
         buffer.append(c);
         index++;
      }

      // Check and append last character
      if (index == length - 1) {
         char c         = s.charAt(index);
         int charAsInt = (int) c;
         if (charAsInt > 127) {
            throw new FormatException(s, "Character at position " + index + " has value " + charAsInt + '.');
         } else if (c == '+') {
            c = ' ';
         }
         buffer.append(c);
      }

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>URLEncoding</code> object.
    */
   private URLEncoding() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
