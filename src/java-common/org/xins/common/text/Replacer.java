/*
 * $Id$
 */
package org.xins.common.text;

import java.util.Enumeration;
import java.util.Properties;
import org.xins.common.MandatoryArgumentChecker;

/**
 * Replacer.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public class Replacer extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Applies replacements to the specified character string. If a property
    * cannot be found in the specified {@link Properties} object, then an
    * exception is thrown.
    *
    * @param text
    *    the source character string, cannot be <code>null</code> but it can
    *    be an empty string.
    *
    * @param tagStart
    *    the start marker of a string that should be replaced.
    *
    * @param tagEnd
    *    the end marker of a string that should be replaced.
    *
    * @param properties
    *    the properties to lookup keys (between <code>tagStart</code> and
    *    <code>tagEnd</code>) to replace by values, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the character string with the replacements applied, never
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>text == null || properties == null</code>.
    *
    * @throws Replacer.Exception
    *    if a tag start was found without a matching tag end, if an empty
    *    replacement tag was found or if no the replacement tag was not found
    *    in <code>properties</code>..
    */
   public static final String replace(String     text,
                                      char       tagStart,
                                      char       tagEnd,
                                      Properties properties)
   throws IllegalArgumentException, Replacer.Exception {

      // Check preconditions
      MandatoryArgumentChecker.check("text", text, "properties", properties);

      // Initialize a string buffer
      int textLength = text.length();
      if (textLength == 0) {
         return text;
      }

      FastStringBuffer buffer = new FastStringBuffer(textLength * 2);

      FastStringBuffer key = new FastStringBuffer(32);

      for (int i = 0; i < textLength; i++) {
         char c = text.charAt(i);
         if (c == tagStart) {
            int startIndex = i;
            do {
               i++;
               if (i == textLength) {
                  throw new Replacer.Exception("Found tag start ('" + tagStart + "') at index " + startIndex + " in string \"" + text + "\", but no tag end ('" + tagEnd + "') was found.");
               }
               c = text.charAt(i);
               if (c != tagEnd) {
                  key.append(c);
               }
            } while (c != tagEnd);

            if (key.getLength() == 0) {
               throw new Replacer.Exception("Found empty replacement tag at index " + startIndex + " in string \"" + text + "\".");
            }

            String replacement = properties.getProperty(key.toString());
            if (replacement == null) {
               throw new Replacer.Exception("No replacement found for key \"" + key.toString() + "\".");
            }
            key.clear();
            buffer.append(replacement);
         } else {
            buffer.append(c);
         }
      }

      return buffer.toString();
   }

   public static final Properties replace(Properties source,
                                          char       tagStart,
                                          char       tagEnd,
                                          Properties properties)
   throws IllegalArgumentException, Replacer.Exception {

      // Check preconditions
      MandatoryArgumentChecker.check("source", source, "properties", properties);

      Properties result = new Properties();

      Enumeration e = source.propertyNames();
      while (e.hasMoreElements()) {

         String key   = (String) e.nextElement();
         String value = source.getProperty(key);

         value = replace(value, tagStart, tagEnd, properties);
         result.put(key, value);
      }

      return result;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Replacer</code> object.
    */
   private Replacer() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Inner classes
   //-------------------------------------------------------------------------

   /**
    * Exception thrown when a replacement cannot be applied.
    *
    * @version $Revision$ $Date$
    * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
    */
   public static class Exception extends java.lang.Exception {

      //----------------------------------------------------------------------
      // Constructors
      //----------------------------------------------------------------------

      /**
       * Constructs a new <code>Exception</code> with the specified detail
       * message.
       *
       * @param message
       *    the detail message, or <code>null</code>.
       */
      private Exception(String message) {
         super(message);
      }


      //----------------------------------------------------------------------
      // Fields
      //----------------------------------------------------------------------

      //----------------------------------------------------------------------
      // Methods
      //----------------------------------------------------------------------
   }
}
