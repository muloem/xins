/*
 * $Id$
 *
 * Copyright 2003-2005 Wanadoo Nederland B.V.
 * See the COPYRIGHT file for redistribution and use restrictions.
 */
package org.xins.logdoc;

import org.apache.log4j.NDC;

/**
 * Central class for <em>logdoc</em> logging.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 1.0.0
 */
public final class LogCentral {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The property used to change the locale of the logdoc.
    */
   public final static String LOG_LOCALE_PROPERTY = "org.xins.logdoc.locale";

   /**
    * The default locale used for starting up when the locale is not defined in
    * command line arguments.
    */
   public static final String DEFAULT_LOCALE = "en_US";

   /**
    * All registered <code>LogController</code> instances.
    *
    * @see #registerLog(AbstractLog.LogController)
    */
   private static AbstractLog.LogController[] CONTROLLERS;

   /**
    * The locale for the logdoc.
    */
   private static String LOCALE = null;


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Registers the specified <code>LogController</code>, which represents a
    * <em>logdoc</em> <code>Log</code> class.
    *
    * @param controller
    *    the {@link AbstractLog.LogController}, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>controller == null</code>.
    *
    * @throws UnsupportedLocaleException
    *    if {@link AbstractLog.LogController} does not support the current Locale.
    */
   static void registerLog(AbstractLog.LogController controller)
   throws IllegalArgumentException, UnsupportedLocaleException {

      // Check preconditions
      MandatoryArgumentChecker.check("controller", controller);

      // When the first LogController registers, set the locale.
      if (LOCALE == null) {
         initStartupLocale();
      }

      if (controller.isLocaleSupported(LOCALE)) {
         controller.setLocale(LOCALE);
      } else {
         throw new UnsupportedLocaleException(LOCALE);
      }

      // Add the controller to the List
      if (CONTROLLERS == null) {
         CONTROLLERS = new AbstractLog.LogController[] { controller };
      } else {
         int size = CONTROLLERS.length;
         AbstractLog.LogController[] newControlers = new AbstractLog.LogController[size + 1];
         System.arraycopy(CONTROLLERS, 0, newControlers, 0, size);
         newControlers[size] = controller;
         CONTROLLERS = newControlers;
      }
   }

   /**
    * Initializes the start-up locale. If the system property
    * {@link #LOG_LOCALE_PROPERTY} is set, then this will be used as the
    * locale, otherwise {@link #DEFAULT_LOCALE} is assumed.
    *
    * <p>This method is called from
    * {@link #registerLog(AbstractLog.LogController)} as soon as the first
    * {@link AbstractLog.LogController} is registered.
    */
   private static void initStartupLocale() {
      String startupLocale = System.getProperty(LOG_LOCALE_PROPERTY);
      if (startupLocale == null || startupLocale.trim().equals("")) {
         LOCALE = DEFAULT_LOCALE;
      } else {
         LOCALE = startupLocale;
      }
   }

   /**
    * Returns the current diagnostic context identifier.
    *
    * @return
    *    the current diagnostic context identifier, or <code>null</code> if
    *    there is none.
    */
   public static String getContext() {
      return NDC.peek();
   }

   /**
    * Sets the locale on all <em>logdoc</em> <code>Log</code> classes.
    *
    * @param newLocale
    *    the new locale, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>newLocale == null</code>.
    *
    * @throws UnsupportedLocaleException
    *    if the specified locale is not supported by all registered
    *    <em>logdoc</em> <code>Log</code> classes.
    */
   public static void setLocale(String newLocale)
   throws IllegalArgumentException, UnsupportedLocaleException {

      // Check preconditions
      MandatoryArgumentChecker.check("newLocale", newLocale);
System.err.println("Changing to locale \"" + newLocale + "\".");

      // Make sure the locale is supported by all controllers
      int size = CONTROLLERS.length;
      for (int i = 0; i < size; i++) {
         if (!CONTROLLERS[i].isLocaleSupported(newLocale)) {
System.err.println("Locale \"" + newLocale + "\" is not supported by log controller " + i + ": " + CONTROLLERS[i]);
            throw new UnsupportedLocaleException(newLocale);
         }
      }

      // Change the locale on all controllers
      // XXX This should be removed and the controller should invoke LogCentral.getLocale()
      for (int i = 0; i < size; i++) {
         CONTROLLERS[i].setLocale(newLocale);
      }

      LOCALE = newLocale;
System.err.println("Changed to locale \"" + newLocale + "\".");
   }

   /**
    * Sets the locale on all <em>logdoc</em> <code>Log</code> classes to the
    * default locale.
    *
    * @since XINS 1.3.0
    */
   public static void useDefaultLocale() {
      try {
         setLocale(DEFAULT_LOCALE);
      } catch (UnsupportedLocaleException ule) {
         String detail = "Failed to apply default locale (\""
                       + DEFAULT_LOCALE
                       + "\").";
         RuntimeException exception = new RuntimeException(detail);
         ExceptionUtils.setCause(exception, ule);
         throw exception;
      }
   }

   /**
    * Get the locale set in this LogCentral.
    *
    * @return
    *    the locale as set for java properties file (e.g. "en_US").
    */
   public static String getLocale() {
      return LOCALE;
   }

   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>LogCentral</code> instance. This constructor is
    * intentionally made <code>private</code>, since no instances should be
    * constructed of this class.
    */
   private LogCentral() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
