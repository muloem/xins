/*
 * $Id$
 */
package org.xins.server;

/**
 * Class that represents the XINS/Java Server Framework library.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.8
 */
public final class Library extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Regular expression that production release versions of XINS match, and
    * non-production release version do not.
    */
   private static final String PRODUCTION_RELEASE_PATTERN = "[1-9][0-9]*\\.[0-9]+\\.[0-9]+";


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns the version of this library.
    *
    * @return
    *    the version of this library, for example <code>"%%VERSION%%"</code>,
    *    never <code>null</code>.
    */
   public static final String getVersion() {
      return "%%VERSION%%";
   }

   /**
    * Checks if the specified version indicates a production release of XINS.
    *
    * @return
    *    <code>true</code> is the specified XINS version identifies a
    *    production release of XINS, <code>false</code> if it does not.
    */
   static final boolean isProductionRelease(String version) {
      return version.matches(PRODUCTION_RELEASE_PATTERN);
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Library</code> object.
    */
   private Library() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
