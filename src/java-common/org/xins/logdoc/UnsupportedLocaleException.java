/*
 * $Id$
 */
package org.xins.logdoc;

/**
 * Exception thrown if a specified locale is not supported by at least one
 * <em>logdoc</em> <code>Log</code> class.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class UnsupportedLocaleException extends Exception {

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
    * Constructs a new <code>UnsupportedLocaleException</code>.
    *
    * @param locale
    *    the locale, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>locale == null</code>.
    */
   public UnsupportedLocaleException(String locale)
   throws IllegalArgumentException {

      // Call superconstructor first
      super("Locale \"" + locale + "\" is not supported.");

      // Check preconditions
      MandatoryArgumentChecker.check("locale", locale);

      // XXX: Store the locale ?
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
