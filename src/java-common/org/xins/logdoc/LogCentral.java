/*
 * $Id$
 */
package org.xins.logdoc;

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

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

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
