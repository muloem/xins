/*
 * $Id$
 *
 * Copyright 2003-2008 Online Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.logdoc;

/**
 * Exception thrown if a specified locale is not supported by at least one
 * <em>logdoc</em> <code>Log</code> class.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @since XINS 1.0.0
 */
public final class UnsupportedLocaleException extends RuntimeException {

   /**
    * The locale that is unsupported. The value of this field cannot be
    * <code>null</code>.
    */
   private final String _locale;

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
      if (locale == null) {
         throw new IllegalArgumentException("locale == null");
      }

      // Store locale?
      _locale = locale;
   }

   /**
    * Retrieves the unsupported locale.
    *
    * @return
    *    the unsupported locale, never <code>null</code>.
    */
   public String getLocale() {
      return _locale;
   }
}
