/*
 * $Id$
 */
package org.xins.util.text;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Exception thrown if a character string does not match a certain format.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class FormatException
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
    * @param string
    *    the character string that mismatches the format, cannot be
    *    <code>null</code>.
    *
    * @param reason
    *    description of the problem, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null</code>.
    *
    * @return
    *    the message the constructor can pass up to the superclass
    *    constructor, never <code>null</code>.
    */
   private static final String createMessage(String string, String reason)
   throws IllegalArgumentException {

      // Check the precondition
      MandatoryArgumentChecker.check("string", string);

      FastStringBuffer buffer = new FastStringBuffer(128);

      buffer.append("The string \"");
      buffer.append(string);
      buffer.append("\" is invalid.");
      if (reason != null) {
         buffer.append(" Reason: ");
         buffer.append(reason);
      }

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a <code>FormatException</code>.
    *
    * @param string
    *    the character string that mismatches the format, cannot be
    *    <code>null</code>.
    *
    * @param reason
    *    description of the problem, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>s == null</code>.
    *
    * @return
    *    the message the constructor can pass up to the superclass
    *    constructor, never <code>null</code>.
    */
   public FormatException(String string, String reason)
   throws IllegalArgumentException {

      // Call superclass
      super(createMessage(string, reason));

      // Store information
      _string = string;
      _reason = reason;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The string that is considered invalid. Cannot be <code>null</code>.
    */
   private final String _string;

   /**
    * The reason for the string to be considered invalid. Can be
    * <code>null</code>.
    */
   private final String _reason;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the string that is considered invalid.
    *
    * @return
    *    the string that is considered invalid, cannot be <code>null</code>.
    */
   public String getString() {
      return _string;
   }

   /**
    * Returns the reason.
    *
    * @return
    *    the reason for the string to be considered invalid, can be
    *    <code>null</code>.
    */
   public String getReason() {
      return _reason;
   }
}
