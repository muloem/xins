/*
 * $Id$
 */
package org.xins.logdoc;

import org.apache.log4j.NDC;

/**
 * Central class for <em>logdoc</em> logging.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 */
public final class LogCentral
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

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
    * Boolean that indicates that an AbstractLog that did not supported the
    * locale tried to register.
    */
   private static boolean CONSISTENT = true;


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
    */
   static final void registerLog(AbstractLog.LogController controller)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("controller", controller);

      // When the first LogController registers, set the locale.
      if (LOCALE == null) {
         String defaultLocale = System.getProperty("org.xins.server.locale");
         if (defaultLocale == null || defaultLocale.equals("")) {
            LOCALE = "en_US";
         } else {
            LOCALE = defaultLocale;
         }
      }

      if (!controller.isLocaleSupported(LOCALE)) {
         CONSISTENT = false;
         //throw new UnsupportedLocaleException(LOCALE);
      } else {
         controller.setLocale(LOCALE);
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
    * Returns the current diagnostic context identifier.
    *
    * @return
    *    the current diagnostic context identifier, or <code>null</code> if
    *    there is none.
    */
   public static final String getContext() {
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
   public static final void setLocale(String newLocale)
   throws IllegalArgumentException, UnsupportedLocaleException {

      // Check preconditions
      MandatoryArgumentChecker.check("newLocale", newLocale);

      // Make sure the locale is supported by all controllers
      int size = CONTROLLERS.length;
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
    * Get the locale set in this LogCentral.
    *
    * @return
    *    the locale as set for java properties file (e.g. "en_US").
    */
   public static final String getLocale() {
      return LOCALE;
   }

   /**
    * Indicates if the registered Logs support the same locale.
    *
    * @return
    *    <code>true</code> if all Logs support the locale set in the LogCentral,
    *    <code>false</code> otherwise.
    */
   public static final boolean isConsistent() {
      return CONSISTENT;
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
