/*
 * $Id$
 */
package org.xins.specs;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Exception that indicates that a specified string is not a well-formed
 * version.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 *
 * @since XINS 0.141
 */
public final class InvalidVersionException extends Exception {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

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

      // Check preconditions
      MandatoryArgumentChecker.check("version", version);

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
