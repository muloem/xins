/*
 * $Id$
 *
 * Copyright 2003-2006 Wanadoo Nederland B.V.
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
    * The name of the property that specifies which locale should be used.
    */
   public final static String LOG_LOCALE_PROPERTY = "org.xins.logdoc.locale";

   /**
    * The name of the property that specifies if the stack traces should be displayed at the message level.
    */
   public final static String LOG_STACK_TRACE_AT_MESSAGE_LEVEL = "org.xins.logdoc.stackTraceAtMessageLevel";

   /**
    * The default locale used at start-up, if no locale is specified in a
    * system property.
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

   /**
    * Flag indicating whether the stack trace shol be displayed at the same
    * level of the message or not.
    */
   private static boolean STACK_TRACE_AT_MESSAGE_LEVEL = false;


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
         LOCALE = determineStartupLocale();
      }

      // Set the locale on the controller
      if (controller.isLocaleSupported(LOCALE)) {
         controller.setLocale(LOCALE);

      // Fail if the controller does not support this locale
      } else {
         System.err.println("Locale \"" + LOCALE + "\" is not supported by log controller: " + controller);
         throw new UnsupportedLocaleException(LOCALE);
      }

      // Add it to the list of registered controllers
      if (CONTROLLERS == null) {
         CONTROLLERS = new AbstractLog.LogController[] { controller };
      } else {
         int size = CONTROLLERS.length;
         AbstractLog.LogController[] temp = new AbstractLog.LogController[size + 1];
         System.arraycopy(CONTROLLERS, 0, temp, 0, size);
         temp[size] = controller;
         CONTROLLERS = temp;
      }
   }

   /**
    * Determines the start-up locale. If the system property
    * {@link #LOG_LOCALE_PROPERTY} is set to a non-empty value, then this
    * will be returned, otherwise {@link #DEFAULT_LOCALE} is returned.
    *
    * <p>This method is called from
    * {@link #registerLog(AbstractLog.LogController)} as soon as the first
    * {@link AbstractLog.LogController} is registered.
    *
    * @return
    *    the locale to use initially, at start-up.
    */
   private static String determineStartupLocale() {

      // Use the value of the system property, if set
      String locale = System.getProperty(LOG_LOCALE_PROPERTY);
      if (locale != null && locale.trim().length() > 0) {
         return locale;

      // Fallback to the default locale
      } else {
         return DEFAULT_LOCALE;
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

      // Short-circuit if the new locale equals the current one
      if (newLocale.equals(LOCALE)) {
         return;
      }

      // Make sure the locale is supported by all controllers
      int size = (CONTROLLERS == null) ? 0 : CONTROLLERS.length;
      for (int i = 0; i < size; i++) {
         if (!CONTROLLERS[i].isLocaleSupported(newLocale)) {
            throw new UnsupportedLocaleException(newLocale);
         }
      }

      // Change the locale on all controllers
      // XXX This should be removed and the controller should invoke LogCentral.getLocale()
      for (int i = 0; i < size; i++) {
         CONTROLLERS[i].setLocale(newLocale);
      }

      LOCALE = newLocale;
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

   /**
    * Sets the display of the stack trace at the same level as the message.
    *
    * @param sameLevel
    *    <code>true</code> if the stack trace should be at the same level,
    *    <code>false</code> if the stack trace should be at DEBUG level.
    */
   public static void setStackTraceAtMessageLevel(boolean sameLevel) {
       STACK_TRACE_AT_MESSAGE_LEVEL = sameLevel;
   }

   /**
    * Indicates whether the stack trace should be displayed at the same level
    * as the message.
    *
    * @return
    *    <code>true</code> if the stack trace should be at the same level,
    *    <code>false</code> if the stack trace should be at DEBUG level.
    */
   public static boolean isStackTraceAtMessageLevel() {
       return STACK_TRACE_AT_MESSAGE_LEVEL;
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
