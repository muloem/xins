/*
 * $Id$
 */
package org.xins.common.specs;

import org.xins.common.MandatoryArgumentChecker;
import org.xins.common.text.FastStringBuffer;

/**
 * Exception that indicates that a specified string is not a well-formed
 * version.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.2.0
 */
public final class InvalidVersionException extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Creates the message the constructor can pass up to the superconstructor.
    *
    * @param version
    *    the string that is considered malformed as a version, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the message the constructor can pass up to the superconstructor,
    *    never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>version == null</code>.
    */
   private static final String createMessage(String version)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("version", version);

      FastStringBuffer buffer = new FastStringBuffer(80);
      buffer.append("The string \"");
      buffer.append(version);
      buffer.append("\" is invalid as a version string.");

      return buffer.toString();
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>InvalidVersionException</code>.
    *
    * @param version
    *    the string that is considered malformed as a version, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>version == null</code>.
    */
   InvalidVersionException(String version)
   throws IllegalArgumentException {

      super(createMessage(version));

      _version = version;
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The string that is considered malformed as a version. Never
    * <code>null</code>.
    */
   private final String _version;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

   /**
    * Returns the string that is considered malformed as a version.
    *
    * @return
    *    the string that is considered malformed as a version, never
    *    <code>null</code>.
    */
   private final String getVersion() {
      return _version;
   }
}
