/*
 * $Id$
 */
package org.xins.util.text;

/**
 * Exception thrown when a character was found that was not a 7-bit ASCII
 * character where not allowed.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class NonASCIIException
extends RuntimeException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates a message for the constructor.
    *
    * @param c
    *    the character argument passed to the constructor.
    *
    * @throws IllegalArgumentException
    *    if <code>c &lt;= 127</code>.
    *
    * @return
    *    the message the constructor can pass up to the superclass
    *    constructor, never <code>null</code>.
    */
   private static final String createMessage(char c)
   throws IllegalArgumentException {

      // Check the precondition
      if (c <= 127) {
         throw new IllegalArgumentException("c (" + ((int) c) + ") <= 127");
      }

      final String PREFIX = "Character ";
      final String SUFFIX = " is not a 7-bit ASCII character.";
      final int BUFLEN = PREFIX.length() + 3 + SUFFIX.length();

      FastStringBuffer buffer = new FastStringBuffer(BUFLEN);

      buffer.append(PREFIX);
      buffer.append(String.valueOf((int) c));
      buffer.append(SUFFIX);

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a <code>NonASCIIException</code>.
    *
    * @param c
    *    The found character, must be &gt; 127.
    *
    * @throws IllegalArgumentException
    *    if <code>c &lt;= 127</code>.
    */
   public NonASCIIException(char c)
   throws IllegalArgumentException {
      super(createMessage(c));
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
