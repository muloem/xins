/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.text;

import org.xins.common.MandatoryArgumentChecker;

/**
 * URL encoding utility functions with Unicode support. This class supports
 * both encoding and decoding. All characters higher than 127 will be encoded 
 * as %uxxxx where xxxx is the Unicode value of the character in hexadecimal.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 * @author Anthony Goubard (<a href="mailto:anthony.goubard@nl.wanadoo.com">anthony.goubard@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class URLEncoding extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The character zero (<code>'0'</code>) as an <code>int</code>.
    */
   private static final int CHAR_ZERO = (int) '0';

   /**
    * The character nine (<code>'9'</code>) as an <code>int</code>.
    */
   private static final int CHAR_NINE = (int) '9';

   /**
    * The character lowercase A (<code>'a'</code>) as an <code>int</code>.
    */
   private static final int CHAR_LOWER_A = (int) 'a';

   /**
    * The character lowercase F (<code>'f'</code>) as an <code>int</code>.
    */
   private static final int CHAR_LOWER_F = (int) 'f';

   /**
    * The character uppercase A (<code>'A'</code>) as an <code>int</code>.
    */
   private static final int CHAR_UPPER_A = (int) 'A';

   /**
    * The character uppercase F (<code>'F'</code>) as an <code>int</code>.
    */
   private static final int CHAR_UPPER_F = (int) 'F';

   /**
    * The character lowercase U (<code>'u'</code>) as an <code>int</code>.
    */
   private static final int CHAR_LOWER_U = (int) 'u';

   /**
    * The character uppercase U (<code>'U'</code>) as an <code>int</code>.
    */
   private static final int CHAR_UPPER_U = (int) 'U';

   /**
    * Mappings from unencoded (array index) to encoded values (array
    * elements). The size of this array is 127.
    */
   private static final String[] UNENCODED_TO_ENCODED;

   /**
    * Table which indicates the valid encoded characters.
    */
   private static final boolean[] VALID_ENCODED_CHAR;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   static {
      UNENCODED_TO_ENCODED = new String[255];
      VALID_ENCODED_CHAR = new boolean[255];
      for (int i = 0; i < 255; i++) {
         char c = (char) i;
         if ((c >= 'a' && c <= 'z') ||
             (c >= 'A' && c <= 'Z') ||
             (c >= '0' && c <= '9') ||
             (c == '-')             ||
             (c == '_')             ||
             (c == '.')             ||
             (c == '*')) {
            UNENCODED_TO_ENCODED[i] = String.valueOf(c);
            VALID_ENCODED_CHAR[i] = true;
         } else if (c == ' ') {
            UNENCODED_TO_ENCODED[i] = "+";
            VALID_ENCODED_CHAR[i] = false;
         } else {
            char[] data = new char[3];
            data[0] = '%';
            data[1] = Character.toUpperCase(Character.forDigit((i >> 4) & 0xF, 16));
            data[2] = Character.toUpperCase(Character.forDigit( i       & 0xF, 16));
            UNENCODED_TO_ENCODED[i] = new String(data);
            VALID_ENCODED_CHAR[i] = false;
         }
      }

      // XXX: Allow test coverage analysis tools to report 100% coverage
      new URLEncoding();
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
    */
   public static String encode(String s)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("s", s);

      // Short-circuit if the string is empty
      int length = s.length();
      if (length < 1) {
         return "";
      }

      // Construct a buffer
      FastStringBuffer buffer = new FastStringBuffer(length * 2);

      // Loop through the string and just append whatever we find
      // in UNENCODED_TO_ENCODED or if c > 255, append %u and the
      // value of the Unicode character in hexadecimal with 4 digits/letters.
      char[] content = s.toCharArray();
      for (int i = 0; i < length; i++) {
         int c = (int) content[i];
         if (c < 256) {
            buffer.append(UNENCODED_TO_ENCODED[c]);
         } else {
            buffer.append("%u");
            buffer.append(Character.toUpperCase(Character.forDigit((c >> 12) & 0xF, 16)));
            buffer.append(Character.toUpperCase(Character.forDigit((c >> 8)  & 0xF, 16)));
            buffer.append(Character.toUpperCase(Character.forDigit((c >> 4)  & 0xF, 16)));
            buffer.append(Character.toUpperCase(Character.forDigit( c        & 0xF, 16)));
         }
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
    *                  &amp;&amp; !(           {@link org.xins.common.text.HexConverter}.{@link org.xins.common.text.HexConverter#isHexDigit(char) isDigit}(s.{@link String#charAt(int) charAt}(<em>n</em> + 1))
    *                               &amp;&amp; {@link org.xins.common.text.HexConverter}.{@link org.xins.common.text.HexConverter#isHexDigit(char) isDigit}(s.{@link String#charAt(int) charAt}(<em>n</em> + 2)))</code>
    *            (percentage sign is followed by 2 characters of which at least one is not a hexadecimal digit)
    *    </ul>
    */
   public static String decode(String s)
   throws IllegalArgumentException, FormatException {

      // Check preconditions
      MandatoryArgumentChecker.check("s", s);

      // If the string is empty, return the original string
      int length = s.length();
      if (length == 0) {
         return s;
      }

      // Avoid calls to charAt() method.
      char[] string = s.toCharArray();

      // Loop through the string
      FastStringBuffer buffer = new FastStringBuffer(length * 2);
      int index = 0;
      while (index < length) {

         // Get the character
         char c = string[index];
         int charAsInt = (int) c;

         // Encoded character must be ASCII
         if (charAsInt > 127) {
            throw new FormatException(s, "Character at position " + index + " has invalid value " + charAsInt + '.');

         // Special case: Recognize plus sign as a space
         } else if (c == '+') {
            buffer.append(' ');

         // Catch encoded characters
         } else if (c == '%') {
            int decodedValue = 0;

            if (index >= length - 2) {
                throw new FormatException(s, "Character at position " + index + " has invalid value " + charAsInt + '.');
            }
            charAsInt = (int) string[++index];
            if (charAsInt == CHAR_LOWER_U || charAsInt == CHAR_UPPER_U) {
                if (index >= length - 4) {
                    throw new FormatException(s, "Character at position " + index + " has invalid value " + charAsInt + '.');
                }
               charAsInt = (int) string[++index];
               decodedValue += digit(charAsInt, s, index);
               decodedValue *= 16;
               charAsInt = (int) string[++index];
               decodedValue += digit(charAsInt, s, index);
               decodedValue *= 16;
               charAsInt = (int) string[++index];
            } else if (charAsInt < CHAR_ZERO || 
                  (charAsInt > CHAR_NINE && charAsInt < CHAR_UPPER_A) ||
                  (charAsInt > CHAR_UPPER_F && charAsInt < CHAR_LOWER_A) ||
                  charAsInt > CHAR_LOWER_F) {
               throw new FormatException(s, "Character at position " + index + " has invalid value " + charAsInt + '.');
            }
            decodedValue += digit(charAsInt, s, index);
            decodedValue *= 16;
            charAsInt = (int) string[++index];
            decodedValue += digit(charAsInt, s, index);

            buffer.append((char) decodedValue);

         // Catch invalid characters
         } else if (!VALID_ENCODED_CHAR[c]) {
            throw new FormatException(s, "Character at position " + index + " has invalid value " + charAsInt + '.');
            
         // Append the character
         } else {
            buffer.append(c);
         }

         // Proceed to the next character
         index++;
      }

      return buffer.toString();
   }

   /**
    * Convert a hexadecimal digit to a number.
    *
    * @param charAsInt
    *    the hexadecimal digit.
    *
    * @param s
    *    the String from which the character has been taken.
    *
    * @param index
    *    the position of the character within the String.
    *
    * @throws FormatException
    *    if c is not a numerical digit or a letter between 'a' and 'f' or
    *    'A' or 'F'.
    */
   private static int digit(int charAsInt, String s, int index) throws FormatException {
      int decodedValue = 0;
      if (charAsInt >= CHAR_ZERO && charAsInt <= CHAR_NINE) {
         decodedValue = charAsInt - CHAR_ZERO;
      } else if (charAsInt >= CHAR_LOWER_A && charAsInt <= CHAR_LOWER_F) {
         decodedValue = charAsInt - CHAR_LOWER_A + 10;
      } else if (charAsInt >= CHAR_UPPER_A && charAsInt <= CHAR_UPPER_F) {
         decodedValue = charAsInt - CHAR_UPPER_A + 10;
      } else {
         throw new FormatException(s, "Character at position " + index + " is not a hex digit. Value is " + charAsInt + '.');
      }
      return decodedValue;
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
