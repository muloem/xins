/*
 * $Id$
 */
package org.xins.common;

/**
 * Utility functions related to exceptions.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:ernst.dehaan@nl.wanadoo.com">ernst.dehaan@nl.wanadoo.com</a>)
 *
 * @since XINS 0.195
 */
public final class ExceptionUtils extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Determines the root cause for the specified exception.
    *
    * @param exception
    *    the exception to determine the root cause for, cannot be
    *    <code>null</code>.
    *
    * @return
    *    the root cause exception, never <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>exception == null</code>.
    */
   public static final Throwable getRootCause(Throwable exception)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("exception", exception);

      // Get the root cause of the exception
      Throwable cause = exception.getCause();
      while (cause != null) {
         exception = cause;
         cause = exception.getCause();
      }

      return exception;
   }


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>ExceptionUtils</code> object.
    */
   private ExceptionUtils() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
