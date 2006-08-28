/*
 * $Id$
 *
 * Copyright 2003-2006 Orange Nederland Breedband B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.logdoc;

/**
 * Error thrown if a mandatory locale is not supported by a <em>logdoc</em>
 * <code>Log</code> class.
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:ernst.dehaan@orange-ft.com">Ernst de Haan</a>
 *
 * @since XINS 1.2.0
 */
public final class UnsupportedLocaleError extends Error {

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Determines the message for an instance of this exception class.
    *
    * @param exception
    *    the source {@link UnsupportedLocaleException}, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the constructed message, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>exception == null</code>.
    */
   private static String createMessage(UnsupportedLocaleException exception)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("exception", exception);

      return exception.getMessage();
   }

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>UnsupportedLocaleError</code>.
    *
    * @param exception
    *    the source {@link UnsupportedLocaleException}, cannot be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>exception == null</code>.
    */
   public UnsupportedLocaleError(UnsupportedLocaleException exception)
   throws IllegalArgumentException {

      // Call superconstructor first
      super(createMessage(exception));

      // Make the UnsupportedLocaleException the cause for this Error
      ExceptionUtils.setCause(this, exception);

      // Store locale?
      _locale = exception.getLocale();
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   /**
    * The locale that is unsupported. The value of this field cannot be
    * <code>null</code>.
    */
   private final String _locale;


   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------

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
