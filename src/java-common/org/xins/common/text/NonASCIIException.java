/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.text;

/**
 * Exception thrown when a character was found that was not a 7-bit ASCII
 * character where not allowed.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
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
   private static String createMessage(char c)
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
    *    the found character, must be &gt; 127.
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
