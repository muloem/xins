/*
 * $Id$
 */
package org.xins.common.net;

import java.util.StringTokenizer;
import org.xins.common.text.ParseException;

/**
 * IP address-related utility functions.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.153
 */
public final class IPAddressUtils extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Converts an IP address in the form <em>a.b.c.d</em> to an
    * <code>int</code>.
    *
    * @param ip
    *    the IP address, must be in the form:
    *    <em>a.a.a.a.</em>, where <em>a</em> is a number between 0 and 255,
    *    with no leading zeroes; cannot be <code>null</code>.
    *
    * @return
    *    the IP address as an <code>int</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>ip == null</code>.
    *
    * @throws ParseException
    *    if <code>ip</code> cannot be parsed as an IP address.
    */
   public static final int ipToInt(String ip)
   throws IllegalArgumentException, ParseException {

      int value = 0;

      // Tokenize the string
      StringTokenizer tokenizer = new StringTokenizer(ip, ".", true);
      if (tokenizer.countTokens() != 7) {
         throw newParseException(ip);
      }

      // Token 1 must be an IP address part
      value = ipPartToInt(ip, tokenizer.nextToken());

      // Token 2 must be a dot
      if (!".".equals(tokenizer.nextToken())) {
         throw newParseException(ip);
      }

      // Token 3 must be an IP address part
      value <<= 8;
      value += ipPartToInt(ip, tokenizer.nextToken());

      // Token 4 must be a dot
      if (!".".equals(tokenizer.nextToken())) {
         throw newParseException(ip);
      }

      // Token 5 must be an IP address part
      value <<= 8;
      value += ipPartToInt(ip, tokenizer.nextToken());

      // Token 6 must be a dot
      if (!".".equals(tokenizer.nextToken())) {
         throw newParseException(ip);
      }

      // Token 7 must be an IP address part
      value <<= 8;
      value += ipPartToInt(ip, tokenizer.nextToken());

      return value;
   }

   /**
    * Converts the specified component of an IP address to a number between 0
    * and 255.
    *
    * @param ip
    *    the complete IP address, needed when throwing a
    *    {@link ParseException}, should not be <code>null</code>; if it is,
    *    then the behaviour is undefined.
    *
    * @param part
    *    the part to convert to an <code>int</code> number, should not be
    *    <code>null</code>; if it is, then the behaviour is undefined.
    *
    * @return
    *    the <code>int</code> value of the part, between 0 and 255
    *    (inclusive).
    *
    * @throws ParseException
    *    if the part cannot be parsed.
    */
   private static final int ipPartToInt(String ip, String part)
   throws ParseException {

      int length = part.length();

      if (length == 1) {
         char c0 = part.charAt(0);
         if (c0 >= '0' && c0 <= '9') {
            return (c0 - '0');
         }

      } else if (length == 2) {
         char c0 = part.charAt(0);
         char c1 = part.charAt(1);

         if (c0 >= '1' && c0 <= '9' && c1 >= '0' && c1 <= '9') {
            return ((c0 - '0') * 10) + (c1 - '0');
         }

      } else if (length == 3) {
         char c0 = part.charAt(0);
         char c1 = part.charAt(1);
         char c2 = part.charAt(2);

         if (c0 >= '1' && c0 <= '2' && c1 >= '0' && c1 <= '9' && c2 >= '0' && c2 <= '9') {
            int value = ((c0 - '0') * 100) + ((c1 - '0') * 10) + (c2 - '0');
            if (value <= 255) {
               return value;
            }
         }
      }

      throw newParseException(ip);
   }

   /**
    * Constructs a new <code>ParseException</code> for the specified malformed
    * IP address.
    *
    * @param ip
    *    the malformed IP address, not <code>null</code>; if it is, then the
    *    behaviour is undefined.
    *
    * @return
    *    the {@link ParseException} to throw.
    */
   private static final ParseException newParseException(String ip) {
      return new ParseException("The string \"" + ip + "\" is not a valid IP address.");
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>IPAddressUtils</code> object.
    */
   private IPAddressUtils() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
