/*
 * $Id$
 *
 * Copyright 2004 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.common.text;

/**
 * Text-related utility functions.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class TextUtils extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

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
   public static final String quote(String s) {
      if (s != null) {
         FastStringBuffer buffer = new FastStringBuffer(s.length() + 2);
         buffer.append('"');
         buffer.append(s);
         buffer.append('"');
         return buffer.toString();
      } else {
         return "(null)";
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
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
