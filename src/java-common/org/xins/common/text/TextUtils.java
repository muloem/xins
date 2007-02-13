/*
 * $Id$
 *
 * Copyright 2003-2007 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.text;

import java.util.Enumeration;
import java.util.Properties;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.ProgrammingException;
import org.xins.common.Utils;

/**
 * Text-related utility functions.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class TextUtils extends Object {

   /**
    * Perl 5 pattern compiler.
    */
   private static final Perl5Compiler PATTERN_COMPILER = new Perl5Compiler();

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Quotes the specified string, or returns <code>"(null)"</code> if it is
    * <code>null</code>.
    *
    * @param s
    *    the input string, or <code>null</code>.
    *
    * @return
    *    if <code>s != null</code> the quoted string, otherwise the string
    *    <code>"(null)"</code>.
    */
   public static String quote(String s) {
      if (s != null) {
         String quoted = '"' + s + '"';
         return quoted;
      } else {
         return "(null)";
      }
   }

   /**
    * Quotes the textual presentation (returned by <code>toString()</code>) of
    * the specified object, or returns <code>"(null)"</code> if the object is
    * <code>null</code>.
    *
    * @param o
    *    the object, or <code>null</code>.
    *
    * @return
    *    if <code>o != null</code> then <code>o.toString()</code> quoted,
    *    otherwise the string <code>"(null)"</code>.
    *
    * @since XINS 1.0.1
    */
   public static String quote(Object o) {
      String s = (o == null) ? null : o.toString();
      return quote(s);
   }

   /**
    * Determines if the specified string is <code>null</code> or an empty
    * string.
    *
    * @param s
    *    the string, or <code>null</code>.
    *
    * @return
    *    <code>true</code> if <code>s == null || s.length() &lt; 1</code>.
    *
    * @since XINS 1.0.1
    */
   public static boolean isEmpty(String s) {
      return (s == null) || (s.length() == 0);
   }

   /**
    * Trims the specified string, or returns an empty string if the argument
    * is <code>null</code>.
    *
    * @param s
    *    the string, or <code>null</code>.
    *
    * @param ifEmpty
    *    the string to return if
    *    <code>s == null || s.trim().length &lt; 1</code>.
    *
    * @return
    *    the trimmed version of the string (see {@link String#trim()}) or
    *    <code>ifEmpty</code> if
    *    <code>s == null</code> or <code>s.trim().length &lt; 1</code>.
    *
    * @since XINS 1.3.0
    */
   public static String trim(String s, String ifEmpty) {

      if (s != null) {
         s = s.trim();
         if (s.length() >= 1) {
            return s;
         }
      }

      return ifEmpty;
   }

   /**
    * Replaces substrings in a string. The substrings to be replaced are
    * passed in a {@link Properties} object. A prefix and a suffix can be
    * specified. These are prepended/appended to each of the search keys.
    *
    * <p />Example: If you have a string <code>"Hello ${name}"</code> and you
    * would like to replace <code>"${name}"</code> with <code>"John"</code>
    * and you would like to replace <code>${surname}</code> with
    * <code>"Doe"</code>, use the following code:
    *
    * <p /><blockquote><code>String s = "Hello ${name}";
    * <br />Properties replacements = new Properties();
    * <br />replacements.put("name", "John");
    * <br />replacements.put("surname", "Doe");
    * <br />
    * <br />StringUtils.replace(s, replacements, "${", "}");</code></blockquote>
    *
    * <p />The result string will be <code>"Hello John"</code>.
    *
    * @param s
    *    the text string to which replacements should be applied, not <code>null</code>.
    *
    * @param replacements
    *    the replacements to apply, not <code>null</code>.
    *
    * @param prefix
    *    the optional prefix for the search keys, or <code>null</code>.
    *
    * @param suffix
    *    the optional prefix for the search keys, or <code>null</code>.
    *
    * @return the String with the replacements.
    *
    * @throws IllegalArgumentException
    *    if one of the mandatory arguments is missing.
    *
    * @since XINS 1.4.0.
    */
   public static String replace(String s, Properties replacements, String prefix, String suffix)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("s", s, "replacements", replacements);

      // Make sure prefix and suffix are not null
      prefix = (prefix == null) ? "" : prefix;
      suffix = (suffix == null) ? "" : suffix;

      Enumeration keys = replacements.propertyNames();
      while (keys.hasMoreElements()) {
         String key    = (String) keys.nextElement();
         String search = prefix + key + suffix;
         int index = s.indexOf(search);
         while (index >= 0) {
            String replacement = replacements.getProperty(key);
            s = s.substring(0, index) + replacement + s.substring(index + search.length());
            index = s.indexOf(search);
         }
      }

      return s;
   }

   /**
    * Tranforms the given <code>String</code> to the similar <code>String</code>, 
    * but starting with an uppercase.
    *
    * @param text
    *    the text to transform, can be <code>null</code>
    *
    * @return
    *    the transformed text, the return value will start with an uppercase. 
    *    <code>null</code> is returned if the text was <code>null</code>.
    *
    * @since XINS 2.0.0.
    */
   public static String firstCharUpper(String text) {
       if (text == null) {
           return null;
       } else if (text.length() == 0) {
           return text;
       } else if (!Character.isLowerCase(text.charAt(0))) {
           return text;
       } else if (text.length() == 1) {
           return text.toUpperCase();
       } else {
           return text.substring(0, 1).toUpperCase() + text.substring(1);
       }
       
   }

   /**
    * Tranforms the given <code>String</code> to the similar <code>String</code>, 
    * but starting with a lowercase.
    *
    * @param text
    *    the text to transform, can be <code>null</code>
    *
    * @return
    *    the transformed text, the return value will start with a lowercase. 
    *    <code>null</code> is returned if the text was <code>null</code>.
    *
    * @since XINS 2.0.0.
    */
   public static String firstCharLower(String text) {
       if (text == null) {
           return null;
       } else if (text.length() == 0) {
           return text;
       } else if (!Character.isUpperCase(text.charAt(0))) {
           return text;
       } else if (text.length() == 1) {
           return text.toLowerCase();
       } else {
           return text.substring(0, 1).toLowerCase() + text.substring(1);
       }
   }

   /**
    * Compile the given regular expression to a Perl5 pattern object.
    * 
    * @param regexp
    *     the String value of the Perl5 regular expresssion, cannot be <code>null</code>.
    *
    * @return
    *    the Perl5 pattern, never <code>null</code>
    *
    * @throws IllegalArgumentException
    *    if <code>regexp == null</code>.
    *
    * @throws ProgrammingException
    *    if the pattern cannot be complied.
    *
    * @since XINS 2.0.0.
    */
   public static Pattern createPattern(String regexp) throws ProgrammingException {
      MandatoryArgumentChecker.check("regexp", regexp);
      try {
         Pattern pattern = PATTERN_COMPILER.compile(regexp,
               Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.CASE_INSENSITIVE_MASK);
         return pattern;
      } catch (MalformedPatternException exception) {
         throw Utils.logProgrammingError(exception);
      }
   }

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>TextUtils</code> object.
    */
   private TextUtils() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
