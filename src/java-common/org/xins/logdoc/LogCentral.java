/*
 * $Id$
 */
package org.xins.logdoc;

import java.util.ArrayList;
import java.util.List;

import org.xins.util.MandatoryArgumentChecker;

/**
 * Central class for <em>logdoc</em> logging.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class LogCentral
extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * All registered <code>LogController</code> instances.
    *
    * @see #registerLog(LogController)
    */
   private static final List CONTROLLERS = new ArrayList();


   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Registers the specified <code>LogController</code>, which represents a
    * <em>logdoc</em> <code>Log</code> class.
    *
    * @param controller
    *    the {@link LogController}, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>controller == null</code>.
    */
   static final void registerLog(AbstractLog.LogController controller)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("controller", controller);

      // Add the controller to the List
      CONTROLLERS.add(controller);
   }

   /**
    * Sets the locale on all <em>logdoc</em> <code>Log</code> classes.
    *
    * @param newLocale
    *    the new locale, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>newLocale == null</code>.
    */
   public static final void setLocale(String newLocale)
   throws IllegalArgumentException {

     // Check preconditions
     MandatoryArgumentChecker.check("newLocale", newLocale);

     // TODO: Call setLocale on all LogController instances
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
